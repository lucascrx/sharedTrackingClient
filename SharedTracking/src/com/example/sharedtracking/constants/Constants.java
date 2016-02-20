package com.example.sharedtracking.constants;

public class Constants {

	
	/**Network Constant*/
	
	
	//public final static String SERVER_ADDRESS = "http://192.168.1.17:8080/findMeServer/jaxrs";
	public final static String SERVER_ADDRESS = "https://sharedtracking-backendserver01.rhcloud.com:8443/findMeServer/jaxrs";
	
	public final static String CREATE_IMMEDIATE_SESSION_PATH = "/immediate_creation";
	public final static String CREATE_PREPARED_SESSION_PATH="/prepared_creation";
	public final static String READ_SESSION_PATH="/reading";
	public final static String UPDATE_SESSION_STARTING_TIME_PATH = "/update_starting_time";
	public final static String UPDATE_SESSION_ENDING_TIME_PATH = "/update_ending_time";
	public final static String UPDATE_SESSION_NAME_PATH = "/update_name";
	public final static String UPDATE_UPLOAD_RATE_SESSION_PATH = "/update_upload_rate";
	public final static String UPDATE_POSITION_SESSION_PATH = "/upload_position";
	public final static String CONTRIBUTE_SESSION_PATH = "/contribution";
	public final static String SYNCHRONIZATION_SESSION_PATH = "/synchronization";
	
	
	/**label for posted parameters*/
	public final static String POST_PARAM_LABEL_SESSION_NAME = "session_name";
	public final static String POST_PARAM_LABEL_UPLOAD_RATE = "rate";
	public final static String POST_PARAM_LABEL_PUBLIC_ID = "public_token";
	public final static String POST_PARAM_LABEL_PASSWORD = "password";
	public final static String POST_PARAM_LABEL_PRIVATE_ID = "private_token";
	public final static String POST_PARAM_LABEL_LATITUDE = "latitude";
	public final static String POST_PARAM_LABEL_LONGITUDE = "longitude";
	public final static String POST_PARAM_LABEL_DEVICE_NAME = "device_name";
	public final static String POST_PARAM_LABEL_DEVICE_ID = "device_token";
	public final static String POST_PARAM_LABEL_LOOKUP_INDEX = "expected_index";
	public final static String POST_PARAM_LABEL_STARTING_TIME = "starting_time";
	public final static String POST_PARAM_LABEL_ENDING_TIME = "ending_time";
	
	/**timestamp formatting for client/server exchanges*/
	public final static String TIMESTAMP_STRING_FORMAT = "yyyy-MM-dd HH:mm:ss.SSS z";
	
	/**Constant for session status*/
	
	/**When network is not reachable*/
	public final static int SESSION_STATUS_NOT_CONNECTED = 1;
	/**When no hosted session on the server corresponds to the local joined session*/
	public final static int SESSION_STATUS_UNKNOWN = 2;
	/**When session starting date has not occurred, no sample is submitted/fetched*/
	public final static int SESSION_STATUS_PENDING = 3;
	/**When session is submitting/fetching samples*/
	public final static int SESSION_STATUS_RUNNING = 4;
	/**When session has completed, ending date has occurred*/
	public final static int SESSION_STATUS_DONE = 5;
	/**When session can't resolve device location*/
	public final static int SESSION_STATUS_NOT_LOCALIZED = 6;
	
	/**Number of empty fetching before standing that session is unknown*/
	public final static int MAX_EMPTY_FETCHING_NUMBER_BEFORE_UNKNOWN = 3;
	/**Number of desynchronization message before standing that session is unknown,
	 * set to 2 since receiving one can be due to network latency */
	public final static int MAX_DESYNCHRONIZATION_NUMBER_BEFORE_UNKNOWN = 2;
	
	/**Constant for permission request*/
	
	/**User location*/
	public final static int PERMISSION_REQUEST_USER_LOCATION = 1;
	
	/**Constant for Location Setting dialog display*/
	public final static int REQUEST_CHECK_LOCATION_SETTINGS = 1001;
	
	/**Notification ID for retrieving*/
	public final static int NOTIFICATION_ID = 100;
	
	/**Extra field name for session public ID for AlarmReceiver destined Intent*/
	public final static String SESSION_PUBLIC_ID_INTENT_EXTRA_LABEL = "session_public_id";
	
	/**Shared Preferences Constants*/
	
	/**Shared preference name*/
	public final static String SHARED_PREFERENCE_NAME = "sharedTrackingPrefs";
	/**Device ID field label*/
	public final static String DEVICE_ID_STORING_LABEL = "device_id";
	/**Random Space for Device ID generation*/
	public final static String DEVICE_ID_ALPHABET = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
	/**Device ID length*/
	public final static int DEVICE_ID_LENGTH = 5;
	
	
	
	
}
