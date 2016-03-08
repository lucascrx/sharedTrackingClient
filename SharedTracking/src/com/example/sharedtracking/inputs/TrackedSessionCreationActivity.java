package com.example.sharedtracking.inputs;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import com.st.sharedtracking.R;
import com.example.sharedtracking.constants.Constants;
import com.example.sharedtracking.views.ConstantGUI;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout.LayoutParams;
import android.widget.Spinner;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

public class TrackedSessionCreationActivity extends Activity {
	
	/**string for dialog tab title setting**/
	public final static String ImmediateCreationTabTag = "Immediate";
	public final static String PreparedCreationTabTag = "Prepared";
	public final static String ContributionTabTag = "Contribution";
	
	/**starting Timestamp attribute for Starting Time*/
	private Timestamp activityStartingTime;
	/**starting Timestamp attribute for Starting Date*/
	private Timestamp activityStartingDate;
	/**starting Timestamp attribute for Ending Time*/
	private Timestamp activityEndingTime;
	/**starting Timestamp attribute for Ending Date*/
	private Timestamp activityEndingDate;
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tracked_session_creation);
        createContent();
	}
        
     public void onCreationDone() {
            	try{
	                //trigger session joining
            		//retrieving public ID
	            	TabHost input = (TabHost) findViewById(R.id.tabhost);
	            	int activeIndex = input.getCurrentTab();
	            	if(activeIndex==0){
	            		//immediate Creation has been selected	            	
	            		//retrieving Name
	            		EditText inputName = (EditText) findViewById(R.id.immediate_creation_name_setting_editText);
		            	String typedName = inputName.getText().toString();
		            	DialogInputSanitizer.sanitizeInputAsName(typedName);
		            	//retrieving Rate
		            	Spinner inputRate = (Spinner) findViewById(R.id.immediate_creation_rate_spinner);
		            	String temp = inputRate.getSelectedItem().toString();
		            	int typedRate = DialogInputConverter.convertRateToInt(temp);
		            	DialogInputSanitizer.sanitizeInputAsRate(typedRate);
		                //finishing and passing inputs
		                finishActivityWithImmediateCreation(typedName,typedRate);
	            	}else if(activeIndex==1){
	            		//prepared Creation has been selected
	            		//retrieving Name
	            		EditText inputName = (EditText) findViewById(R.id.prepared_creation_name_setting_editText);
		            	String typedName = inputName.getText().toString();
		            	DialogInputSanitizer.sanitizeInputAsName(typedName);
		            	//retrieving Rate
		            	Spinner inputRate = (Spinner) findViewById(R.id.prepared_creation_rate_spinner);
		            	String temp = inputRate.getSelectedItem().toString();
		            	int typedRate = DialogInputConverter.convertRateToInt(temp);
		            	DialogInputSanitizer.sanitizeInputAsRate(typedRate);
		            	//retrieving password
		            	EditText inputPassword = (EditText) findViewById(R.id.prepared_creation_password_setting_editText);
		            	String typedPassword = inputPassword.getText().toString();
		            	DialogInputSanitizer.sanitizeInputAsPassword(typedPassword);
		            	
		            	//retrieving starting Timestamp
		            	if(activityStartingDate==null){
		            		throw new DialogInputException(ConstantGUI.TOAST_LABEL_INPUT_EXCEPTION_STARTING_DATE_NOT_SET);
		            	}
		            	if(activityStartingTime==null){
		            		throw new DialogInputException(ConstantGUI.TOAST_LABEL_INPUT_EXCEPTION_STARTING_TIME_NOT_SET);
		            	}
		            	//Year, Month, Day
		            	Calendar startDateCalendar = Calendar.getInstance();
		            	startDateCalendar.setTimeInMillis(activityStartingDate.getTime());
		            	int startyear = startDateCalendar.get(Calendar.YEAR);
		            	int startmonth = startDateCalendar.get(Calendar.MONTH);
		            	int startday = startDateCalendar.get(Calendar.DAY_OF_MONTH);
		            	//Hour, Minute
		            	Calendar startTimeCalendar = Calendar.getInstance();
		            	startTimeCalendar.setTimeInMillis(activityStartingTime.getTime());
						int starthour = startTimeCalendar.get(Calendar.HOUR_OF_DAY);
		            	int startminute = startTimeCalendar.get(Calendar.MINUTE);
		            	//constructing merged timestamp
		        		Calendar startCalendar = Calendar.getInstance();
		        		startCalendar.set(Calendar.SECOND, 0);
		        		startCalendar.set(startyear,startmonth,startday,starthour, startminute);
		        		Timestamp startingTimestamp = new Timestamp(startCalendar.getTimeInMillis());
		            	DialogInputSanitizer.sanitizeInputAsStartingDateAlone(startingTimestamp);
		            	Locale current = getResources().getConfiguration().locale;
		            	SimpleDateFormat dateFormat = new SimpleDateFormat(Constants.TIMESTAMP_STRING_FORMAT,current);
		            	String typedStartTimestamp = dateFormat.format(startingTimestamp);
		            	
		            	//checking if ending Timestamp must be retrieved		            	
		            	String typedEndTimestamp = null;
		            	if(activityEndingDate!=null||activityEndingTime!=null){	 
		            		//at least ending date or ending time is set
		            		//retrieving ending Timestamp
			            	if(activityEndingDate==null){
			            		throw new DialogInputException(ConstantGUI.TOAST_LABEL_INPUT_EXCEPTION_ENDING_DATE_NOT_SET);
			            	}
			            	if(activityEndingTime==null){
			            		throw new DialogInputException(ConstantGUI.TOAST_LABEL_INPUT_EXCEPTION_ENDING_TIME_NOT_SET);
			            	}
			            	//Year, Month, Day
			            	Calendar endDateCalendar = Calendar.getInstance();
			            	endDateCalendar.setTimeInMillis(activityEndingDate.getTime());
			            	int endyear = endDateCalendar.get(Calendar.YEAR);
			            	int endmonth = endDateCalendar.get(Calendar.MONTH);
			            	int endday = endDateCalendar.get(Calendar.DAY_OF_MONTH);
			            	//Hour, Minute
			            	Calendar endTimeCalendar = Calendar.getInstance();
			            	endTimeCalendar.setTimeInMillis(activityEndingTime.getTime());
							int endhour = endTimeCalendar.get(Calendar.HOUR_OF_DAY);
			            	int endminute = endTimeCalendar.get(Calendar.MINUTE);
			            	//constructing merged timestamp
			        		Calendar endCalendar = Calendar.getInstance();
			        		endCalendar.set(Calendar.SECOND, 0);
			        		endCalendar.set(endyear,endmonth,endday,endhour, endminute);
			        		Timestamp endingTimestamp = new Timestamp(endCalendar.getTimeInMillis());
			        		DialogInputSanitizer.sanitizeInputAsEndingDate(endingTimestamp,startingTimestamp);
			        		typedEndTimestamp = dateFormat.format(endingTimestamp);            	
		            	}
		                //finishing and passing inputs
		            	finishActivityWithPreparedCreation(typedName,typedRate, typedPassword, typedStartTimestamp, typedEndTimestamp);
	            	}else if(activeIndex==2){
	            		//retrieving public ID
		            	EditText inputPublicID = (EditText) findViewById(R.id.contribution_publicID_editText);
		            	String typedToken = inputPublicID.getText().toString();
		            	DialogInputSanitizer.sanitizeInputAsPublicID(typedToken);
		            	//retrieving password
		            	EditText inputPassword = (EditText) findViewById(R.id.contribution_password_editText);
		            	String typedPassword = inputPassword.getText().toString();
		            	DialogInputSanitizer.sanitizeInputAsPassword(typedPassword);
		                //finishing and passing inputs
		                finishActivityWithContribution(typedToken, typedPassword);	
	            	}else{
	            		throw new DialogInputException("invalid Tab index");
	            	}     	
            	}catch(DialogInputException e){
            		e.printStackTrace();
            		//printing what was wrong to user
            		Toast.makeText(this,e.getMessage(), Toast.LENGTH_LONG).show();
            	}
                
            }
    

     /**Preparing intent with name and rate
      * then finishing*/
     public void finishActivityWithImmediateCreation(String name,int rate){
    	 Intent returnIntent = new Intent();
    	 returnIntent.putExtra(Constants.INTENT_LABEL_NAME, name);
    	 returnIntent.putExtra(Constants.INTENT_LABEL_RATE, rate);
    	 setResult(Constants.IMMEDIATE_SESSION_CREATION_RESULT,returnIntent);
    	 finish();
     }
     
     /**Preparing intent with name, rate, password and times
      * then finishing*/
     public void finishActivityWithPreparedCreation(String name, int rate, String password, String start, String end){
    	 Intent returnIntent = new Intent();
    	 returnIntent.putExtra(Constants.INTENT_LABEL_NAME, name);
    	 returnIntent.putExtra(Constants.INTENT_LABEL_RATE, rate);
    	 returnIntent.putExtra(Constants.INTENT_LABEL_PASSWORD, password);
    	 returnIntent.putExtra(Constants.INTENT_LABEL_STARTING_TIME, start);
    	 returnIntent.putExtra(Constants.INTENT_LABEL_ENDING_TIME, end);
    	 setResult(Constants.PREPARED_SESSION_CREATION_RESULT,returnIntent);
    	 finish(); 	 
     }
     
     /**Preparing intent with publicID and password
      * then finishing*/
     public void finishActivityWithContribution(String publicID, String password){
    	 Intent returnIntent = new Intent();
    	 returnIntent.putExtra(Constants.INTENT_LABEL_PUBLIC_ID, publicID);
    	 returnIntent.putExtra(Constants.INTENT_LABEL_PASSWORD, password);
    	 setResult(Constants.CONTRIBUTION_RESULT,returnIntent);
    	 finish();
     }
	
     
	public void createContent(){ 
        // Extract the TabHost  
        TabHost mTabHost = (TabHost) findViewById(R.id.tabhost);  
        mTabHost.setup();  
        
        // Create Immediate Creation Tab
        TabHost.TabSpec immediateCreationTab = mTabHost.newTabSpec(ImmediateCreationTabTag);  
        immediateCreationTab.setIndicator(getString(R.string.tracked_session_creation_immediate_tab_title));  
        immediateCreationTab.setContent(R.id.immediate_creation_layout);  
          
        
        // Create Prepared Creation Tab  
        TabHost.TabSpec preparedCreationTab = mTabHost.newTabSpec(PreparedCreationTabTag);  
        preparedCreationTab.setIndicator(getString(R.string.tracked_session_creation_prepared_tab_title));  
        preparedCreationTab.setContent(R.id.prepared_creation_layout);  

    	
        //Prepare Spinners for upload rate
        Spinner spinnerImmediateCreation = (Spinner) findViewById(R.id.immediate_creation_rate_spinner);
        Spinner spinnerPreparedCreation = (Spinner) findViewById(R.id.prepared_creation_rate_spinner);
        
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item) {

            @SuppressLint("NewApi")
			@Override
            public View getView(int position, View convertView, ViewGroup parent) {

                View v = super.getView(position, convertView, parent);
                if (position == getCount()) {
                	TextView hint = ((TextView)v.findViewById(android.R.id.text1));
                    hint.setText("");                   
                    ((TextView)v.findViewById(android.R.id.text1)).setHint(getItem(getCount())); //"Hint to be displayed"
                    
                }else{
                	TextView hint = ((TextView)v.findViewById(android.R.id.text1));
                    if (Build.VERSION.SDK_INT < 23) {
                        hint.setTextAppearance(TrackedSessionCreationActivity.this, android.R.style.TextAppearance_Medium);
                    } else {
                        hint.setTextAppearance(android.R.style.TextAppearance_Medium);
                    }  
                }

                return v;
            }       

            @Override
            public int getCount() {
                return super.getCount()-1; // you dont display last item. It is used as hint.
            }

        };

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        adapter.addAll(getResources().getStringArray(R.array.rates_array));
        adapter.add(getString(R.string.session_rate_title));
        spinnerImmediateCreation.setAdapter(adapter);
        spinnerImmediateCreation.setSelection(adapter.getCount()); //display hint
        spinnerPreparedCreation.setAdapter(adapter);
        spinnerPreparedCreation.setSelection(adapter.getCount()); //display hint
        
        
        
        
        
        mTabHost.addTab(immediateCreationTab);
        mTabHost.addTab(preparedCreationTab);  
        
        //Create contribution tab
        TabHost.TabSpec contributionTab = mTabHost.newTabSpec(ContributionTabTag);  
        contributionTab.setIndicator(getString(R.string.tracked_session_contribution_tab_title));  
        contributionTab.setContent(R.id.contribution_layout);  
        mTabHost.addTab(contributionTab); 
        
        
        //display Action Bar
        final ActionBar actionBar = getActionBar();	
        actionBar.setDisplayHomeAsUpEnabled(false);
        actionBar.setDisplayShowHomeEnabled(false);
        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setDisplayShowCustomEnabled(true);
        
        View customBar = getLayoutInflater().inflate(R.layout.tracked_session_creation_bar_layout, null);
        customBar.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
        customBar.findViewById(R.id.tracked_session_creation_bar_validation).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
            	onCreationDone();
            }
        });
        customBar.findViewById(R.id.tracked_session_creation_bar_back).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
            	finish();
            }
        });
        actionBar.setCustomView(customBar);
        
    }
    
    /**Pops up Starting time setting dialog and sets up corresponding listener*/
    public void onStartingTimeClicked(View v){
    	Calendar c = Calendar.getInstance();
    	if (activityStartingTime != null){
    		//if starting time has already been modified the dialog prints the last hour & minute set
    		//otherwise it prints the current ones
            c.setTimeInMillis(activityStartingTime.getTime());
    	}
    	int startingHour = c.get(Calendar.HOUR_OF_DAY);
    	int startingMinute = c.get(Calendar.MINUTE);  
    	//listener : when starting time set, the starting time attribute is updated
    	TimePickerDialog.OnTimeSetListener startingTimeSetListener = new TimePickerDialog.OnTimeSetListener() {
        	@Override
    		public void onTimeSet(TimePicker view, int newHour,int newMinute) {
	        		//gathering the starting hour and minute in a calendar object
	        		Calendar startingTimeCalendar = Calendar.getInstance();
	        		startingTimeCalendar.set(Calendar.HOUR_OF_DAY,newHour);
	        		startingTimeCalendar.set(Calendar.MINUTE,newMinute);
	        		Timestamp newStartingTime = new Timestamp(startingTimeCalendar.getTimeInMillis());
	        		//updating starting time attribute
	        		activityStartingTime = newStartingTime;
	        		//updating view with the associated string	
	            	Locale current = getResources().getConfiguration().locale;
	            	SimpleDateFormat dateFormat = new SimpleDateFormat(ConstantGUI.TIME_FORMATTING_STRING,current);
	            	String newStartingTimestampString = dateFormat.format(newStartingTime);	
	            	TextView startingTimeView = (TextView) findViewById(R.id.prepared_creation_startingTime_selector);
	            	startingTimeView.setText(newStartingTimestampString);
        	}
       };    
        TimePickerDialog startTimeDialog = new TimePickerDialog(this,startingTimeSetListener, startingHour, startingMinute,true);
        startTimeDialog.setTitle(getString(R.string.session_starting_time_title));
        startTimeDialog.show();
    }
    
    /**Pops up Starting Date setting dialog and sets up corresponding listener*/
    public void onStartingDateClicked(View v){
    	Calendar c = Calendar.getInstance();
    	if (activityStartingDate != null){
    		//if starting date has already been modified the dialog prints the last year, month & day set
    		//otherwise it prints the current ones
            c.setTimeInMillis(activityStartingDate.getTime());
    	}
    	int startingYear = c.get(Calendar.YEAR);
    	int startingMonth = c.get(Calendar.MONTH);
    	int startingDay = c.get(Calendar.DAY_OF_MONTH);  
    	//listener : when starting date set, the starting date attribute is updated
    	DatePickerDialog.OnDateSetListener startingDateSetListener = new DatePickerDialog.OnDateSetListener() {
 			@Override
			public void onDateSet(DatePicker view, int newYear, int newMonth, int newDay) {
	        		//gathering the starting year, month and day a calendar object
	        		Calendar startingDateCalendar = Calendar.getInstance();
	        		startingDateCalendar.set(Calendar.YEAR, newYear);
	        		startingDateCalendar.set(Calendar.MONTH,newMonth);
	        		startingDateCalendar.set(Calendar.DAY_OF_MONTH,newDay);
	        		Timestamp newStartingDate = new Timestamp(startingDateCalendar.getTimeInMillis());
	        		//updating starting date attribute
	        		activityStartingDate = newStartingDate;
	        		//updating view with the associated string	
	            	Locale current = getResources().getConfiguration().locale;
	            	SimpleDateFormat dateFormat = new SimpleDateFormat(ConstantGUI.DATE_FORMATTING_STRING,current);
	            	String newStartingTimestampString = dateFormat.format(newStartingDate);	
	            	TextView startingDateView = (TextView) findViewById(R.id.prepared_creation_startingDate_selector);
	            	startingDateView.setText(newStartingTimestampString);
        	}

       };    
        DatePickerDialog startDateDialog = new DatePickerDialog(this,startingDateSetListener, startingYear, startingMonth, startingDay);
        startDateDialog.setTitle(getString(R.string.session_starting_date_title));
        startDateDialog.show();
    }
    
    
    
	/**Pops up Ending time setting dialog and sets up corresponding listener*/
    public void onEndingTimeClicked(View v){
    	Calendar c = Calendar.getInstance();
    	if (activityEndingTime != null){
    		//if ending time has already been modified the dialog prints the last hour & minute set
    		//otherwise it prints the current ones
            c.setTimeInMillis(activityEndingTime.getTime());
    	}
    	int endingHour = c.get(Calendar.HOUR_OF_DAY);
    	int endingMinute = c.get(Calendar.MINUTE);  
    	//listener : when ending time set, the ending time attribute is updated
    	TimePickerDialog.OnTimeSetListener endingTimeSetListener = new TimePickerDialog.OnTimeSetListener() {
    		@Override
    		public void onTimeSet(TimePicker view, int newHour,int newMinute) {
	        		//gathering the ending hour and minute in a calendar object
	        		Calendar endingTimeCalendar = Calendar.getInstance();
	        		endingTimeCalendar.set(Calendar.HOUR_OF_DAY,newHour);
	        		endingTimeCalendar.set(Calendar.MINUTE,newMinute);
	        		Timestamp newEndingTime = new Timestamp(endingTimeCalendar.getTimeInMillis());
	        		//updating ending time attribute
	        		activityEndingTime = newEndingTime;
	        		//updating view with the associated string	
	            	Locale current = getResources().getConfiguration().locale;
	            	SimpleDateFormat dateFormat = new SimpleDateFormat(ConstantGUI.TIME_FORMATTING_STRING,current);
	            	String newEndingTimestampString = dateFormat.format(newEndingTime);	
	            	TextView endingTimeView = (TextView) findViewById(R.id.prepared_creation_endingTime_selector);
	            	endingTimeView.setText(newEndingTimestampString);
        	}
       };    
        TimePickerDialog endTimeDialog = new TimePickerDialog(this,endingTimeSetListener, endingHour, endingMinute,true);
        endTimeDialog.setTitle(getString(R.string.session_ending_time_title));
        endTimeDialog.show();
    }
    
    /**Pops up Ending Date setting dialog and sets up corresponding listener*/
    public void onEndingDateClicked(View v){
    	Calendar c = Calendar.getInstance();
    	if (activityEndingDate != null){
    		//if starting date has already been modified the dialog prints the last year, month & day set
    		//otherwise it prints the current ones
            c.setTimeInMillis(activityEndingDate.getTime());
    	}
    	int endingYear = c.get(Calendar.YEAR);
    	int endingMonth = c.get(Calendar.MONTH);
    	int endingDay = c.get(Calendar.DAY_OF_MONTH);  
    	//listener : when ending date set, the ending date attribute is updated
    	DatePickerDialog.OnDateSetListener endingDateSetListener = new DatePickerDialog.OnDateSetListener() {
 			@Override
			public void onDateSet(DatePicker view, int newYear, int newMonth, int newDay) {
	        		//gathering the starting year, month and day a calendar object
	        		Calendar endingDateCalendar = Calendar.getInstance();
	        		endingDateCalendar.set(Calendar.YEAR, newYear);
	        		endingDateCalendar.set(Calendar.MONTH,newMonth);
	        		endingDateCalendar.set(Calendar.DAY_OF_MONTH,newDay);
	        		Timestamp newEndingDate = new Timestamp(endingDateCalendar.getTimeInMillis());
	        		//updating ending date attribute
	        		activityEndingDate = newEndingDate;
	        		//updating view with the associated string	
	            	Locale current = getResources().getConfiguration().locale;
	            	SimpleDateFormat dateFormat = new SimpleDateFormat(ConstantGUI.DATE_FORMATTING_STRING,current);
	            	String newEndingTimestampString = dateFormat.format(newEndingDate);	
	            	TextView endingDateView = (TextView) findViewById(R.id.prepared_creation_endingDate_selector);
	            	endingDateView.setText(newEndingTimestampString);
        	}

       };    
        DatePickerDialog endDateDialog = new DatePickerDialog(this,endingDateSetListener, endingYear, endingMonth, endingDay);
        endDateDialog.setTitle(getString(R.string.session_ending_date_title));
        endDateDialog.show();
    }
    
    
}
