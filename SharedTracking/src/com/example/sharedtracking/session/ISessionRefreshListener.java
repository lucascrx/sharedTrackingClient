package com.example.sharedtracking.session;

import com.example.sharedtracking.response.SynchronizationResponse;


public interface ISessionRefreshListener {
	public void onSessionRefreshed(SynchronizationResponse response);
}
