package com.example.sharedtracking.views;


import android.graphics.Color;

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
	public static final String TOAST_LABEL_FOR_NAME_UPDATE = "Updating Session Name";
	public static final String TOAST_LABEL_FOR_RATE_UPDATE = "Updating Session Upload Period";
	public static final String TOAST_LABEL_FOR_STARTING_TIME_UPDATE = "Updating Session Starting Time";
	public static final String TOAST_LABEL_FOR_ENDING_TIME_UPDATE = "Updating Session Ending Time";
	
	public static final String TOAST_LABEL_FOR_LOCATION_RESOLUTION_FAILURE = "Location can't be resolved";
	
	public static final String TOAST_LABEL_EXISTING_HOSTED_SESSION = "This Session is already hosted";
	public static final String TOAST_LABEL_EXISTING_JOINED_SESSION = "This Session has already been joined";
	
	/**label for toast when session creation is not done correctly*/
	
	public static final String TOAST_LABEL_INPUT_EXCEPTION_STARTING_TIME_NOT_SET = "Starting time is not set";
	public static final String TOAST_LABEL_INPUT_EXCEPTION_STARTING_DATE_NOT_SET = "Starting date is not set";
	public static final String TOAST_LABEL_INPUT_EXCEPTION_START_NOT_SET= "Please define when session should start";
	public static final String TOAST_LABEL_INPUT_EXCEPTION_START_OCCURED = "Starting time has already occured";
	public static final String TOAST_LABEL_INPUT_EXCEPTION_OLD_START_OCCURED = "Starting time cannot be changed once session is running";
	public static final String TOAST_LABEL_INPUT_EXCEPTION_START_AFTER_END = "Starting time is after ending time";
	public static final String TOAST_LABEL_INPUT_EXCEPTION_START_WRONG_SCHEDULING = "Starting time can only be postponed";
	
	public static final String TOAST_LABEL_INPUT_EXCEPTION_ENDING_TIME_NOT_SET = "Ending time is not set";
	public static final String TOAST_LABEL_INPUT_EXCEPTION_ENDING_DATE_NOT_SET = "Ending date is not set";
	public static final String TOAST_LABEL_INPUT_EXCEPTION_END_NOT_SET= "Please define when session should end";
	public static final String TOAST_LABEL_INPUT_EXCEPTION_END_OCCURED = "Ending time has already occured";
	public static final String TOAST_LABEL_INPUT_EXCEPTION_OLD_END_OCCURED = "Ending time cannot be changed once session has completed";
	public static final String TOAST_LABEL_INPUT_EXCEPTION_END_BEFORE_START = "Ending time is before starting time";
	
	public static final String TOAST_LABEL_INPUT_EXCEPTION_INVALID_PUBLIC_ID = "Token must contain from 25 to 50 characters";
	public static final String TOAST_LABEL_INPUT_EXCEPTION_INVALID_NAME = "Name must contain from 4 to 50 characters ";
	public static final String TOAST_LABEL_INPUT_EXCEPTION_INVALID_RATE = "Please select a location upload period";
	public static final String TOAST_LABEL_INPUT_EXCEPTION_INVALID_PASSWORD = "Password must contain from 10 to 50 characters";
	
	
	
	
	/**default time & date printing*/
	
	public static final String DEFAULT_VALUE_SESSION_NAME = "...";
	public static final String DEFAULT_VALUE_SESSION_RATE = "...";
	
	public static final String DEFAULT_VALUE_START_TIME = "...";
	public static final String DEFAULT_VALUE_START_DATE = "...";
	public static final String DEFAULT_VALUE_END_TIME = "...";
	public static final String DEFAULT_VALUE_END_DATE = "...";
	
	public static final String TIME_DATE_FORMATTING_STRING = "HH:mm dd/MM/yyyy";
	public static final String DATE_FORMATTING_STRING = "dd/MM/yyyy";
	public static final String TIME_FORMATTING_STRING = "HH:mm";
	public static final String SAMPLING_INFO_PREFIX = "Period : ";
	

	public static final int MIN_DISTANCE_IN_METERS_FOR_SAMPLE_DISPLAY = 30;
	public static final int LIGHT_GREY_COLOR = Color.rgb(237,237,237);
	

}
