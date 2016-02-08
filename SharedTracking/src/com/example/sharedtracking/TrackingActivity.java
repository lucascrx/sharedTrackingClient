package com.example.sharedtracking;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

import com.example.sharedtracking.constants.Constants;
import com.example.sharedtracking.inputs.DialogInputConverter;
import com.example.sharedtracking.session.HostedSession;
import com.example.sharedtracking.session.JoinedSession;
import com.example.sharedtracking.session.Session;
import com.example.sharedtracking.types.Sample;
import com.example.sharedtracking.views.ConstantGUI;
import com.example.sharedtracking.views.MarkerColorManager;
import com.example.sharedtracking.views.PositionedMarker;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

/**A tracking activity is associated to one Joined Session
 * acts as an passive activity*/
public class TrackingActivity extends BaseActivity {
	
	/**Associated joined session*/
	private int indexSession;

	/**map object*/
	private GoogleMap map;
	/**color manager for map markers*/
	private MarkerColorManager colorManager;
	
	/**binds each contributor device ID to the index of the last marker of their position displayed on the Map*/
	private HashMap<String,PositionedMarker> latestMarkersList;
	
    /**
     * Static constructor for starting an activity with a joined Session
     * to be called by the main activity on user click
     */
    public static void startActivity(Context context, int index) {
        // Create and start intent for this activity
        Intent intent = new Intent(context, TrackingActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra("sessionIndex",index);
        context.startActivity(intent);
    }
    
    /**log Tag for debugging*/
	@Override
    public String getActivityClassName(){
    	return "Tracking Activity : ";
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tracking_activity_session);
        //retrieving the session on which the tracking activity is built
       this.indexSession = (Integer) getIntent().getSerializableExtra("sessionIndex");
       
       this.colorManager=new MarkerColorManager();
       this.latestMarkersList = new HashMap<String,PositionedMarker>();
       
       //initialize map
       initilizeMap();
    }


	
	
	public void updateGUI() {
		// the Activity only focus on the session displayed
		Log.d(getActivityClassName(),"updating GUI");
		Session boundSession = manager.getSessionList().get(indexSession);
		try{
			if(boundSession==null){
				//if session is null, update is aborted
				Log.d(this.getActivityClassName(),"impossible to update GUI : session object is null");
				throw new GraphicalException("Tracking Activity is updating a null session");
			}
			//dealing with joined Session
			JoinedSession session = (JoinedSession) boundSession;
		     //Set activity title
			String name = session.getName();
			if(name==null){
				name = ConstantGUI.DEFAULT_VALUE_SESSION_NAME;
			}
		    setTitle(session.getName());
		    getActionBar().setIcon(R.drawable.ic_following_white);
		    
		    //Set status background
			int status = session.getStatus();
			ImageView ivSessionStatus = (ImageView)findViewById(R.id.tracking_session_status);
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
			TextView publicIDTV = (TextView) findViewById(R.id.tracking_session_public_token);
			String publicID = session.getPublicID();
			publicIDTV.setText(publicID);
			
			//Set times, dates and sampling info
			Locale current = getResources().getConfiguration().locale;
			SimpleDateFormat timeFormatter = new SimpleDateFormat(ConstantGUI.TIME_FORMATTING_STRING,current);
			SimpleDateFormat dateFormatter = new SimpleDateFormat(ConstantGUI.DATE_FORMATTING_STRING,current);
			
			//START
			TextView startTimeTV = (TextView) findViewById(R.id.tracking_session_starting_time);	
			TextView startDateTV = (TextView) findViewById(R.id.tracking_session_starting_date);	
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
			TextView endTimeTV = (TextView) findViewById(R.id.tracking_session_ending_time);
			TextView endDateTV = (TextView) findViewById(R.id.tracking_session_ending_date);
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
			TextView receivedSampleTV = (TextView) findViewById(R.id.tracking_session_received_sample);
			int sampleNumber = session.getReceivedSampleNumber();
			receivedSampleTV.setText(Integer.toString(sampleNumber));
			
			TextView samplingInfoTV = (TextView) findViewById(R.id.tracking_session_sampling_rate);
			int rate = session.getUploadRate();
			String rateString = DialogInputConverter.convertRateToString(rate);
			samplingInfoTV.setText(ConstantGUI.SAMPLING_INFO_PREFIX+rateString);
			
			//placing markers on the map
			if(map!=null){
				Log.d(getActivityClassName(),"updating MAP view");
				//map.clear();
				printMarkers(session);
			}
		
		}catch(GraphicalException e){
			e.printStackTrace();
		}	
		
	}
	
    /** function to load map. If map is not created it will create it for you
     * */
    private void initilizeMap() {
        if (map == null) {
            map = ((MapFragment) getFragmentManager().findFragmentById(R.id.map)).getMap();
            // check if map is created successfully or not
            if (map == null) {
            	Log.d(getActivityClassName(),"unable to create Map");
            }
        }
    }
    
    /**Print the markers on the Map*/
    private void printMarkers(JoinedSession joinedSession){
    	Locale current = getResources().getConfiguration().locale;
    	SimpleDateFormat sdf = new SimpleDateFormat(ConstantGUI.TIME_DATE_FORMATTING_STRING,current);
    	HashMap<String, ArrayList<Sample>> samples = joinedSession.getSamples();
		//looping on the different lists
		for(String identifier : samples.keySet()){
			List<Sample> currentList = samples.get(identifier);
			Sample last = currentList.get(currentList.size()-1);
			int color = colorManager.getColorForDevice(identifier);

			//resolving the index of the last printed index positioned 
			PositionedMarker positionedMarker = this.latestMarkersList.get(identifier);
			List<Sample> adjustedList = currentList;
			
		    if(positionedMarker != null){
		    	//two things to do : 
		    	//1. reduce the list to print to make it start from the last printed sample index
		    	//latest must be re-printed with passed icon
		    	int indexLatest = positionedMarker.getPosition();
		    	adjustedList = currentList.subList(indexLatest,currentList.size());
		    	//2.latest sample must be erased from the map
		    	Marker markerLatest = positionedMarker.getMarker();
		    	markerLatest.remove();
		    }
		    	
		    
			for(Sample sample : adjustedList){
					double latitude = sample.getLatitude();
					double longitude = sample.getLongitude();
					Timestamp time = sample.getTime();
					//Log.d(this.getActivityClassName(),"New Sample located at lat: "+latitude+" long : "+longitude);
					String deviceName = sample.getDeviceName();
					String deviceID = sample.getDeviceID();
					String timeString = sdf.format(time);
					// create marker
					MarkerOptions marker = new MarkerOptions()
								.position(new LatLng(latitude, longitude))
								.title(deviceName)
								.snippet(timeString);
		
					Bitmap icon;
				if(sample.equals(last)){
					//if last received sample : Current Icon
					icon = generateIconCurrentLocation(color);
					marker.icon(BitmapDescriptorFactory.fromBitmap(icon));
					marker.anchor((float)0.5,(float)0.9);
					if(samples.size()==1){
						CameraPosition cameraPosition = new CameraPosition.Builder().target(
				                new LatLng(sample.getLatitude(), sample.getLongitude())).zoom(15).build();
						map.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

						// adding marker
						Marker newLatestMarker = map.addMarker(marker);
						//keep reference in memory
						this.latestMarkersList.put(identifier, new PositionedMarker(newLatestMarker, currentList.size()-1));
						
					}else{
						//camera move for multiple device tracks
						//TODO to define
						CameraPosition cameraPosition = new CameraPosition.Builder().target(
				                new LatLng(sample.getLatitude(), sample.getLongitude())).zoom(15).build();
						map.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

						// adding marker
						Marker newLatestMarker = map.addMarker(marker);
						//keep reference in memory
						this.latestMarkersList.put(identifier, new PositionedMarker(newLatestMarker, currentList.size()-1));
					}
				}else{
					if(!toClose(sample, last)){
						//otherwise Passed Icon
						icon = generateIconPassedLocation(color);
						marker.icon(BitmapDescriptorFactory.fromBitmap(icon));
						marker.anchor((float)0.5,(float)0.5);
						// adding marker
						map.addMarker(marker);
					}
				}

			}
		
		}
    }
    
	/**Generate Custom Marker for current location with the specified color
	 * @param color marker color*/
	public Bitmap generateIconCurrentLocation(int color){
        BitmapFactory.Options bitopt=new BitmapFactory.Options();
        bitopt.inMutable=true;
		Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.ic_room_white_48dp, bitopt);
		//collecting bitmap pixels into an array
		int[] pixels = new int[bitmap.getHeight()*bitmap.getWidth()];
		bitmap.getPixels(pixels, 0, bitmap.getWidth(), 0, 0, bitmap.getWidth(), bitmap.getHeight());
		int alpha;
		for(int i = 0; i < pixels.length; i++){
			alpha = Color.alpha(pixels[i]);
			pixels[i]=color+(alpha<<24);
		}
		bitmap.setPixels(pixels, 0, bitmap.getWidth(), 0, 0, bitmap.getWidth(), bitmap.getHeight());
		BitmapDrawable drawable = new BitmapDrawable(getResources(),bitmap);
		drawable.setAntiAlias(true);
		drawable.setFilterBitmap(true);
		drawable.setDither(true);
		Bitmap newBitmap = drawable.getBitmap();
		return newBitmap;
	}
	
	
	/**Generate Custom Marker for passed location with the specified color
	 * @param color marker color*/
	public Bitmap generateIconPassedLocation(int color){
        BitmapFactory.Options bitopt=new BitmapFactory.Options();
        bitopt.inMutable=true;
        //resizing bitmap
        bitopt.inSampleSize=4;
		Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.ic_fiber_manual_record_white_48dp, bitopt);
		//collecting bitmap pixels into an array
		int[] pixels = new int[bitmap.getHeight()*bitmap.getWidth()];
		bitmap.getPixels(pixels, 0, bitmap.getWidth(), 0, 0, bitmap.getWidth(), bitmap.getHeight());
		int alpha;
		for(int i = 0; i < pixels.length; i++){
			alpha = Color.alpha(pixels[i]);
			pixels[i]=color+(alpha<<24);
		}
		bitmap.setPixels(pixels, 0, bitmap.getWidth(), 0, 0, bitmap.getWidth(), bitmap.getHeight());
		BitmapDrawable drawable = new BitmapDrawable(getResources(),bitmap);
		drawable.setAntiAlias(true);
		drawable.setFilterBitmap(true);
		drawable.setDither(true);
		Bitmap newBitmap = drawable.getBitmap();
		return newBitmap;
	}
	
    /**distance computation methode
     * in order figure out if a sample is to close the most recent one to be displayed on screen
     * @return true if the sample is to close*/
	public boolean toClose(Sample sample, Sample mostRecent){
		Location lastLocation = new Location("");
		lastLocation.setLatitude(mostRecent.getLatitude());
		lastLocation.setLongitude(mostRecent.getLongitude());

		Location sampleLocation = new Location("");
		sampleLocation.setLatitude(sample.getLatitude());
		sampleLocation.setLongitude(sample.getLongitude());

		float distanceInMeters = sampleLocation.distanceTo(lastLocation);
		return distanceInMeters<ConstantGUI.MIN_DISTANCE_IN_METERS_FOR_SAMPLE_DISPLAY; 
	}


	
	





}
