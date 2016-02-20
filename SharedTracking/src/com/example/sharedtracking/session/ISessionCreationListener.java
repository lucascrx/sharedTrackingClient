package com.example.sharedtracking.session;

import com.example.sharedtracking.response.CreationResponse;

public interface ISessionCreationListener {

	public void onHostedSessionCreated(CreationResponse response);
	

	public void onAlarmUpdateRequired(HostedSession session);
	
	public void onUploadTimeReachedbySession(String sessionPublicID);

	public void setAlarmforNextUpdate(HostedSession session,long sleepDuration);
}
