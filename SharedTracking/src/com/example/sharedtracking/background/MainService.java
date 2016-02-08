package com.example.sharedtracking.background;


import com.example.sharedtracking.session.SessionManager;
import com.google.gson.Gson;

import android.app.IntentService;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Binder;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.util.Log;

/**Background service waking up session manager periodically*/
public class MainService extends Service {
	
	/**Log Tag for debugging purposes*/
	private final static String Log_Tag = "Timer Service";

	/**Shared Preference for storing manager when service ends*/
	SharedPreferences  mPrefs;
	/**label for storing session manager in shared preferences*/
	private final static String managerLabel = "Session Manager";

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
    	
    	 Context ctx = getApplicationContext();
    	 this.mPrefs = PreferenceManager.getDefaultSharedPreferences(ctx);
    	 
    	 Gson gson = new Gson();
    	 String json = mPrefs.getString(managerLabel, "");
    	 SessionManager mgr = gson.fromJson(json, SessionManager.class);
    	 
    	 if(mgr==null){
    		 Log.d(Log_Tag,"no previous manager retrieved from shared preference, creating new one");
    		 mgr = createManager();
    	 }else{
    		 Log.d(Log_Tag,"manager retrieved from shared preference");
    	 }
    	 this.manager = mgr;
    	 Log.d(Log_Tag,"service is set with a manager, now configuring periodic thread");
    	 
         handler = new Handler();
         handler.postDelayed(runnable, 2000);
         Log.d(Log_Tag,"periodic thread is triggered");
     }
     
     @Override 
     public void onDestroy(){
    	 super.onDestroy();
    	 Log.d(Log_Tag,"onDestroy just called");
    	 if(this.manager!=null){
    		 Log.d(Log_Tag,"manager not null ending the hosted session");
    		 
    	 }
    	 Log.d(Log_Tag,"service destroyed");
     }
     
     
     @Override
     public IBinder onBind(Intent intent) {
    	 Log.d(Log_Tag,"onBind method just called");
         return this.binder;
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
         String name;
         String manufacturer = Build.MANUFACTURER;
         String model = Build.MODEL;
         if (model.startsWith(manufacturer)) {
         	name = model;
         } else {
         	name = manufacturer + " " + model;
         }
         //TODO fix read phone state
         //TelephonyManager telephonyManager = (TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE);
         String devID = "123412341234123:"+"123412341234123";//telephonyManager.getDeviceId();
         
         Log.d(Log_Tag,"phone identifiers retieved : name : "+name+" ID : "+devID);
     	//creating new manager
         SessionManager mgr = new SessionManager(this,name,devID,minPeriod);
         return mgr;
     };
     
     public SessionManager getManager(){
    	 return this.manager;
     }
     
     

}