package com.example.sharedtracking.session;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.ArrayList;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import com.example.sharedtracking.ObjectChangingCallback;
import com.example.sharedtracking.background.AlarmReceiver;
import com.example.sharedtracking.background.LocationAsynchronousResolver;
import com.example.sharedtracking.constants.Constants;
import com.example.sharedtracking.network.CreationOperationCallback;
import com.example.sharedtracking.network.WebClient;
import com.example.sharedtracking.response.CreationResponse;
import com.example.sharedtracking.response.ResponseException;
import com.example.sharedtracking.views.ConstantGUI;

/**Handles the different Tracking Sessions set up by the user*/
public class SessionManager implements ISessionCreationListener, Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	

	/**Log Tag for debugging purposes*/
	private final static String Log_Tag = "Session Manager : ";
	
	/**Device identifiers*/
	private String deviceName;
	private String deviceID;
	/**Active Sessions List*/
	private ArrayList<Session> sessionList;
	
	/**Callback for GUI update*/
	private ObjectChangingCallback callbackGUI;
	
	/**Location helper*/
	private static LocationAsynchronousResolver locationHelper = new LocationAsynchronousResolver();
	
	/**Alarm period in sec*/
	private int alarmPeriod;
	/**counter for session rate*/
	private int counter;
	
	/**Context for Location : the context is the activity in foreground*/
	private Context context;

	public SessionManager(Context ctx, String name, String devID, int period){
		this.sessionList = new ArrayList<Session>();
		this.deviceName=name;
		this.deviceID=devID;
		this.alarmPeriod=period;
		this.counter=0;
		this.context=ctx;
	}
	

	
	public void wakeUp(){
		Log.d(Log_Tag,"manager woken up by the Main Service, checking for session to update, counter : "+counter );
		Log.d(Log_Tag,"manager has "+this.sessionList.size()+" sessions to check");
		this.counter++;
		int time = counter*alarmPeriod;
		Log.d(Log_Tag,"current time : "+time );
		
		java.util.Date date= new java.util.Date();
		Timestamp now = new Timestamp(date.getTime());
		Timestamp start;
		Timestamp end;
		for (Session session : sessionList){
			//checking if current time is between session starting and ending times 
			start = session.getStartingTime();
			end = session.getEndingTime();
			if(start!=null&&now.after(start)){
				if(end==null||now.before(end)){
					//waking up sessions matching upload period : uploading data
					if(session.getUploadRate()!=0 && (time % session.getUploadRate())==0 ){
						//only joined session are handled synchronously
						if(session instanceof JoinedSession){
							Log.d(Log_Tag,"new joined session detected for update" );
							//matching with upload period : uploading data
							((JoinedSession)session).getNewPositions();
							Log.d(Log_Tag,"joined session "+session.getName()+" has fetched new position result from internet" );
						}			
					}
					
				}else if(now.after(end)){
					//setting DONE status
					if(session.getStatus()!=Constants.SESSION_STATUS_DONE){
						Log.d(Log_Tag,"Session "+session.getName()+" must be displayed as done" );
						session.setStatus(Constants.SESSION_STATUS_DONE);
						if(this.callbackGUI!=null){
							//session get the focus, GUI must be updated
							Log.d(Log_Tag,"manager : is under focus : updating GUI" );
							this.callbackGUI.onObjectChanged();
						}else{
							//session not under focus no GUI to be updated
							Log.d(Log_Tag,"manager : isn't under focus : no GUI update" );
						}
					}
				}
			}else if(start!=null&&now.before(start)){
				//setting PENDING status
				if(session.getStatus()!=Constants.SESSION_STATUS_PENDING){
					Log.d(Log_Tag,"Session "+session.getName()+" must be displayed as pending" );
					session.setStatus(Constants.SESSION_STATUS_PENDING);
					if(this.callbackGUI!=null){
						//session get the focus, GUI must be updated
						Log.d(Log_Tag,"manager : is under focus : updating GUI" );
						this.callbackGUI.onObjectChanged();
					}else{
						//session not under focus no GUI to be updated
						Log.d(Log_Tag,"manager : isn't under focus : no GUI update" );
					}
				}
			}
		}
	}
	
	public void setAlarmPeriod(int newPeriod){
		this.alarmPeriod=newPeriod;
	}
	
	public ArrayList<Session> getSessionList(){
		return this.sessionList;
	}
	
	public void setCallbackGUI(ObjectChangingCallback callback){
		this.callbackGUI = callback;
	}
	
	public void setContext(Context ctx){
		this.context = ctx;
	}
	
	public LocationAsynchronousResolver getLocationResolver(){
		return SessionManager.locationHelper;
	}
	
	/**Create a new (hosted) immediate session tracking user's position*/
	public void createNewImmediateSession(String name, int rate){
		CreationOperationCallback callback = new CreationOperationCallback(this);
		WebClient.createSession(name, rate, callback);
	}
	
	/**Create a new (hosted) prepared session tracking user's position*/
	public void createNewPreparedSession(String name, String publicID, String password, 
					String start, String end, int rate){
		CreationOperationCallback callback = new CreationOperationCallback(this);
		WebClient.createSession(name, publicID, password, rate, start, end, callback);
	}
	
	/**Create a new hosted session through contribution request*/
	public void contributeToExistingSession(String publicID, String password){
		CreationOperationCallback callback = new CreationOperationCallback(this);
		WebClient.contributeToSession(publicID, password, callback);
	}
	
	/**Join an existing session, creation is not network based here*/
	public void joinExistingSession(String publicID){
		try{
			//creating new joined session object
			JoinedSession joinedSession = new JoinedSession(publicID);
			checkifJoinedSessionExists(joinedSession.getPublicID());
			//adding it to the global list
			this.sessionList.add(joinedSession);
			//fetching session information
			joinedSession.getNewMetadata();
			
			//calling GUI for refresh
			if(this.callbackGUI!=null){
				//session get the focus, GUI must be updated 
				Log.d(Log_Tag,"manager : is under focus : updating GUI" );
				this.callbackGUI.onObjectChanged();
				//setting new callback on the new session
				joinedSession.setCallbackGUI(this.callbackGUI);
			}else{
				//session not under focus no GUI to be updated
				Log.d(Log_Tag,"manager : isn't under focus : no GUI update" );
			}
		}catch(SessionExistingException e){
			Toast.makeText(this.context, ConstantGUI.TOAST_LABEL_PARAMETER_UPDATE_FAILURE+ConstantGUI.TOAST_LABEL_EXISTING_JOINED_SESSION, Toast.LENGTH_LONG).show();
		}
	}
	
	/**Same callback used for immediate creation, prepared creation and contribution
	 * as same behavior at the end : HostedSession creation*/
	@Override
	public void onHostedSessionCreated(CreationResponse response) {
		boolean hasManagerChanged = false;
		this.callbackGUI.onHostedSessionCreated();
		try{
			if(response==null){
				Log.d(Log_Tag,"creation response from server is empty");
				//GUI notification for network issue
				if(this.callbackGUI!=null){
					//manager got the focus, GUI must be updated
					Log.d(Log_Tag,"manager is under focus : updating GUI" );
					this.callbackGUI.onNetworkIssueEncountered();
				}else{
					//manager not under focus no GUI to be updated
					Log.d(Log_Tag,"manager isn't under focus : no GUI update" );
				}
				throw new ResponseException("Null CreationResponse Object");
			}
			if(!response.getOperationStatus()){
				Log.d(Log_Tag," false operation status from session creation on server");
				//GUI notification for network issue
				if(this.callbackGUI!=null){
					//manager got the focus, GUI must be updated
					Log.d(Log_Tag,"manager is under focus : updating GUI" );
					this.callbackGUI.onFailStatusReturnedOperation(ConstantGUI.TOAST_LABEL_FOR_SESSION_CREATION+ConstantGUI.TOAST_LABEL_FOR_TOKEN_ISSUE);
				}else{
					//manager not under focus no GUI to be updated
					Log.d(Log_Tag,"manager isn't under focus : no GUI update" );
				}
				throw new ResponseException("False Operation Status from CreationResponse after session creation");
			}
			//operation successfully performed on server
			Log.d(Log_Tag,"session creation successfuly performed on server" );
				
			//retrieve session parameters from the callback caller
			String name = response.getName();
			String publicID = response.getPublicID();
			String privateID = response.getPrivateID();
			int rate = response.getRate();
			Timestamp start = response.getStartTime();
			Timestamp end = response.getEndTime();
			Timestamp lastModif = response.getLastModifTime();	
			if(name==null || publicID ==null||privateID==null||rate==0||start==null||lastModif==null){
				//ending Time can be null
				throw new ResponseException("Null attribute(s) from CreationResponse Object");
			}
			//Local Parameters	
			String devName = this.deviceName;
			String devID = this.deviceID;
				
			//creating new hosted session object and adding it to the global list
			checkifHostedSessionExists(publicID);
			HostedSession hostedSession = new HostedSession(name,publicID,rate, start, end, lastModif,  privateID,  devName,  devID,this);
			this.sessionList.add(hostedSession);
			
			//schedule session start : calculating length between now and session starting time (it already considers latency)
			java.util.Date date= new java.util.Date();
			Timestamp now = new Timestamp(date.getTime());
			long timeBeforeStart = hostedSession.startingTime.getTime()-now.getTime();
			if(timeBeforeStart<100){
				timeBeforeStart=100;
			}
			//setting up alarms
			Log.d(Log_Tag,"Hosted session creation: scheduling New Alarm" );
			setAlarmforNextUpdate(hostedSession,timeBeforeStart);
			
			hasManagerChanged = true;
			Log.d(Log_Tag,"session creation success : New Hosted session added to the list" );

				//calling GUI for refresh
				if(this.callbackGUI!=null){
					//session get the focus, GUI must be updated
					Log.d(Log_Tag,"manager : is under focus : updating GUI" );
					this.callbackGUI.onObjectChanged();
					this.callbackGUI.onSuccessStatusReturnedOperation(ConstantGUI.TOAST_LABEL_FOR_SESSION_CREATION);
					//setting new callback on the new session
					hostedSession.setCallbackGUI(this.callbackGUI);
				}else{
					//session not under focus no GUI to be updated
					Log.d(Log_Tag,"manager : isn't under focus : no GUI update" );
				}	
			
		}catch(ResponseException e){
			e.printStackTrace();
		}catch (SessionExistingException e) {
			Toast.makeText(this.context, ConstantGUI.TOAST_LABEL_PARAMETER_UPDATE_FAILURE+ConstantGUI.TOAST_LABEL_EXISTING_HOSTED_SESSION, Toast.LENGTH_LONG).show();
		}
		//At the end, if session has changed, calling GUI for refresh
		if(hasManagerChanged){
			if(this.callbackGUI!=null){
				//manager got the focus, GUI must be updated
				Log.d(Log_Tag,"manager is under focus : updating GUI" );
				this.callbackGUI.onObjectChanged();
			}else{
				//manager not under focus no GUI to be updated
			}
		}
	}
	
	/**Drop an existing session*/
	public void dropSession(int sessionIndex){
		try{
			Session session = this.sessionList.get(sessionIndex);
			//if it is an hosted session, associated alarm intent is canceled 
			//and it is unregistered from the location resolver
			if(session instanceof HostedSession){
				locationHelper.onSessionSuppressed(session);
				HostedSession hosted = ((HostedSession) session);
				if(hosted.getAlarmIntent()!=null){
					hosted.getAlarmIntent().cancel();
				}
			}
			
			this.sessionList.remove(sessionIndex);
			if(this.callbackGUI!=null){
				//manager got the focus, GUI must be updated
				Log.d(Log_Tag,"manager is under focus : updating GUI" );
				this.callbackGUI.onObjectChanged();
			}else{
				//manager not under focus no GUI to be updated
				Log.d(Log_Tag,"manager isn't under focus : no GUI update" );
			}
		}catch(IndexOutOfBoundsException e){
			e.printStackTrace();
		}
	}
	
	
	public void checkifJoinedSessionExists(String publicID) throws SessionExistingException{
		for(Session session : this.sessionList){
			if(session instanceof JoinedSession){
				if(session.getPublicID().equals(publicID)){
					throw new SessionExistingException();
				}
			}
		}
	}
	
	public void checkifHostedSessionExists(String publicID) throws SessionExistingException{
		for(Session session : this.sessionList){
			if(session instanceof HostedSession){
				if(session.getPublicID().equals(publicID)){
					throw new SessionExistingException();
				}
			}
		}
	}

	public HostedSession ResolveHostedSessionByPublicID(String publicID) {
		HostedSession found = null;
		for(Session session : this.sessionList){
			if(session instanceof HostedSession){
				if(session.getPublicID().equals(publicID)){
					found = (HostedSession) session;
					break;
				}
			}
		}
		return found;
	}
	

	/**Asynchronous call done when alarmReceiver wakes up manager with intent containing session public ID
	 * manager register the session for location upload and set a new alarm if needed*/
	@Override
	public void onUploadTimeReachedbySession(String sessionPublicID) {
		Log.d(Log_Tag,"Hosted Session : "+sessionPublicID+" woken up by Alarm for uploading");
		//resolve hosted session
		HostedSession session = ResolveHostedSessionByPublicID(sessionPublicID);
		if(session!=null){
			//register for location
			SessionManager.locationHelper.registerForLocation(session,this.context);
			//check next before end and if yes reschedule
			if(session.isNextUploadBeforeEnd()){
				Log.d(Log_Tag,"Hosted Session : "+sessionPublicID+" next Alarm before end : Rescheduling");
				setAlarmforNextUpdate(session,session.getUploadRate());
			}
		}
		
	}
	


	/**Creates a pending intent with the public ID of the hosted session to wake up
	 * at the specified time (in ms) and stores the pending intent as session attribute 
	 * in case of cancellation*/
	@Override
	public void setAlarmforNextUpdate(HostedSession session, long sleepDuration) {
		//set up pending intent attribute and provide alarm with it
		AlarmManager manager = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
		//creating intent and adding sessionId as intent extra field
		Intent intent = new Intent(context, AlarmReceiver.class);
		intent.putExtra(Constants.SESSION_PUBLIC_ID_INTENT_EXTRA_LABEL, session.getPublicID());
		int code = session.getPublicID().hashCode();
		PendingIntent alarmIntent = PendingIntent.getBroadcast(context, code, intent, 0);
		//registering intent to the session
		session.setAlarmIntent(alarmIntent);
		//preparing firing configuration
		Log.d(Log_Tag,"Hosted Session : "+session.getPublicID()+", Alarm scheduled to upload in : "+sleepDuration+" ms" );
		manager.set(AlarmManager.RTC_WAKEUP,System.currentTimeMillis() + sleepDuration, alarmIntent);
	}


	
	/**Called when an hosted session is refreshed and its alarm must then need to be updated*/
	@Override
	public void onAlarmUpdateRequired(HostedSession session) {
		Log.d(Log_Tag,"Hosted Session : "+session.getPublicID()+", Updating Alarm" );
		java.util.Date date= new java.util.Date();
		Timestamp now = new Timestamp(date.getTime());
		Timestamp end = session.getEndingTime();
		//checking if session has started
		if(now.before(session.startingTime)){
			//Waiting state : reschedule alarm
			if(session.getAlarmIntent()!=null){
				session.getAlarmIntent().cancel();
			}
			long timeBeforeStart = session.startingTime.getTime()-now.getTime();
			setAlarmforNextUpdate(session,timeBeforeStart);
		}else if(end==null||now.before(end)){
				//running state : reschedule alarm if needed:
				if(end!=null&&!session.isNextUploadBeforeEnd()){
					if(session.getAlarmIntent()!=null){
						session.getAlarmIntent().cancel();
					}
				}	
		}
		
	}
	
	/**When service get destroyed, all the alarm intent must be canceled*/
	public void cancelAlarmIntents(){
		for(Session session : this.sessionList){
			if(session instanceof HostedSession){
				HostedSession hosted = (HostedSession) session;
				if(hosted.getAlarmIntent()!=null){
					hosted.getAlarmIntent().cancel();
				}
		
			}
		}
	}
	
	
	
}
