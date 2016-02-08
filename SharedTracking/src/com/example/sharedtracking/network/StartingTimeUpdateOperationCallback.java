package com.example.sharedtracking.network;

import com.example.sharedtracking.response.UpdateResponse;
import com.example.sharedtracking.session.ISessionUpdateListener;
import com.example.sharedtracking.types.Deserializer;

public class StartingTimeUpdateOperationCallback implements INetworkOperationCallback{
	
	/**Listener to wake up at network operation end*/
	private ISessionUpdateListener listener;
	
	public StartingTimeUpdateOperationCallback(ISessionUpdateListener listener) {
		this.listener = listener;
	}

	@Override
	public void onNetworkActionDone(String result) {
		UpdateResponse response = Deserializer.deserializeUpdateResponse(result);
		this.listener.onStartingTimeUpdated(response);
	}

}
