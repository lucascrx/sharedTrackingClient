package com.example.sharedtracking.background;

import java.util.ArrayList;

import com.example.sharedtracking.BaseActivity;
import com.example.sharedtracking.session.Session;
import com.example.sharedtracking.views.ConstantGUI;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.provider.Settings.SettingNotFoundException;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

/**Class providing user location upon request*/
public class LocationAsynchronousResolver implements ConnectionCallbacks, OnConnectionFailedListener {
	
	/**Log tag for debugging*/
	private final static String Log_Tag = "Location Asynchronous Resolver : ";
	
    private LocationManager locationManager;
    private ArrayList<Session> sessionSet;
    private boolean gpsEnabled = false;
    private boolean networkEnabled = false;
    
    /**Inform if a prompt for turning location has already been displayed*/
    private boolean isUserPromptedForLocationSetting = false;
    
    /**Context set at session registration*/
    private Context context;
    
    /**Network Location Listener*/
    LocationListener locationListenerNetwork = new LocationListener() {
        public void onLocationChanged(Location location){
        	Log.d(Log_Tag,"new Network position resolved");
            onNewLocationResolved(location);
            locationManager.removeUpdates(this);
            locationManager.removeUpdates(locationListenerGps);
        }
        public void onProviderDisabled(String provider) {}
        public void onProviderEnabled(String provider) {}
        public void onStatusChanged(String provider, int status, Bundle extra) {}
    };
  
    /**GPS Location Listener*/
    LocationListener locationListenerGps = new LocationListener() {
        public void onLocationChanged(Location location){
        	Log.d(Log_Tag,"new GPS position resolved");
        	onNewLocationResolved(location);
            locationManager.removeUpdates(this);
            locationManager.removeUpdates(locationListenerNetwork);
        }
        public void onProviderDisabled(String provider) {}
        public void onProviderEnabled(String provider) {}
        public void onStatusChanged(String provider, int status, Bundle extra) {}
    };
    
    
    public LocationAsynchronousResolver(){
    	this.sessionSet = new ArrayList<Session>();
    }
    
    
  
    public void registerForLocation(Session sessionToInform,Context ctx){
    	this.context=ctx;
    	//registering session for receiving activities if not already registered
    	if(!sessionSet.contains(sessionToInform)){
    		Log.d(Log_Tag,"new session added to the session list to inform");
    		sessionSet.add(sessionToInform);
    	}else{
    		//if the session is already contained : it means that GPS location hasn't been resolved
    		//during session period then session is set as NOT_LOCALIZED
    		sessionToInform.unresolvedLocation();
    		//set already contains session triggering new checkAndTriggerLocation to check gps state
    		checkAndTriggerLocation();
    	}
    	//if the session is the first on added, location is requested
    	if(sessionSet.size()==1){
    		//the registered session is the first one of the set : asking for location resolution
    		Log.d(Log_Tag,"Registering Session : set was empty before, location resolution is requested");
    		checkAndTriggerLocation();
    	}else{
    		//the set wasn't empty before session adding, location is already being resolved
    		Log.d(Log_Tag,"Registering Session : set wasn't empty before, location resolution is not requested");
        }
    }
    	
    public void askForLocation(){	
    	try{ 
    		//check if location permission is granted
    		locationManager = (LocationManager)context.getSystemService(Context.LOCATION_SERVICE);
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            	int fineLocationPermissionCheck = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION);
            	int coarseLocationPermissionCheck = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION);
            	if(fineLocationPermissionCheck == PackageManager.PERMISSION_GRANTED
            			&& coarseLocationPermissionCheck == PackageManager.PERMISSION_GRANTED){
            		gpsEnabled=true;
            		networkEnabled = true;
            	}
            	if(fineLocationPermissionCheck == PackageManager.PERMISSION_GRANTED
            			&& coarseLocationPermissionCheck == PackageManager.PERMISSION_DENIED){
            		gpsEnabled=true;
            		networkEnabled = false;
            	}if(fineLocationPermissionCheck == PackageManager.PERMISSION_DENIED
            			&& coarseLocationPermissionCheck == PackageManager.PERMISSION_GRANTED){
            		gpsEnabled=false;
            		networkEnabled = true;
            	}if(fineLocationPermissionCheck == PackageManager.PERMISSION_DENIED
            			&& coarseLocationPermissionCheck == PackageManager.PERMISSION_DENIED){
            		gpsEnabled=false;
            		networkEnabled = false;
            	}
            	
            }else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT){
                try {
                	int locationMode = Settings.Secure.getInt(context.getContentResolver(), Settings.Secure.LOCATION_MODE);
                    if(locationMode==Settings.Secure.LOCATION_MODE_OFF){
                    	gpsEnabled = false;
                    	networkEnabled = false;
                    }else if(locationMode == Settings.Secure.LOCATION_MODE_HIGH_ACCURACY){
                    	gpsEnabled = true;
                    	networkEnabled = true;            	
                    }else if(locationMode == Settings.Secure.LOCATION_MODE_SENSORS_ONLY){
                    	gpsEnabled = true;
                    	networkEnabled = false;
                    }else if(locationMode == Settings.Secure.LOCATION_MODE_BATTERY_SAVING){
                    	gpsEnabled = false;
                    	networkEnabled = true;
                    }
                } catch (SettingNotFoundException e) {
                    e.printStackTrace();
                }
            }else{
                String locationProviders = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.LOCATION_PROVIDERS_ALLOWED);
                if (TextUtils.isEmpty(locationProviders)){
                	gpsEnabled = false;
                	networkEnabled = false;
                }
                else if (locationProviders.contains(LocationManager.GPS_PROVIDER) && locationProviders.contains(LocationManager.NETWORK_PROVIDER)){
                	gpsEnabled = true;
                	networkEnabled = true;
                }
                else if (locationProviders.contains(LocationManager.GPS_PROVIDER)){
                	gpsEnabled = true;
                	networkEnabled = false;
                }
                else if (locationProviders.contains(LocationManager.NETWORK_PROVIDER)){
                	gpsEnabled = false;
                	networkEnabled = true;
                }
            }
            
            
        	//don't start listeners if no provider is enabled
            if(!gpsEnabled && !networkEnabled){
                throw new PermissionException("no permission granted to get user location");
            }
            if(gpsEnabled){
            	locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListenerGps);
            }
            if(networkEnabled){
            	locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListenerNetwork);
            }
        }catch (PermissionException e){
        	e.printStackTrace();
        	Toast.makeText(context, ConstantGUI.TOAST_LABEL_FOR_LOCATION_RESOLUTION_FAILURE, Toast.LENGTH_LONG).show();
        }
    }
    
    public void onNewLocationResolved(Location loc){
    	//informing every registered session
    	for(Session session : this.sessionSet){
    		session.newLocationResolved(loc);
    	}
    	//flushing the array list
    	this.sessionSet.clear();
    }

    
    public void checkAndTriggerLocation(){
    	Log.d(Log_Tag,"configuring location setting");
    	GoogleApiClient mGoogleApiClient = new GoogleApiClient.Builder(this.context).addApi(LocationServices.API)
											         .addConnectionCallbacks(this)
											         .addOnConnectionFailedListener(this)
											         .build();

        mGoogleApiClient.connect();
        
        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        //TODO make it depend on rate array
        locationRequest.setFastestInterval(7 * 1000);
         
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
					         .addLocationRequest(locationRequest);
    	 
        builder.setAlwaysShow(true);
        PendingResult<LocationSettingsResult> result =
                 LocationServices.SettingsApi.checkLocationSettings(mGoogleApiClient, builder.build());
        
        result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
            @Override
            public void onResult(LocationSettingsResult result) {
                final Status status = result.getStatus();
                
                switch (status.getStatusCode()) {
                    case LocationSettingsStatusCodes.SUCCESS:
                    	Log.d(Log_Tag," result callback : location setting is OK");
                    	//response received
                    	isUserPromptedForLocationSetting = false;
                        // All location settings are satisfied. The client can initialize location
                        // requests here.
                    	askForLocation();
                        break;
                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                    	Log.d(Log_Tag," result callback : location setting is NOT OK, user must be prompted");
                        // Location settings are not satisfied. But could be fixed by showing the user
                        // a dialog. : Asking current Context for display dialog:
                    	//LocationAsynchronousResolver Context is a Base Activity, since it is set by Session Manager
                        if(!isUserPromptedForLocationSetting){
                        	BaseActivity currentActivity = (BaseActivity) context;
                        	currentActivity.displayLocationSettingDialog(status);
                        	isUserPromptedForLocationSetting = true;
                        }else{
                        	onLocationForbidden();
                        }
                        	
                        break;
                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                    	Log.d(Log_Tag," result callback : location setting is NOT OK and UNAVAILABLE");
                        // Location settings are not satisfied. However, we have no way to fix the
                        // settings so we won't show the dialog.
                    	onLocationForbidden();
                        break;
                }
            }
        });
    }
    
    /**When user has refused to turn on location service after being prompted*/
    public void onLocationForbidden(){
    	Log.d(Log_Tag," turning to NOT LOCALIZED the registered sessions ");
    	//informing every session
    	for(Session session : sessionSet){
    		session.unresolvedLocation();
    	}
    	//flushing the array list
    	this.sessionSet.clear();
    	
    }
    
    /**When user has accepted to turn on location service after being prompted*/
    public void onLocationAccepted(){
    	//moving to location resolution
    	Log.d(Log_Tag," Proceeding to location resolving ");
    	askForLocation();	
    }

    /**Called by the manager when a session is deleted*/
    public void onSessionSuppressed(Session session){
    	this.sessionSet.remove(session);
    }

	@Override
	public void onConnected(Bundle arg0) {
		// TODO Auto-generated method stub
		
	}



	@Override
	public void onConnectionSuspended(int arg0) {
		// TODO Auto-generated method stub
		
	}



	@Override
	public void onConnectionFailed(ConnectionResult arg0) {	
	}
    
}
