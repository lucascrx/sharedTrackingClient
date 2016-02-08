package com.example.sharedtracking.network;

import java.sql.Timestamp;
import java.util.HashMap;

import android.util.Log;

import com.example.sharedtracking.constants.Constants;

/**Class providing connectivity with the remote Server*/
public class WebClient {
	
	public final static String Log_Tag = "Web Client";
	
	/**Session Creation Immediate Mode*/
	public static void createSession(String name, int rate,CreationOperationCallback callback){
		Log.d(Log_Tag,"preparing request for immediate session creation");
		//creating the POST request to send
		HashMap<String,String> parameters = new HashMap<String,String>();
		parameters.put(Constants.POST_PARAM_LABEL_SESSION_NAME, name);
		parameters.put(Constants.POST_PARAM_LABEL_UPLOAD_RATE,Integer.toString(rate));
		//creating the URL to reach
		String url = Constants.SERVER_ADDRESS+Constants.CREATE_IMMEDIATE_SESSION_PATH;
		//create HTTPSender Object
		HTTPRequestSender sender = new HTTPRequestSender(parameters,url, callback);
		//execute sending
		sender.execute();
	}
	
	/**Session Creation Prepared Mode*/
	public static void createSession(String name,String publicID,String password,Integer rate,String startingTime,
									String endingTime, CreationOperationCallback callback){
		Log.d(Log_Tag,"preparing request for prepared session creation");
		//creating the POST request to send
		HashMap<String,String> parameters = new HashMap<String,String>();
		parameters.put(Constants.POST_PARAM_LABEL_SESSION_NAME, name);
		parameters.put(Constants.POST_PARAM_LABEL_PUBLIC_ID, publicID);
		parameters.put(Constants.POST_PARAM_LABEL_PASSWORD, password);
		parameters.put(Constants.POST_PARAM_LABEL_STARTING_TIME, startingTime);
		if(endingTime!=null){
			parameters.put(Constants.POST_PARAM_LABEL_ENDING_TIME, endingTime);
		}
		parameters.put(Constants.POST_PARAM_LABEL_UPLOAD_RATE,Integer.toString(rate));
		//creating the URL to reach
		String url = Constants.SERVER_ADDRESS+Constants.CREATE_PREPARED_SESSION_PATH;
		//create HTTPSender Object
		HTTPRequestSender sender = new HTTPRequestSender(parameters,url, callback);
		//execute sending
		sender.execute();
	}
	
	/**Session Position Update*/
	public static void uploadPosition(String privateID, double latitude, double longitude,String deviceName, String deviceID, PositionUpdateOperationCallback callback){
		Log.d(Log_Tag,"preparing request for position upload");
		//creating the POST request to send
		HashMap<String,String> parameters = new HashMap<String,String>();
		parameters.put(Constants.POST_PARAM_LABEL_PRIVATE_ID, privateID);
		parameters.put(Constants.POST_PARAM_LABEL_LATITUDE, Double.toString(latitude));
		parameters.put(Constants.POST_PARAM_LABEL_LONGITUDE, Double.toString(longitude));
		parameters.put(Constants.POST_PARAM_LABEL_DEVICE_NAME, deviceName);
		parameters.put(Constants.POST_PARAM_LABEL_DEVICE_ID, deviceID);
		//creating the URL to reach
		String url = Constants.SERVER_ADDRESS+Constants.UPDATE_POSITION_SESSION_PATH;
		//create HTTPSender Object
		HTTPRequestSender sender = new HTTPRequestSender(parameters,url, callback);
		//execute sending
		sender.execute();
	}

	/**Session Name Update*/
	public static void updateName(String privateID, String newName, NameUpdateOperationCallback callback){
		Log.d(Log_Tag,"preparing request for name update");
		//creating the POST request to send
		HashMap<String,String> parameters = new HashMap<String,String>();
		parameters.put(Constants.POST_PARAM_LABEL_PRIVATE_ID, privateID);
		parameters.put(Constants.POST_PARAM_LABEL_SESSION_NAME, newName);
		//creating the URL to reach
		String url = Constants.SERVER_ADDRESS+Constants.UPDATE_SESSION_NAME_PATH;
		//create HTTPSender Object
		HTTPRequestSender sender = new HTTPRequestSender(parameters,url, callback);
		//execute sending
		sender.execute();
	}
	
	/**Session Upload Rate Update*/
	public static void updateRate(String privateID, int newRate, RateUpdateOperationCallback callback){
		Log.d(Log_Tag,"preparing request for rate update");
		//creating the POST request to send
		HashMap<String,String> parameters = new HashMap<String,String>();
		parameters.put(Constants.POST_PARAM_LABEL_PRIVATE_ID, privateID);
		parameters.put(Constants.POST_PARAM_LABEL_UPLOAD_RATE, Integer.toString(newRate));
		//creating the URL to reach
		String url = Constants.SERVER_ADDRESS+Constants.UPDATE_UPLOAD_RATE_SESSION_PATH;
		//create HTTPSender Object
		HTTPRequestSender sender = new HTTPRequestSender(parameters,url, callback);
		//execute sending
		sender.execute();
	}
	
	/**Session Starting Time Update*/
	public static void updateStartingTime(String privateID, Timestamp start, StartingTimeUpdateOperationCallback callback){
		Log.d(Log_Tag,"preparing request for starting time update");
		//creating the POST request to send
		HashMap<String,String> parameters = new HashMap<String,String>();
		parameters.put(Constants.POST_PARAM_LABEL_PRIVATE_ID, privateID);
		parameters.put(Constants.POST_PARAM_LABEL_STARTING_TIME, start.toString());
		//creating the URL to reach
		String url = Constants.SERVER_ADDRESS+Constants.UPDATE_SESSION_STARTING_TIME_PATH;
		//create HTTPSender Object
		HTTPRequestSender sender = new HTTPRequestSender(parameters,url, callback);
		//execute sending
		sender.execute();
	}
	
	/**Session ending Time Update*/
	public static void updateEndingTime(String privateID, Timestamp end, EndingTimeUpdateOperationCallback callback){
		Log.d(Log_Tag,"preparing request for ending time update");
		//creating the POST request to send
		HashMap<String,String> parameters = new HashMap<String,String>();
		parameters.put(Constants.POST_PARAM_LABEL_PRIVATE_ID, privateID);
		parameters.put(Constants.POST_PARAM_LABEL_ENDING_TIME, end.toString());
		//creating the URL to reach
		String url = Constants.SERVER_ADDRESS+Constants.UPDATE_SESSION_ENDING_TIME_PATH;
		//create HTTPSender Object
		HTTPRequestSender sender = new HTTPRequestSender(parameters,url, callback);
		//execute sending
		sender.execute();
	}
	
	/**Session Reading*/
	public static void readPosition(String publicID, int sampleIndex, ReadingOperationCallback callback){
		Log.d(Log_Tag,"preparing request for position reading, index : "+sampleIndex);
		//creating the POST request to send
		HashMap<String,String> parameters = new HashMap<String,String>();
		parameters.put(Constants.POST_PARAM_LABEL_PUBLIC_ID, publicID);
		parameters.put(Constants.POST_PARAM_LABEL_LOOKUP_INDEX, Integer.toString(sampleIndex));
		//creating the URL to reach
		String url = Constants.SERVER_ADDRESS+Constants.READ_SESSION_PATH;
		//create HTTPSender Object
		HTTPRequestSender sender = new HTTPRequestSender(parameters,url, callback);
		//execute sending
		sender.execute();
	}
	
	/**Session Contribution*/
	public static void contributeToSession(String publicID, String password,CreationOperationCallback callback){
		//creating the POST request to send
		HashMap<String,String> parameters = new HashMap<String,String>();
		parameters.put(Constants.POST_PARAM_LABEL_PUBLIC_ID, publicID);
		parameters.put(Constants.POST_PARAM_LABEL_PASSWORD, password);
		//creating the URL to reach
		String url = Constants.SERVER_ADDRESS+Constants.CONTRIBUTE_SESSION_PATH;
		//create HTTPSender Object
		HTTPRequestSender sender = new HTTPRequestSender(parameters,url, callback);
		//execute sending
		sender.execute();
	}
	
	/**Session Synchronization*/
	public static void refreshSession(String publicID, SessionRefreshCallback callback){
		Log.d(Log_Tag,"preparing request for session refresh");
		//creating the POST request to send
		HashMap<String,String> parameters = new HashMap<String,String>();
		parameters.put(Constants.POST_PARAM_LABEL_PUBLIC_ID, publicID);
		//creating the URL to reach
		String url = Constants.SERVER_ADDRESS+Constants.SYNCHRONIZATION_SESSION_PATH;
		//create HTTPSender Object
		HTTPRequestSender sender = new HTTPRequestSender(parameters,url, callback);
		//execute sending
		sender.execute();
	}	

}
