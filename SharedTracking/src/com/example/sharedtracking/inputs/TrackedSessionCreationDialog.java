package com.example.sharedtracking.inputs;

import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import com.example.sharedtracking.BaseActivity;
import com.example.sharedtracking.R;
import com.example.sharedtracking.IInputListener;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TabHost;
import android.widget.TimePicker;
import android.widget.Toast;

public class TrackedSessionCreationDialog extends DialogFragment {
	
	/**string for dialog tab title setting**/
	public final static String ImmediateCreationTabTag = "Immediate";
	public final static String PreparedCreationTabTag = "Prepared";
	public final static String ContributionTabTag = "Contribution";

	@Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        
        //setTitle
        builder.setTitle(R.string.tracked_session_creation_title_dialogue);
        

        // Get the layout inflater
        LayoutInflater inflater = getActivity().getLayoutInflater();
        // Set the View to the Dialog  
        final View view = createDialogContent(inflater);
        builder.setView(view);
        //set yes and cancel buttons
        builder.setPositiveButton(R.string.session_creation_validation, new DialogInterface.OnClickListener() {
            @SuppressWarnings({ "deprecation", "deprecation" })
			@Override
            public void onClick(DialogInterface dialog, int id) {
            	try{
	                //trigger session joining
            		//retrieving public ID
	            	TabHost input = (TabHost) view.findViewById(R.id.tabhost);
	            	int activeIndex = input.getCurrentTab();
	            	if(activeIndex==0){
	            		//immediate Creation has been selected	            	
	            		//retrieving Name
	            		EditText inputName = (EditText) view.findViewById(R.id.immediate_creation_name_setting_editText);
		            	String typedName = inputName.getText().toString();
		            	DialogInputSanitizer.sanitizeInputAsName(typedName);
		            	//retrieving Rate
		            	Spinner inputRate = (Spinner) view.findViewById(R.id.immediate_creation_rate_spinner);
		            	String temp = inputRate.getSelectedItem().toString();
		            	int typedRate = DialogInputConverter.convertRateToInt(temp);
		            	DialogInputSanitizer.sanitizeInputAsRate(typedRate);
		            	//calling callback
		                IInputListener callingActivity = (IInputListener) getActivity();
		                callingActivity.onImmediateSessionCreationReady(typedName,typedRate);	
	            	}else if(activeIndex==1){
	            		//prepared Creation has been selected
	            		//retrieving Name
	            		EditText inputName = (EditText) view.findViewById(R.id.prepared_creation_name_setting_editText);
		            	String typedName = inputName.getText().toString();
		            	DialogInputSanitizer.sanitizeInputAsName(typedName);
		            	//retrieving Rate
		            	Spinner inputRate = (Spinner) view.findViewById(R.id.prepared_creation_rate_spinner);
		            	String temp = inputRate.getSelectedItem().toString();
		            	int typedRate = DialogInputConverter.convertRateToInt(temp);
		            	DialogInputSanitizer.sanitizeInputAsRate(typedRate);
	            		//retrieving public ID
		            	EditText inputPublicID = (EditText) view.findViewById(R.id.prepared_creation_publicID_setting_editText);
		            	String typedToken = inputPublicID.getText().toString();
		            	DialogInputSanitizer.sanitizeInputAsPublicID(typedToken);
		            	//retrieving password
		            	EditText inputPassword = (EditText) view.findViewById(R.id.prepared_creation_password_setting_editText);
		            	String typedPassword = inputPassword.getText().toString();
		            	DialogInputSanitizer.sanitizeInputAsPassword(typedPassword);
		            	//retrieving starting Date
		            	DatePicker inputStartingDate = (DatePicker) view.findViewById(R.id.prepared_creation_startingDate_date_picker);
		            	TimePicker inputStartingTime = (TimePicker) view.findViewById(R.id.prepared_creation_startingDate_time_picker);
		            	int startyear = inputStartingDate.getYear();
		            	int startmonth = inputStartingDate.getMonth();
		            	int startday = inputStartingDate.getDayOfMonth();
						int starthour = inputStartingTime.getCurrentHour();
		            	int startminute = inputStartingTime.getCurrentMinute();
		        		Calendar startCalendar = Calendar.getInstance();
		        		startCalendar.set(Calendar.SECOND, 0);
		        		startCalendar.set(startyear,startmonth,startday,starthour, startminute);
		        		Timestamp startingDate = new Timestamp(startCalendar.getTimeInMillis());
		            	DialogInputSanitizer.sanitizeInputAsStartingDateAlone(startingDate);
		            	String typedStartDate = startingDate.toString();
		            	//retreiving ending date
		            	CheckBox checkEndTime = (CheckBox) view.findViewById(R.id.prepared_creation_endingDate_setting_checkBox);
		            	String typedEndDate = null;
		            	//if check box is checked, end timestamp is set. Otherwise, end timestamp is null
		            	if(checkEndTime.isChecked()){	            		
			            	DatePicker inputEndingDate = (DatePicker) view.findViewById(R.id.prepared_creation_endingDate_date_picker);
			            	TimePicker inputEndingTime = (TimePicker) view.findViewById(R.id.prepared_creation_endingDate_time_picker);
			            	int endyear = inputEndingDate.getYear();
			            	int endmonth = inputEndingDate.getMonth();
			            	int endday = inputEndingDate.getDayOfMonth();
							int endhour = inputEndingTime.getCurrentHour();
			            	int endminute = inputEndingTime.getCurrentMinute();	            	
			        		Calendar endCalendar = Calendar.getInstance();
			        		endCalendar.set(Calendar.SECOND, 0);
			        		endCalendar.set(endyear,endmonth,endday,endhour, endminute);
			        		Timestamp endingDate = new Timestamp(endCalendar.getTimeInMillis());
			            	DialogInputSanitizer.sanitizeInputAsEndingDate(endingDate,startingDate);
			            	typedEndDate = endingDate.toString();
		            	}
		            	//calling callback
		                IInputListener callingActivity = (IInputListener) getActivity();
		                callingActivity.onPreparedSessionCreationReady(typedName, typedRate, typedToken, typedPassword, typedStartDate, typedEndDate);
	            	}else if(activeIndex==2){
	            		//retrieving public ID
		            	EditText inputPublicID = (EditText) view.findViewById(R.id.contribution_publicID_editText);
		            	String typedToken = inputPublicID.getText().toString();
		            	DialogInputSanitizer.sanitizeInputAsPublicID(typedToken);
		            	//retrieving password
		            	EditText inputPassword = (EditText) view.findViewById(R.id.contribution_password_editText);
		            	String typedPassword = inputPassword.getText().toString();
		            	DialogInputSanitizer.sanitizeInputAsPassword(typedPassword);
		            	//calling callback
		                IInputListener callingActivity = (IInputListener) getActivity();
		                callingActivity.onSessionContributionReady(typedToken, typedPassword);	
	            	}else{
	            		throw new DialogInputException("invalid Tab index");
	            	}     	
            	}catch(DialogInputException e){
            		e.printStackTrace();
            		//printing what was wrong to user
            		Toast.makeText(getActivity(),e.getMessage(), Toast.LENGTH_LONG).show();
            	}
                
            }
        });
        builder.setNegativeButton(R.string.session_creation_cancelation, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
            	//TODO
            }
        });
        
        // Create the Alert Dialog  
        AlertDialog mDialog = builder.create(); 
        // Return the Dialog created  
        return mDialog; 
    }
    
    @SuppressLint("NewApi")
	@SuppressWarnings("deprecation")
	public View createDialogContent(LayoutInflater inflater){
    	View mView = inflater.inflate(R.layout.tracked_session_creation,null);  
        // Extract the TabHost  
        TabHost mTabHost = (TabHost) mView.findViewById(R.id.tabhost);  
        mTabHost.setup();  
        
        // Create Immediate Creation Tab
        TabHost.TabSpec immediateCreationTab = mTabHost.newTabSpec(ImmediateCreationTabTag);  
        immediateCreationTab.setIndicator(getString(R.string.tracked_session_creation_immediate_tab_title));  
        immediateCreationTab.setContent(R.id.immediate_creation_layout);  
        mTabHost.addTab(immediateCreationTab);  
        
        // Create Prepared Creation Tab  
        TabHost.TabSpec preparedCreationTab = mTabHost.newTabSpec(PreparedCreationTabTag);  
        preparedCreationTab.setIndicator(getString(R.string.tracked_session_creation_prepared_tab_title));  
        preparedCreationTab.setContent(R.id.prepared_creation_layout);  

    	
	    // Initialize Date and Time Pickers  
        Calendar currentDate = Calendar.getInstance();  
        DatePicker startDatePicker = (DatePicker) mView.findViewById(R.id.prepared_creation_startingDate_date_picker);  
        TimePicker startTimePicker = (TimePicker) mView.findViewById(R.id.prepared_creation_startingDate_time_picker);  
        DatePicker endDatePicker = (DatePicker) mView.findViewById(R.id.prepared_creation_endingDate_date_picker);  
        TimePicker endTimePicker = (TimePicker) mView.findViewById(R.id.prepared_creation_endingDate_time_picker);        
        
        startDatePicker.init(currentDate.get(Calendar.YEAR), currentDate.get(Calendar.MONTH), currentDate.get(Calendar.DAY_OF_MONTH), null);  
        endDatePicker.init(currentDate.get(Calendar.YEAR), currentDate.get(Calendar.MONTH), currentDate.get(Calendar.DAY_OF_MONTH), null);  
       
        startTimePicker.setIs24HourView(true);
        endTimePicker.setIs24HourView(true);
        
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
        	startTimePicker.setHour(currentDate.get(Calendar.HOUR_OF_DAY));
    		startTimePicker.setMinute(currentDate.get(Calendar.MINUTE));
    		endTimePicker.setHour(currentDate.get(Calendar.HOUR_OF_DAY));  
    		endTimePicker.setMinute(currentDate.get(Calendar.MINUTE));
        }else{
        	startTimePicker.setCurrentHour(currentDate.get(Calendar.HOUR_OF_DAY));
        	startTimePicker.setCurrentMinute(currentDate.get(Calendar.MINUTE));
        	endTimePicker.setCurrentHour(currentDate.get(Calendar.HOUR_OF_DAY));  
            endTimePicker.setCurrentMinute(currentDate.get(Calendar.MINUTE));
        }
        
        //initialize end time check box
        final LinearLayout endTimeLayout = (LinearLayout) mView.findViewById(R.id.prepared_creation_endingDate_setting_layout);
    	CheckBox checkEndTime = (CheckBox) mView.findViewById(R.id.prepared_creation_endingDate_setting_checkBox);
    	checkEndTime.setOnClickListener(new OnClickListener() {
    		  @Override
    		  public void onClick(View v) {
        	        //display end date time picker if checked
        			if (((CheckBox) v).isChecked()) {
        				endTimeLayout.setVisibility(View.VISIBLE);
        			}else{
        				endTimeLayout.setVisibility(View.GONE);
        			}
    		  }
    	});
        //Prepare Spinners for upload rate
        Spinner spinnerImmediateCreation = (Spinner) mView.findViewById(R.id.immediate_creation_rate_spinner);
        Spinner spinnerPreparedCreation = (Spinner) mView.findViewById(R.id.prepared_creation_rate_spinner);
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getActivity(),
             R.array.rates_array, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        spinnerImmediateCreation.setAdapter(adapter);
        spinnerPreparedCreation.setAdapter(adapter);
        mTabHost.addTab(preparedCreationTab);  
        
        //Create contribution tab
        TabHost.TabSpec contributionTab = mTabHost.newTabSpec(ImmediateCreationTabTag);  
        contributionTab.setIndicator(getString(R.string.tracked_session_contribution_tab_title));  
        contributionTab.setContent(R.id.contribution_layout);  
        mTabHost.addTab(contributionTab); 
        
        return mView;
    }
    
}
