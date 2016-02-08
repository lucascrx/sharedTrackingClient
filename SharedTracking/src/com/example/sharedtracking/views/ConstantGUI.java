package com.example.sharedtracking.views;

import java.text.SimpleDateFormat;

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
	public static final String TOAST_LABEL_FOR_NAME_UPDATE = "Update of Session Name";
	public static final String TOAST_LABEL_FOR_RATE_UPDATE = "Update of Session Upload Period";
	public static final String TOAST_LABEL_FOR_STARTING_TIME_UPDATE = "Update of Session Starting Time";
	public static final String TOAST_LABEL_FOR_ENDING_TIME_UPDATE = "Update of Session Ending Time";
	
	public static final String TOAST_LABEL_FOR_LOCATION_RESOLUTION_FAILURE = "Location can't be resolved";
	
	
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
	

	public static final int MIN_DISTANCE_IN_METERS_FOR_SAMPLE_DISPLAY = 10;
	public static final int LIGHT_GREY_COLOR = Color.rgb(237,237,237);
}
