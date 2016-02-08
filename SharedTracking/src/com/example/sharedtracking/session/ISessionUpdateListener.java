package com.example.sharedtracking.session;

import com.example.sharedtracking.response.UpdateResponse;


public interface ISessionUpdateListener {

	public void onPositionUpdated(UpdateResponse response);
	
	public void onNameUpdated(UpdateResponse response);
	
	public void onRateUpdated(UpdateResponse response);
	
	public void onStartingTimeUpdated(UpdateResponse response);
	
	public void onEndingTimeUpdated(UpdateResponse response);
	
}
