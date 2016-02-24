package com.example.sharedtracking.views;


import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.TimeZone;

import android.graphics.Color;
import android.view.View;
import android.widget.TextView;

public class ConstantGUI {
	
	/**label for parameters*/
	public final static String LABEL_SESSION_NAME = "session_name";
	public final static String LABEL_UPLOAD_RATE = "rate";
	public final static String LABEL_PUBLIC_ID = "public_token";
	public final static String LABEL_PASSWORD = "password";
	public final static String LABEL_PRIVATE_ID = "private_token";
	public final static String LABEL_LATITUDE = "latitude";
	public final static String LABEL_LONGITUDE = "longitude";
	public final static String LABEL_DEVICE_NAME = "device_name";
	public final static String LABEL_DEVICE_ID = "device_token";
	public final static String LABEL_LOOKUP_INDEX = "expected_index";
	public final static String LABEL_STARTING_TIME = "starting_time";
	public final static String LABEL_ENDING_TIME = "ending_time";
	public final static String LABEL_SESSION_TYPE = "session_type";
	
	public static final String TOAST_LABEL_NETWORK_CONNECTION_ISSUE = "Server Unreachable";
	public static final String TOAST_LABEL_PARAMETER_UPDATE_SUCCESS = "Operation Succeeded : ";
	public static final String TOAST_LABEL_PARAMETER_UPDATE_FAILURE = "Operation Failed : ";
	
	public static final String TOAST_LABEL_FOR_SESSION_CREATION = "Session Creation";
	public static final String TOAST_LABEL_FOR_TOKEN_ISSUE = ", Token might be already used";
	public static final String TOAST_LABEL_FOR_NAME_UPDATE = "Update of Session Name";
	public static final String TOAST_LABEL_FOR_RATE_UPDATE = "Update of Session Upload Period";
	public static final String TOAST_LABEL_FOR_STARTING_TIME_UPDATE = "Update of Session Starting Time";
	public static final String TOAST_LABEL_FOR_ENDING_TIME_UPDATE = "Update of Session Ending Time";
	
	public static final String TOAST_LABEL_FOR_LOCATION_RESOLUTION_FAILURE = "Location can't be resolved";
	
	public static final String TOAST_LABEL_EXISTING_HOSTED_SESSION = "This Session is already hosted";
	public static final String TOAST_LABEL_EXISTING_JOINED_SESSION = "This Session has already been joined";
	
	
	public static final String DEFAULT_VALUE_SESSION_NAME = "...";
	public static final String DEFAULT_VALUE_SESSION_RATE = "...";
	
	public static final String DEFAULT_VALUE_START_TIME = "...";
	public static final String DEFAULT_VALUE_START_DATE = "...";
	public static final String DEFAULT_VALUE_END_TIME = "...";
	public static final String DEFAULT_VALUE_END_DATE = "...";
	
	public static final String TIME_DATE_FORMATTING_STRING = "HH:mm dd/MM/yyyy";
	public static final String DATE_FORMATTING_STRING = "dd/MM/yyyy";
	public static final String DATE_FORMATTING_STRING_SMALL = "dd/MM";
	public static final String TIME_FORMATTING_STRING = "HH:mm";
	public static final String SAMPLING_INFO_PREFIX = "Period : ";
	

	public static final int MIN_DISTANCE_IN_METERS_FOR_SAMPLE_DISPLAY = 30;
	public static final int LIGHT_GREY_COLOR = Color.rgb(237,237,237);
	
	/**Handlers to fix view overlapping after display (for low resolution screens)*/
	
	
	
	
	/**Handler for session dates, in case of overlapping, the year is not printed*/
	public static class DateOverlappingRunnable implements Runnable{
		private TextView view;
		private Timestamp date;
		private Locale current;
		
		public DateOverlappingRunnable(TextView viewToUpdate,Timestamp dateToDisplay, Locale cur){
			this.view = viewToUpdate;
			this.date = dateToDisplay;
			this.current = cur;
		}

		@Override
		public void run() {
	        int lineCnt = view.getLineCount();
	        if(lineCnt>1){
	        	//formatting date under a new format
				SimpleDateFormat dateFormatter = new SimpleDateFormat(ConstantGUI.DATE_FORMATTING_STRING_SMALL,current);
				dateFormatter.setTimeZone(TimeZone.getDefault());
				String newDate = dateFormatter.format(date);
				view.setText(newDate);
	        }
	        this.view.setVisibility(View.VISIBLE);
		}
		
		
	}
}
