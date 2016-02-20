package com.example.sharedtracking.session;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import android.location.Location;
import android.util.Log;

import com.example.sharedtracking.constants.Constants;
import com.example.sharedtracking.network.ReadingOperationCallback;
import com.example.sharedtracking.network.WebClient;
import com.example.sharedtracking.response.ReadingResponse;
import com.example.sharedtracking.response.ResponseException;
import com.example.sharedtracking.types.Sample;
import com.example.sharedtracking.types.SampleList;

/**Tracking session joined by the user*/
public class JoinedSession extends Session implements ISessionReadingListener {
	
	/**log tag for debugging*/
	private static String Log_Tag = "Joined Session : ";
	
	/**lists of samples fetched by the session, sorted by deviceID*/
	private HashMap<String, ArrayList<Sample>> fetchedSamples;
	/**Counter of empty fetching*/
	private int emptySampleCounter;
	
	public JoinedSession(String publicID) {
		super(publicID);
		fetchedSamples = new HashMap<String, ArrayList<Sample>>();
		//hosted session is always initialized with Unknown status
		this.status=Constants.SESSION_STATUS_UNKNOWN;
		this.emptySampleCounter=0;
	}
	
	/**Triggers a session reading on the server*/
	public void getNewPositions(){
		//request server using WebClient
		ReadingOperationCallback callback = new ReadingOperationCallback(this);
		WebClient.readPosition(publicID, getReceivedSampleNumber()+1	, callback);

	}

	/**Callback method called in response to sample reading on server*/
	@Override
	public void onSessionRead(ReadingResponse response) {
		boolean hasSessionChanged = false;
		try{
			if(response==null){
				Log.d(Log_Tag,"session "+this.name+" : session reading response is empty");
				//server unreachable : setting status to NOT CONNECTED
				if(this.status!=Constants.SESSION_STATUS_NOT_CONNECTED){
					this.status=Constants.SESSION_STATUS_NOT_CONNECTED;
					hasSessionChanged=true;
				}
				throw new ResponseException("Null UpdateResponse Object : from UpdateResponse after session reading");
			}
			boolean isSynchro = response.getOperationStatus();
			SampleList list = response.getSamples();
			if(list == null){
				//session hasn't been resolved
				Log.d(Log_Tag,"session "+this.name+" : session not resolved from session reading on server");
				//Session Reading has not been processed correctly on server.
				//set Session to unknown
				if(this.status!=Constants.SESSION_STATUS_UNKNOWN){
					this.status=Constants.SESSION_STATUS_UNKNOWN;
					hasSessionChanged=true;
				}
				//operation went wrong on server
				throw new ResponseException("Session Not Resolved from UpdateResponse after session reading");
			}
			//Otherwise: session has been retrieved
			//retrieving sample and last modification time from SampleList
			Timestamp newLastModifTime = list.getLastModificationTime();
			if(newLastModifTime==null){
				throw new ResponseException("Null last mofification timestamp within SampleList object from ReadingResponse");
			}
			//Checking synchronization
			if(isSynchro){
				Log.d(Log_Tag,"session "+this.name+" : performed sucessful reading on server (synchronized)" );
				//session is synchronized : set desynchronization counter to null
				this.desynchronizationCounter=0;
				//retrieving SampleList object
				ArrayList<Sample> samples = list.getSamples();
				if(samples==null){
					throw new ResponseException("Null attribute(s) within SampleList object from ReadingResponse");
				}
				//adding retrieved sample to the list
				int size = samples.size();
				Log.d(Log_Tag,"session "+this.name+" : has read "+size+" new sample(s)");
				hasSessionChanged = updateEmptySampleReceivedCounter(size);					
				for(Sample sample : samples){
				    // fetch the list for this object's id
					String devID = sample.getDeviceID();
				    ArrayList<Sample> temp = this.fetchedSamples.get(devID);
				    if(temp == null){
				        // if the list is null we haven't seen an object with this id before, so create 
				        // a new list
				        temp = new ArrayList<Sample>();
				        // and add it to the map
				        this.fetchedSamples.put(devID, temp);
				    }
				    // whether we got the list from the map
				    // or made a new one we need to add our
				    // object.
				    temp.add(sample);
					hasSessionChanged=true;
				}
				Log.d(Log_Tag,"session "+this.name+" : updated with read samples");
				//check for metadata refresh
				if(newLastModifTime.after(this.getLastModifTime())){
					//if last modification time has changed since last refresh, perform a new refresh:
					Log.d(Log_Tag,"session "+this.name+" : is not up to date (detection from reading), performing refresh");
					this.getNewMetadata();
				}else{
					//else no refresh needed
					Log.d(Log_Tag,"session "+this.name+" : is up to date (detection from reading), no need for refresh");
				}
			}else{
				//synchronization error
				Log.d(Log_Tag,"session "+this.name+" : performed not synchronized reading on server" );
				//check for resynchronization
				if(newLastModifTime.after(this.getLastModifTime())){
					//a new synchronization must be done, set counter to null
					this.desynchronizationCounter=0;
					//if last modification time has changed since last refresh, perform a new refresh:
					Log.d(Log_Tag,"session "+this.name+" : Desynchronization comes from outdated session parameters, peforming refresh");
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


	/**When the device has resolved current location upon session request*/
	@Override
	public void newLocationResolved(Location loc) {
		// update map with location
		
	}
	
	public HashMap<String, ArrayList<Sample>> getSamples(){
		return this.fetchedSamples;
	}
	
	public int getReceivedSampleNumber(){
		int total = 0;
		for(Map.Entry<String,ArrayList<Sample>> entry : this.fetchedSamples.entrySet()){
		    total = total + entry.getValue().size();
		}
		return total;
	}
	
	/**When a given number of empty sample list is reached the joined session
	 * moves to unknown state : indeed the tracked device is no more updating
	 * its location as expected.
	 * This function handles the counter of empty samples and updates session
	 * state
	 * @return if session has changed or not*/
	public boolean updateEmptySampleReceivedCounter(int receivedListSize){
		boolean hasSessionChanged=false;
		if(receivedListSize==0){
			//if 0 samples retrieved, incremented empty fetching counter when maximum is reached, turning state to unknown
			if(this.emptySampleCounter>=Constants.MAX_EMPTY_FETCHING_NUMBER_BEFORE_UNKNOWN){
				if(this.status!=Constants.SESSION_STATUS_UNKNOWN){
					Log.d(Log_Tag,"Session "+this.name+" must be displayed as unknown : too many empty samples received" );
					this.status=Constants.SESSION_STATUS_UNKNOWN;
					hasSessionChanged=true;		
				}
			}else{
				this.emptySampleCounter++;
			}
		}else{
			//otherwise, counter set back to 0
			this.emptySampleCounter=0;
			//setting status to RUNNING
			if(this.status!=Constants.SESSION_STATUS_RUNNING){
				Log.d(Log_Tag,"Session "+this.name+" must be displayed as running" );
				this.status=Constants.SESSION_STATUS_RUNNING;
				hasSessionChanged=true;
			}	
		}
		return hasSessionChanged;
	}
	
	
	
}
