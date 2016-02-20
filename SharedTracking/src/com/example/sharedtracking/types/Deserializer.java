package com.example.sharedtracking.types;

import com.example.sharedtracking.response.CreationResponse;
import com.example.sharedtracking.response.ReadingResponse;
import com.example.sharedtracking.response.SynchronizationResponse;
import com.example.sharedtracking.response.UpdateResponse;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**Class deserializing JSON object returned from server*/
public class Deserializer {

	/**To be call when session reading response is received*/
	public static ReadingResponse deserializeSessionReadingResponse(String message){
		Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss.SSS z").create();
		ReadingResponse response = null;
		response = gson.fromJson(message, ReadingResponse.class);
		return response;
	}
	
	/**To be call when session creation response is received*/
	public static CreationResponse deserializeSessionCreationResponse(String message){
		Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss.SSS z").create();
		CreationResponse response = null;
		response = gson.fromJson(message, CreationResponse.class);
		return response;
	}

	/**To be call when position update response is received*/
	public static UpdateResponse deserializeUpdateResponse(String message){
		Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss.SSS z").create();
		UpdateResponse response = null;
		response = gson.fromJson(message, UpdateResponse.class);
		return response;
	}
	
	/**To be call when session refresh response is received*/
	public static SynchronizationResponse deserializeSessionRefreshResponse(String message){
		Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss.SSS z").create();
		SynchronizationResponse response = null;
		response = gson.fromJson(message, SynchronizationResponse.class);
		return response;
	}
	
}
