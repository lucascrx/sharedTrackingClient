package com.example.sharedtracking.network;

import com.example.sharedtracking.response.CreationResponse;
import com.example.sharedtracking.session.ISessionCreationListener;
import com.example.sharedtracking.types.Deserializer;

public class CreationOperationCallback implements INetworkOperationCallback{


	/**Listener to wake up at network operation end*/
	private ISessionCreationListener listener;
	
	public CreationOperationCallback(ISessionCreationListener listener) {
		this.listener = listener;
	}

	
	@Override
	public void onNetworkActionDone(String result) {
		//Parsing the received string in the creation response object
		CreationResponse response = Deserializer.deserializeSessionCreationResponse(result);
		//warning the listener
		this.listener.onHostedSessionCreated(response);
	}

}
