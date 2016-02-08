package com.example.sharedtracking.network;

import com.example.sharedtracking.response.CreationResponse;
import com.example.sharedtracking.response.SynchronizationResponse;
import com.example.sharedtracking.session.ISessionCreationListener;
import com.example.sharedtracking.session.ISessionRefreshListener;
import com.example.sharedtracking.types.Deserializer;

public class SessionRefreshCallback implements INetworkOperationCallback {
	
	/**Listener to wake up at network operation end*/
	private ISessionRefreshListener listener;
	
	public SessionRefreshCallback(ISessionRefreshListener listener) {
		this.listener = listener;
	}

	
	@Override
	public void onNetworkActionDone(String result) {
		//Parsing the received string in the creation response object
		SynchronizationResponse response = Deserializer.deserializeSessionRefreshResponse(result);
		//warning the listener
		this.listener.onSessionRefreshed(response);
	}

}
