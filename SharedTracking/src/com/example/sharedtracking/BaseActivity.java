package com.example.sharedtracking;

import com.example.sharedtracking.background.LocationAsynchronousResolver;
import com.example.sharedtracking.background.MainService;
import com.example.sharedtracking.background.MainService.MainBinder;
import com.example.sharedtracking.constants.Constants;
import com.example.sharedtracking.session.Session;
import com.example.sharedtracking.session.SessionManager;
import com.example.sharedtracking.views.ConstantGUI;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationSettingsStates;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

public abstract class BaseActivity extends FragmentActivity implements IGraphicalListener{
	
	/**Service handling the session manager object*/
    protected MainService mainService;
    /**is main service bound to the activity*/
    protected boolean isMainServiceBound = false;
    
    /**Session Manager obtained from the service**/
    protected SessionManager manager;
    

    /** Defines callbacks for service binding, passed to bindService() */
    private ServiceConnection mainConnection = new ServiceConnection() {
    	@Override
        public void onServiceConnected(ComponentName className,IBinder service) {
    		Log.d(getActivityClassName(),"Activity now connected to the service");
            // bound to MainService, cast the IBinder and get MainService instance
            MainBinder binder = (MainBinder) service;
            mainService = binder.getMainService();
            isMainServiceBound = true;
            //requesting GUI
            requestGUI();
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
        	Log.d(getActivityClassName(),"Activity now disconnected from the service");
        	isMainServiceBound = false;
        }
    };
    
    /**log Tag for debugging*/
    public String getActivityClassName(){
    	return "Base Activity : ";
    }
    
    /**When Activity is created, it gets bound to service*/
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
        Log.d(this.getActivityClassName(),"creating Activity");
        
        //starting service
        Log.d(this.getActivityClassName(),"binding to Main Service");
        Intent intent = new Intent(this, MainService.class);
        bindService(intent, mainConnection, Context.BIND_AUTO_CREATE);
    }
    
    /**When Activity is destroyed, it gets unbound to service*/
    @Override 
    public void onDestroy(){
    	super.onDestroy();
    	
    	Log.d(this.getActivityClassName(),"destroying Activity");
        if (isMainServiceBound) {
            unbindService(mainConnection);
            isMainServiceBound = false;
        }
    }
    
    /**TODO delete at the end*/
    @Override
    public void onPause(){
    	super.onPause();
    /*	
    	Log.d(this.getActivityClassName(),"pausing Activity");
        if (isMainServiceBound) {
            unbindService(mainConnection);
            isMainServiceBound = false;
        }*/
    }
    
    public void requestGUI(){
    	Log.d(this.getActivityClassName(),"requesting GUI");
    	try{
    		if(!this.isMainServiceBound){
    			throw new GraphicalException(this.getActivityClassName()+" not yet bound to Main Service");
    		}
    		SessionManager mgr = this.mainService.getManager();
    		if(mgr==null){
    			throw new GraphicalException(this.getActivityClassName()+" Manager object can't be retrieved (null)");
    		}
    		Log.d(this.getActivityClassName(),"session manager from service retrieved by the activity");
    		this.manager = mgr;
    		//as soon as manager is retrieved Activity Layout can be updated
    		Log.d(this.getActivityClassName(),"Updating application Layout");
    		//triggering itself a GUI update 
    		updateGUI();
    		
    		//registering the activity as GUI of the manager
            ObjectChangingCallback callback = new ObjectChangingCallback(this);   		
            this.manager.setCallbackGUI(callback);
    		Log.d(this.getActivityClassName(),"activity registered as manager GUI");
    		
    		//registering the activity as Context of the manager, useful when dialog must be displayed
    		//for location setting
    		this.manager.setContext(this);
    		
    		//registering the activity as GUI of the sessions within the manager
            for(Session session : this.manager.getSessionList()){
          	   session.setCallbackGUI(callback);
             }
            Log.d(this.getActivityClassName(),"activity registered as sessions GUI");
    		
    	}catch(GraphicalException e){
    		e.printStackTrace();
    	}
    }
    
	public abstract void updateGUI();
	
	public SessionManager getSessionManager(){
		return this.manager;
	}
	
	/**Location Resolver may request the current activty to display location setting dialog
	 * when location is not enabled*/
	public void displayLocationSettingDialog(Status status){
		Log.d(this.getActivityClassName(),"Displaying dialog for location setting");
		try{
			//TODO check if already displayed
	        status.startResolutionForResult(this, Constants.REQUEST_CHECK_LOCATION_SETTINGS);
	    } catch (IntentSender.SendIntentException e) {
	        // Ignore the error.
	    }
	}
	
	/**Callback Method when user turn on or not location service after being prompted*/
	 @Override
	 protected void onActivityResult(int requestCode, int resultCode, Intent data) {
	     switch (requestCode) {
	         case Constants.REQUEST_CHECK_LOCATION_SETTINGS:
	        	 LocationAsynchronousResolver locationResolver = this.manager.getLocationResolver();
	             switch (resultCode) {
	                 case Activity.RESULT_OK:
	                	 Log.d(this.getActivityClassName(),"user accepted to turn on location");
	                     // All required changes were successfully made
	                	 locationResolver.onLocationAccepted();
	                     break;
	                 case Activity.RESULT_CANCELED:
	                	 Log.d(this.getActivityClassName(),"user refused to turn on location");
	                     // The user was asked to change settings, but chose not to
	                	 locationResolver.onLocationForbidden();
	                     break;
	                 default:
	                     break;
	             }
	             break;
	     }
	 }
	
	
	@Override
	public void notifyNetworkIssue() {
		Toast.makeText(this, ConstantGUI.TOAST_LABEL_NETWORK_CONNECTION_ISSUE, Toast.LENGTH_LONG).show();
	}

	@Override
	public void notifySuccessfulUpdateOperation(String ParameterName) {
		Toast.makeText(this, ConstantGUI.TOAST_LABEL_PARAMETER_UPDATE_SUCCESS+ParameterName, Toast.LENGTH_LONG).show();
	}

	@Override
	public void notifyFailedUpdateOperation(String ParameterName) {
		Toast.makeText(this, ConstantGUI.TOAST_LABEL_PARAMETER_UPDATE_FAILURE+ParameterName, Toast.LENGTH_LONG).show();
	}

}
