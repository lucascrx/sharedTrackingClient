package com.example.sharedtracking.session;

import java.sql.Timestamp;

import android.location.Location;
import android.os.Parcel;
import android.util.Log;

import com.example.sharedtracking.ObjectChangingCallback;
import com.example.sharedtracking.constants.Constants;
import com.example.sharedtracking.network.SessionRefreshCallback;
import com.example.sharedtracking.network.WebClient;
import com.example.sharedtracking.response.ResponseException;
import com.example.sharedtracking.response.SynchronizationResponse;
import com.example.sharedtracking.types.SessionMetaData;


/**Tracking Session either read or updated by the user*/
public abstract class Session implements ISessionRefreshListener{
	
	/**Log Tag for debugging purposes*/
	private final static String Log_Tag = "Session : ";
	
	/**Session Name*/
	protected String name;
	/**Session Public ID*/
	protected String publicID;
	/**update rate*/
	protected int uploadRate;
	/**starting time*/
	protected Timestamp startingTime;
	/**ending time**/
	protected Timestamp endingTime;
	/**last modification time*/
	protected Timestamp lastModifTime;
	
	/**Status for GUI display*/
	protected int status;
	/**Callback update for GUI update*/
	protected ObjectChangingCallback callbackGUI;
	
	/**Counter for desynchronized requests:
	 * when hosted and joined session send request not compatible with session current time frame
	 * when a max value (2 since the first desynchronized triggers desynchronization) session
	 * state is moved to UNKNOWN*/
	protected int desynchronizationCounter;
	
	
	///**web client to access the internet*/
	//private WebClient client;
	
	/**Creator for hosted sessions*/
	public Session(String name, String publicID,int rate, Timestamp start, Timestamp end, Timestamp lastModif) {
		this.name = name;
		this.publicID = publicID;
		this.uploadRate = rate;
		this.startingTime = start;
		this.endingTime = end;
		this.lastModifTime = lastModif;
		this.desynchronizationCounter=0;
	}
	
	/**Creator for joined sessions*/
	public Session(String publicID){
		this.publicID=publicID;
		this.desynchronizationCounter=0;
	}
	
	/**When the device has resolved current location upon session request
	 * Joined session at a time may implement it has well in order to resolve device current position*/
	public abstract void newLocationResolved(Location loc);

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPublicID() {
		return publicID;
	}

	public void setPublicID(String publicID) {
		this.publicID = publicID;
	}

	public int getUploadRate() {
		return uploadRate;
	}

	public void setUploadRate(int uploadRate) {
		this.uploadRate = uploadRate;
	}

	public Timestamp getStartingTime() {
		return startingTime;
	}

	public void setStartingTime(Timestamp startingTime) {
		this.startingTime = startingTime;
	}

	public Timestamp getEndingTime() {
		return endingTime;
	}

	public void setEndingTime(Timestamp endingTime) {
		this.endingTime = endingTime;
	}
	
	public Timestamp getLastModifTime() {
		return lastModifTime;
	}

	public void setLastModifTime(Timestamp endingTime) {
		this.lastModifTime = endingTime;
	}
	
	public ObjectChangingCallback getCallbackGUI(){
		return this.callbackGUI;
	}
	
	public void setCallbackGUI(ObjectChangingCallback callback){
		this.callbackGUI = callback;
	}
	
	public int getStatus(){
		return this.status;
	}
	
	public void setStatus(int s){
		this.status=s;
	}

	/**Triggers a session refresh from the server*/
	public void getNewMetadata(){
		//request server using WebClient
		SessionRefreshCallback callback = new SessionRefreshCallback(this);
		WebClient.refreshSession(publicID, callback);
	}
	
	public void onSessionRefreshed(SynchronizationResponse response){
		boolean hasSessionChanged = false;
		try{
			if(response==null){
				Log.d(Log_Tag,"session "+this.name+" : session synchronization response from refresh is empty");
				//server unreachable : setting status to NOT CONNECTED
				if(this.status!=Constants.SESSION_STATUS_NOT_CONNECTED){
					this.status=Constants.SESSION_STATUS_NOT_CONNECTED;
					hasSessionChanged=true;
				}
				throw new ResponseException("Null Synchronization Response Object : from SynchronizationResponse after session refresh");
			}
			if(!response.getOperationStatus()){
				Log.d(Log_Tag,"session "+this.name+" : false operation status from session refresh on server");
				//Session refresh has not been processed correctly on server.
				//set Session to unknown
				if(this.status!=Constants.SESSION_STATUS_UNKNOWN){
					this.status=Constants.SESSION_STATUS_UNKNOWN;
					hasSessionChanged=true;
				}
				//operation went wrong on server
				throw new ResponseException("False Operation Status from SynchronizationResponse after session refresh");
			}
			//Otherwise: refresh successfully performed on server
			//session parameters setting doesn't affect session status
			
			//retrieving Metadata object
			Log.d(Log_Tag,"session "+this.name+" : performed sucessful refresh on server" );
			SessionMetaData metadata = response.getMetadata();
			if(metadata==null){
				throw new ResponseException("Null MetaData object from SynchronizationResponse");
			}
			String newName = metadata.getName();
			int newRate = metadata.getRate();
			Timestamp newStartTime = metadata.getStartTime();
			Timestamp newEndTime = metadata.getEndTime();
			Timestamp newLastmodifTime = metadata.getLastModificationTime();
			if(newName==null || newRate ==0||newStartTime==null || newLastmodifTime==null ){
				//Only end time can be null
				throw new ResponseException("Null parameter(s) within MetaData object from SynchronizationResponse");
			}
			
			//updating session Metadata with the received fields
			this.name = newName;
			this.uploadRate = newRate;
			this.startingTime = newStartTime;
			this.endingTime = newEndTime;
			this.lastModifTime = newLastmodifTime;
			Log.d(Log_Tag,"session "+this.name+" : refreshed with new parameters" );
			hasSessionChanged=true;

		}catch(ResponseException e){
			e.printStackTrace();
		}
		//At the end, if session has changed, calling GUI for refresh
		if(hasSessionChanged){
			if(this.callbackGUI!=null){
				//session get the focus, GUI must be updated
				Log.d(Log_Tag,"session "+this.name+" : is under focus : updating GUI" );
				this.callbackGUI.onObjectChanged();
			}else{
				//session not under focus no GUI to be updated
				Log.d(Log_Tag,"session "+this.name+" : isn't under focus : no GUI update" );
			}
		}
	}


	/**Called by locationResolver when device location can't be resolved,
	 * moves session to not connected state.*/
	public void unresolvedLocation(){
		Log.d(Log_Tag,"session "+this.name+" : can't have its location resolved");
		//server unreachable : setting status to NOT CONNECTED
		if(this.status!=Constants.SESSION_STATUS_NOT_LOCALIZED){
			this.status=Constants.SESSION_STATUS_NOT_LOCALIZED;
			this.callbackGUI.onObjectChanged();
		}
	}

	
	
	
	//Parcelable part
	
/**All the attributes that can be null are written as Value
 * callback attribute is not written*/
    public void writeToParcel(Parcel out, int flags) {
        out.writeValue(name);
    	out.writeString(publicID);
    	out.writeValue(uploadRate);
    	out.writeValue(startingTime);
    	out.writeValue(endingTime);
    	out.writeValue(lastModifTime);
    	out.writeInt(status);
    }

    protected Session(Parcel in) {
        name = (String) in.readValue(String.class.getClassLoader());
        publicID = in.readString();
        uploadRate = (int) in.readValue(int.class.getClassLoader());
        startingTime = (Timestamp) in.readValue(Timestamp.class.getClassLoader());
        endingTime = (Timestamp) in.readValue(Timestamp.class.getClassLoader());
        lastModifTime = (Timestamp) in.readValue(Timestamp.class.getClassLoader());
        status = in.readInt();
    }

}
