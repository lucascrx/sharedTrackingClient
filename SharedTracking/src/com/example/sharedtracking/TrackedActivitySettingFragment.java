package com.example.sharedtracking;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Locale;

import com.example.sharedtracking.constants.Constants;
import com.example.sharedtracking.inputs.DialogInputConverter;
import com.example.sharedtracking.inputs.DialogInputException;
import com.example.sharedtracking.inputs.DialogInputSanitizer;
import com.example.sharedtracking.session.HostedSession;
import com.example.sharedtracking.views.ConstantGUI;
import com.st.sharedtracking.R;

import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.TimePickerDialog;
import android.content.DialogInterface;

public class TrackedActivitySettingFragment extends DialogFragment{
	
	/**Log Tag For debugging purposes*/
	private final static String LogTag = "Tracked Activity Preferences";
 
	/**Hosted Session handled by the fragment*/
	private HostedSession updatedSession = null;
	
	 @Override
	 public Dialog onCreateDialog(Bundle savedInstanceState) {
	        Dialog dialog = super.onCreateDialog(savedInstanceState);
	        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
	        dialog.setCanceledOnTouchOutside(true);
	        return dialog;
	 }
	 
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
    	      Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getDialog().getWindow().setGravity(Gravity.END | Gravity.TOP);
        
        View view = inflater.inflate(R.layout.preferences_tracked_session, container, false);
        
 
        //retrieving session object
        updatedSession = (HostedSession) ((TrackedActivity) getActivity()).getSessionManager().getSessionList()
        		.get(((TrackedActivity) getActivity()).getSessionIndex());

        
        	//NameEditTextPreference Initialization
        	Button sessionNamePref = (Button) view.findViewById(R.id.pref_session_name);
            sessionNamePref.setOnClickListener(new OnClickListener() {
    		    @Override
    		    public void onClick(View v) {
    		        try {
    		        	showSessionNameDialog();	        	
    				} catch (GraphicalException e) {
    					e.printStackTrace();
    				}	
    		    }
    		});
 
        
    	//RateListPreference Initialization
        Button sessionRatePref = (Button) view.findViewById(R.id.pref_session_rate);
        sessionRatePref.setOnClickListener(new OnClickListener() {
		    @Override
		    public void onClick(View v) {
		        try {
		        	showUploadRateDialog();	        	
				} catch (GraphicalException e) {
					e.printStackTrace();
				}
		    }
		});

        
        
        
        //Adding Date Picker for Starting Date
		Button startingDatePref = (Button) view.findViewById(R.id.pref_starting_date);
		startingDatePref.setOnClickListener(new OnClickListener() {
		    @Override
		    public void onClick(View v) {
		        try {
					showStartingDateDialog();
				} catch (GraphicalException e) {
					e.printStackTrace();
				}
		    }
		});
		
		//Adding Time Picker for Starting Time
		Button startingTimePref = (Button) view.findViewById(R.id.pref_starting_time);
		startingTimePref.setOnClickListener(new OnClickListener() {
		    @Override
		    public void onClick(View v) {
		        try {
					showStartingTimeDialog();
				} catch (GraphicalException e) {
					e.printStackTrace();
				}
		    }
		});
		
		
		//Adding Date Picker for Ending Date
		Button endingDatePref = (Button) view.findViewById(R.id.pref_ending_date);
		endingDatePref.setOnClickListener(new OnClickListener() {
		    @Override
		    public void onClick(View v) {
		        try {
					showEndingDateDialog();
				} catch (GraphicalException e) {
					e.printStackTrace();
				}	
		    }
		});
		
		//Adding Time Picker for Ending Time
		Button endingTimePref = (Button) view.findViewById(R.id.pref_ending_time);
		endingTimePref.setOnClickListener(new OnClickListener() {
		    @Override
		    public void onClick(View v) {
		        try {
					showEndingTimeDialog();
				} catch (GraphicalException e) {
					e.printStackTrace();
				}
		    }
		});
		return view;
        
        
        
    }
    
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        getView().setBackgroundColor(ConstantGUI.LIGHT_GREY_COLOR);
        getView().setClickable(true);
    }

    /**Pops up session name setting dialog and sets up corresponding listener
     * @throws GraphicalException */
    private void showSessionNameDialog() throws GraphicalException {
    	if(this.updatedSession==null){
    		throw new GraphicalException("Upload rate update : Updated Session is null");
    	}
    	String currentName = updatedSession.getName();
    	AlertDialog.Builder builder = new Builder(getActivity());
    	builder.setTitle(R.string.pref_session_name_title);
    	final EditText input = new EditText(getActivity());
    	input.setText(currentName);
    	input.setMaxLines(1);
    	input.setInputType(InputType.TYPE_CLASS_TEXT);
    	
    	builder.setView(input);
    	// Set up the buttons
    	builder.setPositiveButton(R.string.session_creation_validation, new DialogInterface.OnClickListener() { 
    	    @Override
    	    public void onClick(DialogInterface dialog, int which) {
    	    	try{
	    	        String newName = input.getText().toString();
	        		DialogInputSanitizer.sanitizeInputAsName(newName);
	        		updatedSession.updateName(newName);
    	    	}catch(DialogInputException e){
	        		e.printStackTrace();
	        		//printing what was wrong to user
	        		Toast.makeText(getActivity(),e.getMessage(), Toast.LENGTH_LONG).show();
    	    	}
    	        dialog.cancel();
    	    }
    	});
    	builder.setNegativeButton(R.string.session_creation_cancelation, new DialogInterface.OnClickListener() {
    	    @Override
    	    public void onClick(DialogInterface dialog, int which) {
    	    	dialog.cancel();
    	    }
    	});
    	
    	AlertDialog levelDialog = builder.create();
        levelDialog.show();
    }
    
    /**Pops up upload rate setting dialog and sets up corresponding listener
     * @throws GraphicalException */
    private void showUploadRateDialog() throws GraphicalException {
    	if(this.updatedSession==null){
    		throw new GraphicalException("Upload rate update : Updated Session is null");
    	}
    	int currentRate = updatedSession.getUploadRate();
    	String rateString = DialogInputConverter.convertRateToString(currentRate);
    	AlertDialog.Builder builder = new Builder(getActivity());
    	builder.setTitle(R.string.pref_session_rate_title);
    	final String[] rateList = getResources().getStringArray(R.array.rates_array); 
    	int currentIndex = Arrays.asList(rateList).indexOf(rateString);
    	builder.setSingleChoiceItems(rateList, currentIndex, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int item) {        
                String selectedRate = rateList[item];
                int rate = DialogInputConverter.convertRateToInt(selectedRate);
                Log.d(LogTag,"Listener for Upload Rate new rate Defined, item: "+item+" String: "+selectedRate+" Value: "+rate);
                if(rate!=0){
                	updatedSession.updateUploadRate(rate);
                }
                dialog.dismiss();   
            }
        });
    	AlertDialog dialog = builder.create();
    	dialog.show();
    }

    /**Pops up Starting date setting dialog and sets up corresponding listener
     * @throws GraphicalException */
    private void showStartingDateDialog() throws GraphicalException{
    	if(this.updatedSession==null){
    		throw new GraphicalException("Starting Date update : Updated Session is null");
    	}
    	Timestamp originalStartingTimestamp = this.updatedSession.getStartingTime();
        final Calendar c = Calendar.getInstance();
        c.setTimeInMillis(originalStartingTimestamp.getTime());
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);     
        DatePickerDialog.OnDateSetListener startingDateSetListener = new DatePickerDialog.OnDateSetListener() {
        	public void onDateSet(DatePicker view, int newYear, int newMonth, int newDay) {
        		try{
	        		Log.d(LogTag,"Listener for Starting Date woken up with values : Day :"+newDay+
	        				", Month : "+newMonth+", Year : "+newYear);
	        		//constructing the new timestamp to upload
	        		Calendar newCalendar = Calendar.getInstance();
	        		newCalendar.set(Calendar.SECOND, 0);
	        		newCalendar.set(newYear, newMonth, newDay, c.get(Calendar.HOUR_OF_DAY),c.get(Calendar.MINUTE));
	        		Timestamp newStartingTimestamp = new Timestamp(newCalendar.getTimeInMillis());
	        		Timestamp endingTimestamp = updatedSession.getEndingTime();
	        		if(endingTimestamp==null){
	        			DialogInputSanitizer.sanitizeInputAsStartingDateAlone(newStartingTimestamp);
	        		}else{
	        			DialogInputSanitizer.sanitizeStartTime(newStartingTimestamp,endingTimestamp);
	        		}
	        		DialogInputSanitizer.verifyStartingTimeSynchro(newStartingTimestamp,updatedSession.getStartingTime());
	            	Locale current = getActivity().getResources().getConfiguration().locale;
	            	SimpleDateFormat dateFormat = new SimpleDateFormat(Constants.TIMESTAMP_STRING_FORMAT,current);
	            	String newStartingTimestampString = dateFormat.format(newStartingTimestamp);	
	        		updatedSession.updateStartingTime(newStartingTimestampString);
	        	}catch(DialogInputException e){
	        		e.printStackTrace();
	        		//printing what was wrong to user
	        		Toast.makeText(getActivity(),e.getMessage(), Toast.LENGTH_LONG).show();
	        	}
        	}
       };     
        DatePickerDialog startDateDialog = new DatePickerDialog(getActivity(),startingDateSetListener, year, month, day);
        startDateDialog.setTitle(getActivity().getString(R.string.pref_session_starting_date_title));
        startDateDialog.setMessage(getActivity().getString(R.string.pref_session_starting_message));
        startDateDialog.show();
        Log.d(LogTag,"Dialog open for Starting Date Update");
    }
    
    /**Pops up Starting time setting dialog and sets up corresponding listener
     * @throws GraphicalException */
    private void showStartingTimeDialog() throws GraphicalException{
    	if(this.updatedSession==null){
    		throw new GraphicalException("Starting Time update : Updated Session is null");
    	}
    	Timestamp originalStartingTimestamp = this.updatedSession.getStartingTime();
        final Calendar c = Calendar.getInstance();
        c.setTimeInMillis(originalStartingTimestamp.getTime());
        int hour = c.get(Calendar.HOUR_OF_DAY);
        int minute = c.get(Calendar.MINUTE);  
        TimePickerDialog.OnTimeSetListener startingTimeSetListener = new TimePickerDialog.OnTimeSetListener() {
        	public void onTimeSet(TimePicker view, int newHour,int newMinute) {
        		try{
	        		Log.d(LogTag,"Listener for Starting Time woken up with values : Hour :"+newHour+
	        				", Minute : "+newMinute);
	        		//constructing the new timestamp to upload
	        		Calendar newCalendar = Calendar.getInstance();
	        		newCalendar.set(Calendar.SECOND, 0);
	        		newCalendar.set(c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH),
	        				newHour, newMinute);
	        		Timestamp newStartingTimestamp = new Timestamp(newCalendar.getTimeInMillis());
	        		Timestamp endingTimestamp = updatedSession.getEndingTime();
	        		if(endingTimestamp==null){
	        			DialogInputSanitizer.sanitizeInputAsStartingDateAlone(newStartingTimestamp);
	        		}else{
	        			DialogInputSanitizer.sanitizeStartTime(newStartingTimestamp,endingTimestamp);
	        		}
	        		DialogInputSanitizer.verifyStartingTimeSynchro(newStartingTimestamp,updatedSession.getStartingTime());
	        		
	            	Locale current = getActivity().getResources().getConfiguration().locale;
	            	SimpleDateFormat dateFormat = new SimpleDateFormat(Constants.TIMESTAMP_STRING_FORMAT,current);
	            	String newStartingTimestampString = dateFormat.format(newStartingTimestamp);	
	        		updatedSession.updateStartingTime(newStartingTimestampString);
	        		
	        	}catch(DialogInputException e){
	        		e.printStackTrace();
	        		//printing what was wrong to user
	        		Toast.makeText(getActivity(),e.getMessage(), Toast.LENGTH_LONG).show();
	        	}
        	}
       };     
        TimePickerDialog startTimeDialog = new TimePickerDialog(getActivity(),startingTimeSetListener, hour, minute,true);
        startTimeDialog.setTitle(getActivity().getString(R.string.pref_session_starting_time_title));
        startTimeDialog.setMessage(getActivity().getString(R.string.pref_session_starting_message));
        startTimeDialog.show();
        Log.d(LogTag,"Dialog open for Starting Time Update");
    }
    
    
    /**Pops up ending date setting dialog and sets up corresponding listener
     * @throws GraphicalException */
    private void showEndingDateDialog() throws GraphicalException{
    	if(this.updatedSession==null){
    		throw new GraphicalException("Ending Date update : Updated Session is null");
    	}
    	Timestamp originalEndingTimestamp = this.updatedSession.getEndingTime();
        final Calendar c = Calendar.getInstance();
        if(originalEndingTimestamp!=null){
        	c.setTimeInMillis(originalEndingTimestamp.getTime());	
        }else{
        	//initializing calendar with starting time
        	Timestamp originalStartingTimestamp = this.updatedSession.getStartingTime();
        	c.setTimeInMillis(originalStartingTimestamp.getTime());
        }
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);   
        DatePickerDialog.OnDateSetListener endingDateSetListener = new DatePickerDialog.OnDateSetListener() {
            public void onDateSet(DatePicker view, int newYear, int newMonth, int newDay) {
            	try{
	            	Log.d(LogTag,"Listener for Ending Date woken up with values : Day :"+newDay+
	            			", Month : "+newMonth+", Year : "+newYear);
	            	//constructing the new timestamp to upload
	            	Calendar newCalendar = Calendar.getInstance();
	            	newCalendar.set(Calendar.SECOND, 0);
	            	newCalendar.set(newYear, newMonth, newDay, c.get(Calendar.HOUR_OF_DAY), c.get(Calendar.MINUTE));
	            	Timestamp newEndingTimestamp = new Timestamp(newCalendar.getTimeInMillis());
	            	DialogInputSanitizer.sanitizeInputAsEndingDate(newEndingTimestamp,updatedSession.getStartingTime());
	            	DialogInputSanitizer.verifyEndingTimeSynchro(newEndingTimestamp,updatedSession.getEndingTime());
	        		
	            	Locale current = getActivity().getResources().getConfiguration().locale;
	            	SimpleDateFormat dateFormat = new SimpleDateFormat(Constants.TIMESTAMP_STRING_FORMAT,current);
	            	String newEndingTimestampString = dateFormat.format(newEndingTimestamp);	
	        		updatedSession.updateEndingTime(newEndingTimestampString);
	            	
	        	}catch(DialogInputException e){
	        		e.printStackTrace();
	        		//printing what was wrong to user
	        		Toast.makeText(getActivity(),e.getMessage(), Toast.LENGTH_LONG).show();
	        	}
            }
       };     
        DatePickerDialog endDateDialog = new DatePickerDialog(getActivity(),endingDateSetListener, year, month, day);
        endDateDialog.setTitle(getActivity().getString(R.string.pref_session_ending_date_title));
        endDateDialog.setMessage(getActivity().getString(R.string.pref_session_ending_message));
        endDateDialog.show();
        Log.d(LogTag,"Dialog open for Ending Date Update");
    }
    
    
    /**Pops up Ending time setting dialog and sets up corresponding listener
     * @throws GraphicalException */
    private void showEndingTimeDialog() throws GraphicalException{
    	if(this.updatedSession==null){
    		throw new GraphicalException("Ending Time update : Updated Session is null");
    	}
    	Timestamp originalEndingTimestamp = this.updatedSession.getEndingTime();
        final Calendar c = Calendar.getInstance();
        if(originalEndingTimestamp!=null){
        	c.setTimeInMillis(originalEndingTimestamp.getTime());	
        }else{
        	//initializing calendar with starting time
        	Timestamp originalStartingTimestamp = this.updatedSession.getStartingTime();
        	c.setTimeInMillis(originalStartingTimestamp.getTime());
        }
        int hour = c.get(Calendar.HOUR_OF_DAY);
        int minute = c.get(Calendar.MINUTE);  
        TimePickerDialog.OnTimeSetListener endingTimeSetListener = new TimePickerDialog.OnTimeSetListener() {
        	public void onTimeSet(TimePicker view, int newHour,int newMinute) {
        		try{
	        		Log.d(LogTag,"Listener for Ending Time woken up with values : Hour :"+newHour+
	        				", Minute : "+newMinute);
	        		//constructing the new timestamp to upload
	        		Calendar newCalendar = Calendar.getInstance();
	        		newCalendar.set(Calendar.SECOND, 0);
	        		newCalendar.set(c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH),
	        				newHour, newMinute);
	        		Timestamp newEndingTimestamp = new Timestamp(newCalendar.getTimeInMillis());
	        		DialogInputSanitizer.sanitizeInputAsEndingDate(newEndingTimestamp,updatedSession.getStartingTime());
	        		DialogInputSanitizer.verifyEndingTimeSynchro(newEndingTimestamp,updatedSession.getEndingTime());
	        		
	        		Locale current = getActivity().getResources().getConfiguration().locale;
	            	SimpleDateFormat dateFormat = new SimpleDateFormat(Constants.TIMESTAMP_STRING_FORMAT,current);
	            	String newEndingTimestampString = dateFormat.format(newEndingTimestamp);
	            	updatedSession.updateEndingTime(newEndingTimestampString);
	        	}catch(DialogInputException e){
	        		e.printStackTrace();
	        		//printing what was wrong to user
	        		Toast.makeText(getActivity(),e.getMessage(), Toast.LENGTH_LONG).show();
	        	}
        	}
       };     
        TimePickerDialog endingTimeDialog = new TimePickerDialog(getActivity(),endingTimeSetListener, hour, minute,true);
        endingTimeDialog.setTitle(getActivity().getString(R.string.pref_session_ending_time_title));
        endingTimeDialog.setMessage(getActivity().getString(R.string.pref_session_ending_message));
        endingTimeDialog.show();
        Log.d(LogTag,"Dialog open for Ending Time Update");
    }
    
    /**Callback method so that a NameEditTextPreference can pass the typed session Name*/
    public void onNewNameSet(String typedName){
    	try{
    		Log.d(LogTag,"Listener for Session Name Woken up with new name : "+typedName);
        	if(this.updatedSession==null){
        		throw new GraphicalException("Session Name update : Updated Session is null");
        	}
        	DialogInputSanitizer.sanitizeInputAsName(typedName);
        	updatedSession.updateName(typedName);
    	}catch(GraphicalException e){
    		e.printStackTrace();
    	} catch (DialogInputException e) {
			e.printStackTrace();
		}
    }
    
    /**Callback method so that a RateListPreference can pass the typed session Rate*/
    public void onNewRateSet(String selectedRate){
    	try{
    		Log.d(LogTag,"Listener for Session Rate Woken up with new rate : "+selectedRate);
        	if(this.updatedSession==null){
        		throw new GraphicalException("Session Rate update : Updated Session is null");
        	}
        	int convertedRate = DialogInputConverter.convertRateToInt(selectedRate);
        	DialogInputSanitizer.sanitizeInputAsRate(convertedRate);
        	updatedSession.updateUploadRate(convertedRate);
    	}catch(GraphicalException e){
    		e.printStackTrace();
    	} catch (DialogInputException e) {
			e.printStackTrace();
		}
    }
    
    
}
