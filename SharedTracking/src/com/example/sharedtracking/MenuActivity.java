package com.example.sharedtracking;

import android.Manifest;
import android.app.ActionBar;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ListView;
import android.widget.TextView;
import com.example.sharedtracking.constants.Constants;
import com.example.sharedtracking.inputs.SessionDeletionDialog;
import com.example.sharedtracking.inputs.TrackedSessionCreationDialog;
import com.example.sharedtracking.inputs.TrackingSessionCreationDialog;
import com.example.sharedtracking.session.HostedSession;
import com.example.sharedtracking.session.JoinedSession;
import com.example.sharedtracking.session.Session;
import com.st.sharedtracking.R;

public class MenuActivity extends BaseActivity implements IInputListener{
	
	private SessionListAdapter adapter;
	
	/**counting how many hosted session are currently being created : for displaying or not progress bar*/
	private int currentCreationsCounter = 0;
	
    /**log Tag for debugging*/
	@Override
    public String getActivityClassName(){
    	return "Menu Activity : ";
    }
    @Override
    public void onResume(){
    	super.onResume();
    	Log.d(this.getActivityClassName(),"resuming to activity");
    	requestGUI();
    }
    
    /**When Activity is destroyed, cancel notifications*/
    @Override 
    public void onDestroy(){
    	Log.d(this.getActivityClassName(),"detroying menu activity");
    	super.onDestroy();
    }
	
    /**When an object signals an update the activity informs the user*/
	@Override
	public void updateGUI() {
		
		Log.d(this.getActivityClassName(),"updating GUI");

        //updating notifications
        updateNotification();
        
        setTitle(R.string.menu_activity_title);
		getActionBar().setIcon(R.drawable.ic_action_bar);
		
		ListView list = (ListView)findViewById(android.R.id.list);
		TextView emptyText = (TextView)findViewById(android.R.id.empty);
		list.setEmptyView(emptyText);
		if(this.adapter==null){
			// Create the adapter to convert the array to views
			adapter = new SessionListAdapter(this, this.manager.getSessionList());
			list.setAdapter(adapter);
		}else{
			adapter.notifyDataSetChanged();
		}
        // Item Click Listener for the listview
        OnItemClickListener itemClickListener = new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View container, int position, long id) {
            	Log.d(getActivityClassName(),"session item view clicked, index :"+position);
            	Log.d(getActivityClassName(),"starting new acitivity for selected session");
            	onSessionSelected(position);
            }
        };
        // Setting the item click listener for the listview
        list.setOnItemClickListener(itemClickListener);
        
        //Item Long Click Listener for deletion
        OnItemLongClickListener itemLongClickListener = new OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View container, int position, long id) {
            	Bundle args = new Bundle();
            	//position is incremented in order to avoid passing 0 value through intent
            	args.putInt("incrementedIndex", position+1);
            	String name = manager.getSessionList().get(position).getName();
            	args.putString("name",name);
            	SessionDeletionDialog dialog = new SessionDeletionDialog();  
            	dialog.setArguments(args);
            	dialog.show(getFragmentManager(),"Deletion Fragment Dialog");
                return true;
            }
        }; 
        
        // Setting the item click listener for the listview
        list.setOnItemLongClickListener(itemLongClickListener);
        
	}
	
	public void onSessionSelected(int index){
		try{
			Session selectedSession = this.manager.getSessionList().get(index);
			if(selectedSession==null){
				throw new GraphicalException("Session Index is not valid");
			}
			
			if(selectedSession instanceof HostedSession){
				//launching Tracked Activity
				Log.d(getActivityClassName(),"launching new tracked activity");
				TrackedActivity.startActivity(this,index);			
			}
			else if (selectedSession instanceof JoinedSession){
				//launching Tracking Activity
				Log.d(getActivityClassName(),"launching new tracking activity");
				TrackingActivity.startActivity(this,index);		
			}else{
				throw new GraphicalException("Unknown Session type");
			}
			
		}catch(GraphicalException e){
			e.printStackTrace();
		}
	}
	
	public void createHostedSession(View v){
		if (handleUserLocationDynamicPermission()){
			TrackedSessionCreationDialog dialog = new TrackedSessionCreationDialog();
			dialog.show(getFragmentManager(), "Tracked Fragment Dialog");
		}
	}
	
	public void joinSession(View v){
		TrackingSessionCreationDialog dialog = new TrackingSessionCreationDialog();
		dialog.show(getFragmentManager(), "Tracking Fragment Dialog");
	}
	@Override
	public void onImmediateSessionCreationReady(String name,int rate) {
		Log.d(this.getActivityClassName()," called by dialog for Immediate Hosted Session Creation");
		showProgressBar();
		this.manager.createNewImmediateSession(name,rate);
		
	}
	@Override
	public void onPreparedSessionCreationReady(String name, int rate, String publicID, String password,
			String startDate, String endDate) {
		Log.d(this.getActivityClassName()," called by dialog for Prepared Hosted Session Creation");
		showProgressBar();
		this.manager.createNewPreparedSession(name, publicID, password, startDate, endDate, rate);
		
	}
	@Override
	public void onSessionContributionReady(String publicID, String password) {
		Log.d(this.getActivityClassName()," called by dialog for Session Contribution");
		showProgressBar();
		this.manager.contributeToExistingSession(publicID, password);
	}
	@Override
	public void onSessionJoiningReady(String publicID) {
		Log.d(this.getActivityClassName()," called by dialog for creating new Tracking Session ");
		this.manager.joinExistingSession(publicID);
		
	}
	
	
	
	public boolean handleUserLocationDynamicPermission(){
		boolean result = true;
		 if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
         	int fineLocationPermissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);
         	int coarseLocationPermissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION);    	
         		if(fineLocationPermissionCheck == PackageManager.PERMISSION_DENIED
         				&& coarseLocationPermissionCheck == PackageManager.PERMISSION_DENIED){
         			
         			result=false;
         			//if location permission not provided, requesting it:
         	       ActivityCompat.requestPermissions((Activity)this,
         	                new String[]{Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.ACCESS_COARSE_LOCATION},
         	                Constants.PERMISSION_REQUEST_USER_LOCATION);
         	      
         		
         		}
		 }
		 return result;
	}
	
	@Override
	public void onRequestPermissionsResult(int requestCode,
	        String permissions[], int[] grantResults) {
	    switch (requestCode) {
	        case Constants.PERMISSION_REQUEST_USER_LOCATION: {
	            // If request is cancelled, the result arrays are empty.
	            if (grantResults.length > 0
	                && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
	                // permission was granted
	    			TrackedSessionCreationDialog dialog = new TrackedSessionCreationDialog();
	    			dialog.show(getFragmentManager(), "Tracked Fragment Dialog");
	            } else {

	                // permission denied, boo! Disable the
	                // functionality that depends on this permission.
	            }
	            return;
	        }

	        // other 'case' lines to check for other
	        // permissions this app might request
	    }
	}
	
	/**When the user delete a session : simply removes it from the sessionList
	 * no further operation session can be shared (contribution)*/
	@Override
	public void onSessionDeletionReady(int index) {
		manager.dropSession(index);
	}
	

	@Override
	public void onBackPressed() {
		moveTaskToBack(true);
	}

	public void notifyHostedSessionCreation(){
		dismissProgressBar();
	}
	
	/**make appear the progress bar if no hosted session are being created*/
	public void showProgressBar(){
		if(this.currentCreationsCounter==0){
	        final ActionBar actionBar = getActionBar();	    
	        actionBar.setCustomView(R.layout.progress_bar_layout);
	        actionBar.setDisplayShowCustomEnabled(true);
		}
		this.currentCreationsCounter++;
	}
	
	public void dismissProgressBar(){
		this.currentCreationsCounter--;
		if(this.currentCreationsCounter<=0){
			final ActionBar actionBar = getActionBar();
			actionBar.setDisplayShowCustomEnabled(false);
			this.currentCreationsCounter=0;
		}
	}
	
	
}
