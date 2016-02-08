package com.example.sharedtracking.network;

import com.example.sharedtracking.response.ReadingResponse;
import com.example.sharedtracking.session.ISessionReadingListener;
import com.example.sharedtracking.types.Deserializer;

public class ReadingOperationCallback implements INetworkOperationCallback {

	/**Listener to wake up at network operation end*/
	private ISessionReadingListener listener;
	
	public ReadingOperationCallback(ISessionReadingListener listener) {
		this.listener = listener;
	}

	
	@Override
	public void onNetworkActionDone(String result) {
		ReadingResponse response = Deserializer.deserializeSessionReadingResponse(result);
		this.listener.onSessionRead(response);
	}

}
