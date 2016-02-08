package com.example.sharedtracking;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.sharedtracking.constants.Constants;
import com.example.sharedtracking.inputs.DialogInputConverter;
import com.example.sharedtracking.session.HostedSession;
import com.example.sharedtracking.session.JoinedSession;
import com.example.sharedtracking.session.Session;
import com.example.sharedtracking.types.Sample;
import com.example.sharedtracking.views.ConstantGUI;
import com.google.android.gms.maps.GoogleMap;

/**A tracked activity associated to one hosted session
 * acts as a passive activity*/
public class TrackedActivity extends BaseActivity {
	
	/**Associated joined session*/
	private int indexSession;

	/**map object*/
	private GoogleMap map;
	
    /**log Tag for debugging*/
	@Override
    public String getActivityClassName(){
    	return "Tracked Activity : ";
    }
	
    /**
     * Static constructor for starting an activity with a joined Session
     * to be called by the main activity on user click
     */
    public static void startActivity(Context context, int index) {
        // Create and start intent for this activity
        Intent intent = new Intent(context, TrackedActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra("sessionIndex",index);
        context.startActivity(intent);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tracked_activity_session);
        //retrieving the session on which the tracking activity is built
       this.indexSession = (Integer) getIntent().getSerializableExtra("sessionIndex");
    }	


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
      getMenuInflater().inflate(R.menu.main, menu);
      return true;
    }
    
	@Override
	/**Tracked activity implements onObjectModified method in re-reading session meta data and samples*/
	public void updateGUI() {
		// the Activity only focus on the session displayed
		Log.d(this.getActivityClassName(),"updating GUI");
		Session boundSession = this.manager.getSessionList().get(this.indexSession);
		try{
			if(boundSession==null){
				//if session is null, update is aborted
				Log.d(this.getActivityClassName(),"impossible to update GUI : session object is null");
				throw new GraphicalException("Tracked Activity is updating a null session");
			}
			//dealing with hosted session
			HostedSession session = (HostedSession) boundSession;
			//Set activity title and icon
			String name = session.getName();
			if(name==null){
				name = ConstantGUI.DEFAULT_VALUE_SESSION_NAME;
			}
		    setTitle(session.getName());
			getActionBar().setIcon(R.drawable.ic_followed_white);
		     
		    //Set status background
			int status = session.getStatus();
			ImageView ivSessionStatus = (ImageView)findViewById(R.id.tracked_session_status);
			if (status==Constants.SESSION_STATUS_DONE){
				ivSessionStatus.setBackground(ContextCompat.getDrawable(this,R.drawable.session_item_status_background_done));
			}else if(status==Constants.SESSION_STATUS_RUNNING){
				ivSessionStatus.setBackground(ContextCompat.getDrawable(this,R.drawable.session_item_status_background_active));
			}else if(status==Constants.SESSION_STATUS_PENDING){
				ivSessionStatus.setBackground(ContextCompat.getDrawable(this,R.drawable.session_item_status_background_pending));
			}else if(status==Constants.SESSION_STATUS_UNKNOWN){
				ivSessionStatus.setBackground(ContextCompat.getDrawable(this,R.drawable.session_item_status_background_unknown));
			}else if(status==Constants.SESSION_STATUS_NOT_CONNECTED){
				ivSessionStatus.setBackground(ContextCompat.getDrawable(this,R.drawable.session_item_status_background_not_connected));
			}else if(status==Constants.SESSION_STATUS_NOT_LOCALIZED){
				ivSessionStatus.setBackground(ContextCompat.getDrawable(this,R.drawable.session_item_status_background_not_localized));
			}
			
		     //Set Activity Public ID
			TextView publicIDTV = (TextView) findViewById(R.id.tracked_session_public_token);
			final String publicID = session.getPublicID();
			publicIDTV.setText(publicID);

		     
			//Set times, dates and sampling info
			Locale current = getResources().getConfiguration().locale;
			SimpleDateFormat timeFormatter = new SimpleDateFormat(ConstantGUI.TIME_FORMATTING_STRING,current);
			SimpleDateFormat dateFormatter = new SimpleDateFormat(ConstantGUI.DATE_FORMATTING_STRING,current);
			
			//START
			TextView startTimeTV = (TextView) findViewById(R.id.tracked_session_starting_time);	
			TextView startDateTV = (TextView) findViewById(R.id.tracked_session_starting_date);	
			Timestamp start = session.getStartingTime();
			String startTimeString;
			String startDateString;
			if(start==null){
				startTimeString = ConstantGUI.DEFAULT_VALUE_END_TIME;
				startDateString = ConstantGUI.DEFAULT_VALUE_END_DATE;
			}else{
				startTimeString = timeFormatter.format(start);
				startDateString = dateFormatter.format(start);
			}
			startTimeTV.setText(startTimeString);
			startDateTV.setText(startDateString);
			
			//END
			TextView endTimeTV = (TextView) findViewById(R.id.tracked_session_ending_time);
			TextView endDateTV = (TextView) findViewById(R.id.tracked_session_ending_date);
			Timestamp end = session.getEndingTime();
			String endTimeString;
			String endDateString;
			if(end==null){
				endTimeString = ConstantGUI.DEFAULT_VALUE_END_TIME;
				endDateString = ConstantGUI.DEFAULT_VALUE_END_DATE;
			}else{
				endTimeString = timeFormatter.format(end);
				endDateString = dateFormatter.format(end);
			}
			endTimeTV.setText(endTimeString);
			endDateTV.setText(endDateString);
			
			//Sampling Info
			TextView submittedSampleTV = (TextView) findViewById(R.id.tracked_session_submitted_sample);
			int sampleNumber = session.getSubmittedSampleNumber();
			submittedSampleTV.setText(Integer.toString(sampleNumber));
			
			TextView samplingInfoTV = (TextView) findViewById(R.id.tracked_session_sampling_rate);
			int rate = session.getUploadRate();
			String rateString = DialogInputConverter.convertRateToString(rate);
			samplingInfoTV.setText(ConstantGUI.SAMPLING_INFO_PREFIX+rateString);
			
			//stop tracking button, only visible when session is running
			Button stoptrackingButton = (Button) findViewById(R.id.tracked_session_stop_tracking);
			if (session.getStatus()==Constants.SESSION_STATUS_RUNNING){
				stoptrackingButton.setVisibility(View.VISIBLE);
			}else{
				stoptrackingButton.setVisibility(View.INVISIBLE);
			}

		}catch(GraphicalException e){
			e.printStackTrace();
		}
		
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    switch (item.getItemId()) {
	        case R.id.action_settings:
		    // Load the preferences from an XML resource
		    // Display the fragment as the main content.
		    getFragmentManager().beginTransaction()
		            .replace(android.R.id.content, new TrackedActivitySettingFragment())
		            .addToBackStack("settings")
		            .commit();
	        default:
	            // If we got here, the user's action was not recognized.
	            // Invoke the superclass to handle it.
	            return super.onOptionsItemSelected(item);

	    }
	}

	public int getSessionIndex(){
		return this.indexSession;
	}

	/**stops a running session : set ending time to now*/
	public void stopTracking(View v){
		Session boundSession = this.manager.getSessionList().get(this.indexSession);
		try{
			if(boundSession==null){
				//if session is null, update is aborted
				Log.d(this.getActivityClassName(),"impossible to stop Tracking : session object is null");
				throw new GraphicalException("Tracked Activity is trying to stop tracking on a null session");
			}
			//dealing with hosted session
			HostedSession session = (HostedSession) boundSession;
			//operation only performed when tracking is running
			if(session.getStatus()==Constants.SESSION_STATUS_RUNNING){
				//setting end date to now + handling latency
				java.util.Date date= new java.util.Date();
				int latency = 2 ;//2 seconds for latency
				Timestamp now = new Timestamp(date.getTime()+latency*1000);
				session.updateEndingTime(now);
			}
			
		}catch(GraphicalException e){
			e.printStackTrace();
		}
	}

}