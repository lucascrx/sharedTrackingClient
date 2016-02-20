package com.example.sharedtracking.session;

import java.sql.Timestamp;

import android.app.PendingIntent;
import android.location.Location;
import android.util.Log;

import com.example.sharedtracking.constants.Constants;
import com.example.sharedtracking.network.EndingTimeUpdateOperationCallback;
import com.example.sharedtracking.network.NameUpdateOperationCallback;
import com.example.sharedtracking.network.PositionUpdateOperationCallback;
import com.example.sharedtracking.network.RateUpdateOperationCallback;
import com.example.sharedtracking.network.StartingTimeUpdateOperationCallback;
import com.example.sharedtracking.network.WebClient;
import com.example.sharedtracking.response.ResponseException;
import com.example.sharedtracking.response.SynchronizationResponse;
import com.example.sharedtracking.response.UpdateResponse;
import com.example.sharedtracking.views.ConstantGUI;

/**Either a session created by the user or one for which it is a contributor*/
public class HostedSession extends Session implements ISessionUpdateListener{
	
	/**log tag for debugging*/
	private static String Log_Tag = "Hosted Session : ";

	/**Session private ID*/
	private String privateID;
	/**Device Name**/
	private String deviceName;
	/**Device identifier**/
	private String deviceID;
	/**Number of uploaded Samples*/
	private int uploadedSampleNumber;
	
	/**Pending intent destined to alarm receiver*/
	private PendingIntent nextWakeUpIntent;
	/**Callback for session alarm intent update*/
	private ISessionCreationListener callbackForAlarmUpdate;
	
	
	public HostedSession(String name, String publicID,int rate, Timestamp start, Timestamp end, 
			Timestamp lastModifTime, String privateID, String devName, String devID,ISessionCreationListener callback ) {
		super(name, publicID,rate,start,end,lastModifTime);
		this.privateID=privateID;
		
		this.deviceName=devName;
		this.deviceID=devID;
		
		
		//hosted session is always initialized with Pending status
		this.status=Constants.SESSION_STATUS_PENDING;
		this.uploadedSampleNumber=0;
		
		this.callbackForAlarmUpdate = callback;
	}

	/**Triggers a sample submission to the server*/
	public void submitCurrentPosition(double latitude, double longitude){
		Log.d(Log_Tag,"session "+this.name+" submitting latitude and longitude to server");
		//request server using WebClient
		PositionUpdateOperationCallback callback = new PositionUpdateOperationCallback(this);
		WebClient.uploadPosition(privateID, latitude, longitude, deviceName, deviceID, callback);
	}

	/**Callback method called in response to sample submitting to server*/
	@Override
	public void onPositionUpdated(UpdateResponse response) {
		boolean hasSessionChanged = false;
		try{
			if(response==null){
				Log.d(Log_Tag,"session "+this.name+" : upload position response is empty");
				//server unreachable : setting status to NOT CONNECTED
				if(this.status!=Constants.SESSION_STATUS_NOT_CONNECTED){
					this.status=Constants.SESSION_STATUS_NOT_CONNECTED;
					hasSessionChanged=true;
				}
				throw new ResponseException("Null UpdateResponse Object : from UpdateResponse after position update");
			}
			boolean isSynchro = response.getOperationStatus();
			Timestamp newlastmodif = response.getLastModificationTime();
			if(newlastmodif==null){
				Log.d(Log_Tag,"session "+this.name+" : session not resolved from upload postion on server");
				//Position Upload has not been processed correctly on server.
				//set Session to unknown
				if(this.status!=Constants.SESSION_STATUS_UNKNOWN){
					this.status=Constants.SESSION_STATUS_UNKNOWN;
					hasSessionChanged=true;
				}
				//operation went wrong on server
				throw new ResponseException("Session not resolved from UpdateResponse after position update");
			}
			//Otherwise: session has been retrieved, checking synchronization
			if(isSynchro){
				Log.d(Log_Tag,"session "+this.name+" : performed sucessful position upload on server, synchronized" );
				//setting status to RUNNING
				if(this.status!=Constants.SESSION_STATUS_RUNNING){
					Log.d(Log_Tag,"Session "+this.name+" must be displayed as running" );
					this.status=Constants.SESSION_STATUS_RUNNING;
					hasSessionChanged=true;
				}
				//incrementing uploaded sample number
				this.uploadedSampleNumber++;
				hasSessionChanged=true;
				//check for metadata refresh
				if(newlastmodif.after(this.getLastModifTime())){
					//if last modification time has changed since last refresh, perform a new refresh:
					Log.d(Log_Tag,"session "+this.name+" : is not up to date (detection from position update), performing refresh");
					this.getNewMetadata();
				}else{
					//else no refresh needed
					Log.d(Log_Tag,"session "+this.name+" : is up to date (detection from position update), no need for refresh");
				}	
			}else{
				//synchronization error
				Log.d(Log_Tag,"session "+this.name+" : performed not synchronized sample upload on server" );
				//check for resynchronization
				if(newlastmodif.after(this.getLastModifTime())){
					//a new synchronization must be done, set counter to null
					this.desynchronizationCounter=0;
					//if last modification time has changed since last refresh, perform a new refresh:
					Log.d(Log_Tag,"session "+this.name+" : Desynchronization comes from outdated session parameters, performing refresh");
					this.getNewMetadata();
				}else{
					Log.d(Log_Tag,"session "+this.name+" : Desynchronization whiles session parameters are up to date : problem");
					//session keeps sending not synchronized messages while lastUpdateDate is up to date
					//receiving it once can be due to network latency, but twice or more is an issue
					if(desynchronizationCounter>=Constants.MAX_DESYNCHRONIZATION_NUMBER_BEFORE_UNKNOWN){
						//if max number reached, setting status to unknown
						if(this.status!=Constants.SESSION_STATUS_UNKNOWN){
							this.status=Constants.SESSION_STATUS_UNKNOWN;
							hasSessionChanged=true;
						}
					}else{
						desynchronizationCounter++;
					}				
				}
			}
		}catch(ResponseException e){
			//No need to callback GUI for periodic activities
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
	
	/**Triggers a session upload rate update on server, changes are taken in account
	 * locally after server response*/
	public void updateUploadRate(int newRate){
		Log.d(Log_Tag,"session "+this.name+" upload rate update triggered");
		//request server using WebClient
		RateUpdateOperationCallback callback = new RateUpdateOperationCallback(this);
		WebClient.updateRate(privateID, newRate, callback);
	}

	/**Callback method called in response to rate update on server
	 * not subject to desynchronization*/
	@Override
	public void onRateUpdated(UpdateResponse response) {
		try{
			if(response==null){
				Log.d(Log_Tag,"session "+this.name+" : rate update response is empty");
				//punctual operation status do not have any impact on Session status
				
				//GUI notification for network issue
				if(this.callbackGUI!=null){
					Log.d(Log_Tag,"session "+this.name+" : is under focus : updating GUI" );
					this.callbackGUI.onNetworkIssueEncountered();
				}else{
					Log.d(Log_Tag,"session "+this.name+" : isn't under focus : no GUI update" );
				}
				throw new ResponseException("Null UpdateResponse Object : from UpdateResponse after rate update");
			}
			if(!response.getOperationStatus()){
				Log.d(Log_Tag,"session "+this.name+" : false operation status from update rate on server");
				//punctual operation status do not have any impact on Session status
				//GUI notification for network issue
				if(this.callbackGUI!=null){
					Log.d(Log_Tag,"session "+this.name+" : is under focus : updating GUI" );
					this.callbackGUI.onFailStatusReturnedOperation(ConstantGUI.TOAST_LABEL_FOR_RATE_UPDATE);
				}else{
					Log.d(Log_Tag,"session "+this.name+" : isn't under focus : no GUI update" );
				}
				throw new ResponseException("False Operation Status from UpdateResponse after rate update");
			}
			//punctual operation status do not have any impact on Session status
			Log.d(Log_Tag,"session "+this.name+" : rate update success");
			if(this.callbackGUI!=null){
				Log.d(Log_Tag,"session "+this.name+" : is under focus : updating GUI" );
				this.callbackGUI.onSuccessStatusReturnedOperation(ConstantGUI.TOAST_LABEL_FOR_RATE_UPDATE);
			}else{
				Log.d(Log_Tag,"session "+this.name+" : isn't under focus : no GUI update" );
			}
	
		}catch(ResponseException e){
			//GUI callback done before throwing exception
			e.printStackTrace();
		}
		//Session Refresh is triggered regardless to the received last modification time
		Log.d(Log_Tag,"session "+this.name+" : performing refresh after rate update");
		this.getNewMetadata();
	}

	/**Triggers a session name update on server, changes are taken in account
	 * locally after server response*/
	public void updateName(String newName){
		Log.d(Log_Tag,"session "+this.name+" name update triggered");
		//request server using WebClient
		NameUpdateOperationCallback callback = new NameUpdateOperationCallback(this);
		WebClient.updateName(privateID, newName, callback);
	}
	
	/**Callback method called in response to name update on server
	 * not subject to desynchronization*/
	@Override
	public void onNameUpdated(UpdateResponse response) {
		try{
			if(response==null){
				Log.d(Log_Tag,"session "+this.name+" : name update response is empty");
				//punctual operation status do not have any impact on Session status
				
				//GUI notification for network issue
				if(this.callbackGUI!=null){
					Log.d(Log_Tag,"session "+this.name+" : is under focus : updating GUI" );
					this.callbackGUI.onNetworkIssueEncountered();
				}else{
					Log.d(Log_Tag,"session "+this.name+" : isn't under focus : no GUI update" );
				}
				throw new ResponseException("Null UpdateResponse Object : from UpdateResponse after name update");
			}
			if(!response.getOperationStatus()){
				Log.d(Log_Tag,"session "+this.name+" : false operation status from update name on server");
				//punctual operation status do not have any impact on Session status
				//GUI notification for network issue
				if(this.callbackGUI!=null){
					Log.d(Log_Tag,"session "+this.name+" : is under focus : updating GUI" );
					this.callbackGUI.onFailStatusReturnedOperation(ConstantGUI.TOAST_LABEL_FOR_NAME_UPDATE);
				}else{
					Log.d(Log_Tag,"session "+this.name+" : isn't under focus : no GUI update" );
				}
				throw new ResponseException("False Operation Status from UpdateResponse after name update");
			}
			//punctual operation status do not have any impact on Session status
			Log.d(Log_Tag,"session "+this.name+" : name update success");
			if(this.callbackGUI!=null){
				Log.d(Log_Tag,"session "+this.name+" : is under focus : updating GUI" );
				this.callbackGUI.onSuccessStatusReturnedOperation(ConstantGUI.TOAST_LABEL_FOR_NAME_UPDATE);
			}else{
				Log.d(Log_Tag,"session "+this.name+" : isn't under focus : no GUI update" );
			}		
		}catch(ResponseException e){
			//GUI callback done before throwing exception
			e.printStackTrace();
		}
		//Session Refresh is triggered regardless to the received last modification time
		Log.d(Log_Tag,"session "+this.name+" : performing refresh after name update");
		this.getNewMetadata();
	}

	/**Triggers a session starting time update on server, changes are taken in account
	 * locally after server response*/
	public void updateStartingTime(String start){
		Log.d(Log_Tag,"session "+this.name+" starting time update triggered");
		//request server using WebClient
		StartingTimeUpdateOperationCallback callback = new StartingTimeUpdateOperationCallback(this);
		WebClient.updateStartingTime(privateID, start, callback);
	}
	
	/**Callback method called in response to starting time update on server*/
	@Override
	public void onStartingTimeUpdated(UpdateResponse response) {
		try{
			if(response==null){
				Log.d(Log_Tag,"session "+this.name+" : starting time update response is empty");
				//punctual operation status do not have any impact on Session status
				
				//GUI notification for network issue
				if(this.callbackGUI!=null){
					Log.d(Log_Tag,"session "+this.name+" : is under focus : updating GUI" );
					this.callbackGUI.onNetworkIssueEncountered();
				}else{
					Log.d(Log_Tag,"session "+this.name+" : isn't under focus : no GUI update" );
				}
				throw new ResponseException("Null UpdateResponse Object : from UpdateResponse after starting time update");
			}
			if(!response.getOperationStatus()){
				Log.d(Log_Tag,"session "+this.name+" : false operation status from update starting time on server");
				//punctual operation status do not have any impact on Session status
				//GUI notification for network issue
				if(this.callbackGUI!=null){
					Log.d(Log_Tag,"session "+this.name+" : is under focus : updating GUI" );
					this.callbackGUI.onFailStatusReturnedOperation(ConstantGUI.TOAST_LABEL_FOR_STARTING_TIME_UPDATE);
				}else{
					Log.d(Log_Tag,"session "+this.name+" : isn't under focus : no GUI update" );
				}
				throw new ResponseException("False Operation Status from UpdateResponse after starting time update");
			}
			//punctual operation status do not have any impact on Session status
			Log.d(Log_Tag,"session "+this.name+" : starting time update success");
			if(this.callbackGUI!=null){
				Log.d(Log_Tag,"session "+this.name+" : is under focus : updating GUI" );
				this.callbackGUI.onSuccessStatusReturnedOperation(ConstantGUI.TOAST_LABEL_FOR_STARTING_TIME_UPDATE);
			}else{
				Log.d(Log_Tag,"session "+this.name+" : isn't under focus : no GUI update" );
			}
		}catch(ResponseException e){
			//GUI callback done before throwing exception
			e.printStackTrace();
		}
		//Session Refresh is triggered regardless to the received last modification time
		Log.d(Log_Tag,"session "+this.name+" : performing refresh after starting time update");
		this.getNewMetadata();
	}
	
	/**Triggers a session ending time update on server, changes are taken in account
	 * locally after server response*/
	public void updateEndingTime(String end){
		Log.d(Log_Tag,"session "+this.name+" ending time update triggered");
		//request server using WebClient
		EndingTimeUpdateOperationCallback callback = new EndingTimeUpdateOperationCallback(this);
		WebClient.updateEndingTime(privateID, end, callback);
	}

	/**Callback method called in response to ending time update on server*/
	@Override
	public void onEndingTimeUpdated(UpdateResponse response) {
		try{
			if(response==null){
				Log.d(Log_Tag,"session "+this.name+" : ending time update response is empty");
				//punctual operation status do not have any impact on Session status
				
				//GUI notification for network issue
				if(this.callbackGUI!=null){
					Log.d(Log_Tag,"session "+this.name+" : is under focus : updating GUI" );
					this.callbackGUI.onNetworkIssueEncountered();
				}else{
					Log.d(Log_Tag,"session "+this.name+" : isn't under focus : no GUI update" );
				}
				throw new ResponseException("Null UpdateResponse Object : from UpdateResponse after ending time update");
			}
			if(!response.getOperationStatus()){
				Log.d(Log_Tag,"session "+this.name+" : false operation status from update ending time on server");
				//punctual operation status do not have any impact on Session status
				//GUI notification for network issue
				if(this.callbackGUI!=null){
					Log.d(Log_Tag,"session "+this.name+" : is under focus : updating GUI" );
					this.callbackGUI.onFailStatusReturnedOperation(ConstantGUI.TOAST_LABEL_FOR_ENDING_TIME_UPDATE);
				}else{
					Log.d(Log_Tag,"session "+this.name+" : isn't under focus : no GUI update" );
				}
				throw new ResponseException("False Operation Status from UpdateResponse after ending time update");
			}
			//punctual operation status do not have any impact on Session status
			Log.d(Log_Tag,"session "+this.name+" : ending time update success");
			if(this.callbackGUI!=null){
				Log.d(Log_Tag,"session "+this.name+" : is under focus : updating GUI" );
				this.callbackGUI.onSuccessStatusReturnedOperation(ConstantGUI.TOAST_LABEL_FOR_ENDING_TIME_UPDATE);
			}else{
				Log.d(Log_Tag,"session "+this.name+" : isn't under focus : no GUI update" );
			}
		}catch(ResponseException e){
			//GUI callback done before throwing exception
			e.printStackTrace();
		}
		//Session Refresh is triggered regardless to the received last modification time
		Log.d(Log_Tag,"session "+this.name+" : performing refresh after ending time update");
		this.getNewMetadata();
	}
	


	/**When the device has resolved current location upon session request*/
	@Override
	public void newLocationResolved(Location loc) {
		//submit location to server
		double latitude = loc.getLatitude();
		double longitude = loc.getLongitude();

		submitCurrentPosition(latitude,longitude);			
		
	}
	
	
	public int getSubmittedSampleNumber(){
		return this.uploadedSampleNumber;
	}
	
	public void setAlarmIntent(PendingIntent intent) {
		this.nextWakeUpIntent = intent;
	}
	
	public PendingIntent getAlarmIntent(){
		return this.nextWakeUpIntent;
	}
	
	
	public boolean isNextUploadBeforeEnd(){
		boolean result = true;
		if(this.endingTime!=null){
			//next upload time
			java.util.Date date= new java.util.Date();
			Timestamp nextUpload = new Timestamp(date.getTime());
			nextUpload.setTime(nextUpload.getTime() + this.uploadRate);
			//comparing
			if(nextUpload.after(this.endingTime)){
				result=false;
			}	
		}
		return result;
	}

	/**After session refresh, alarms have to be updated too
	 * callback is called*/
	@Override
    public void onSessionRefreshed(SynchronizationResponse response){
    	super.onSessionRefreshed(response);
    	//calling callback for alarm update even if it not always needed
    	this.callbackForAlarmUpdate.onAlarmUpdateRequired(this);
    }
	
	
}
