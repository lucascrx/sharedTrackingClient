package com.example.sharedtracking.session;

import com.example.sharedtracking.response.CreationResponse;

public interface ISessionCreationListener {

	public void onHostedSessionCreated(CreationResponse response);
}
