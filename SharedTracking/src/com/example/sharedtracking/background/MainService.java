package com.example.sharedtracking.background;


import java.util.Random;

import com.example.sharedtracking.constants.Constants;
import com.example.sharedtracking.session.SessionManager;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Binder;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

/**Background service waking up session manager periodically*/
public class MainService extends Service {
	
	/**Log Tag for debugging purposes*/
	private final static String Log_Tag = "Timer Service";

	/**Session Manager handled by the service*/
	private SessionManager manager;
	/**Binder to receive commands from Activities*/
	private MainBinder binder = new MainBinder();
	
	/**Thread and handler for repetitive process*/
	private int period = 5000;
	
	private Handler handler = null;
    
	private Runnable runnable = new Runnable() {
        public void run() {
   		 Log.d(Log_Tag, "runnable is looping again");
   		 if(manager==null){
   			Log.d(Log_Tag, "No Manager Set");
   		 }else{
   			Log.d(Log_Tag, "a Manager is Set, waking it up");
   			 manager.wakeUp();
   		 }
            handler.postDelayed(runnable, period);
        }
    };

	
    public MainService() {
        super();
     }

     
     @Override
     public void onCreate() {
    	 super.onCreate();
    	 Log.d(Log_Tag,"onCreate just called, Retreiving manager from shared preferences or creating a new one");

    	 this.manager = createManager();
    	 Log.d(Log_Tag,"service is set with a manager, now configuring periodic thread");
    	 
         handler = new Handler();
         handler.postDelayed(runnable, 2000);
         Log.d(Log_Tag,"periodic thread is triggered");
     }
     
     @Override 
     public void onDestroy(){
    	 
    	 Log.d(Log_Tag,"onDestroy just called");
    	 //killing alarm intents
    	 this.manager.cancelAlarmIntents();
    	 //removing notifications
    	 NotificationManager notifManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
     	notifManager.cancel(Constants.NOTIFICATION_ID);
     	
     	super.onDestroy();
     }
     
     
     @Override
     public IBinder onBind(Intent intent) {
    	 Log.d(Log_Tag,"onBind method just called");
         return this.binder;
     }
     
     @Override
     public boolean onUnbind(Intent intent) {
    	 boolean result = super.onUnbind(intent);
    	 this.stopSelf();
         return result;
     }
     
     //interface for clients to access the service
     public class MainBinder extends Binder{
    	 
    	 public MainService getMainService(){
    		 return MainService.this;
    	 }
    	 
     }
     
     /**Public methods to be accessed from activity: (in addition to start and stop)*/
     
     /**Create a new manager object*/
     public SessionManager createManager(){
    	 int minPeriod=period;
         //retrieving device name
    	 String name;
         String manufacturer = Build.MANUFACTURER;
         String model = Build.MODEL;
         if (model.startsWith(manufacturer)) {
         	name = model;
         } else {
         	name = manufacturer + " " + model;
         }
         //truncating, avoiding too long name
         String deviceName;
         if(name.length()>20){
        	  deviceName = name.substring(0,20);
         }else{
        	 deviceName = name;
         }
         //retrieving or generating application ID
         Context ctx = getApplicationContext();
    	 SharedPreferences prefs = ctx.getSharedPreferences(Constants.SHARED_PREFERENCE_NAME, MODE_PRIVATE);
    	 String deviceID = prefs.getString(Constants.DEVICE_ID_STORING_LABEL, null);
    	 
    	 if(deviceID==null){
    		 Log.d(Log_Tag,"no previous manager retrieved from shared preference, creating new one and storing it");
    		 //generating device ID
    		 deviceID = randomString();
    		 Log.d(Log_Tag,"device ID generated : "+deviceID);
    		 //storing device ID
    	     Editor prefsEditor = prefs.edit();
    	     prefsEditor.putString(Constants.DEVICE_ID_STORING_LABEL,deviceID);
    	     prefsEditor.commit();
    	 }
         
         Log.d(Log_Tag,"phone identifiers retieved : name : "+deviceName+" ID : "+deviceID);
     	//creating new manager
         SessionManager mgr = new SessionManager(this,deviceName,deviceID,minPeriod);
         return mgr;
     };
     
     public SessionManager getManager(){
    	 return this.manager;
     }
      

 	public String randomString(){
 		String Alphabet = Constants.DEVICE_ID_ALPHABET;
 	 	int tokenLength = Constants.DEVICE_ID_LENGTH;
 	 	Random rnd = new Random();
 	   StringBuilder sb = new StringBuilder(tokenLength);
 	   for( int i = 0; i < tokenLength; i++ ) 
 	      sb.append( Alphabet.charAt( rnd.nextInt(Alphabet.length()) ) );
 	   return sb.toString();
 	}
     
     public void onTaskRemoved (Intent rootIntent){
    	 Log.d(Log_Tag,"task removed");
    	 //removing notifications
    	 NotificationManager notifManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
     	notifManager.cancel(Constants.NOTIFICATION_ID);
        this.stopSelf();
    }
     
     /**Called by broadcast receiver on alarm*/
     public void asynchronousUpload(String publicID){
    	 Log.d(Log_Tag,"service peeked by Alarm receiver with ID : "+publicID);
    	 this.manager.onUploadTimeReachedbySession(publicID);
     }
     

}