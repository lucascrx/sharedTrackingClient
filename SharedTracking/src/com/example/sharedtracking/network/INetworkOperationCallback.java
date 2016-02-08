package com.example.sharedtracking.network;

public interface INetworkOperationCallback {

	/**Method to implement for listening the response from the server
	 * @param result : string returned by the server*/
		public void onNetworkActionDone(String result);
}
