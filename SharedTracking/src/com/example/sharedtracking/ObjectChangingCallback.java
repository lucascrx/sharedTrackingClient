package com.example.sharedtracking;

public class ObjectChangingCallback {
	
	private IGraphicalListener listener;
	
	public ObjectChangingCallback(IGraphicalListener list){
		this.listener = list;
	}
	
	/**function called by object (session or manager) when object changed after a network operation*/
	public void onObjectChanged(){
		//triggers a GUI update, object originated
		this.listener.updateGUI();
	}
	
	/**function called by object (session or manager) when server is not reachable*/
	public void onNetworkIssueEncountered(){
		this.listener.notifyNetworkIssue();
	}
	
	/**function called by an object (session or manager) when object update request has failed*/
	public void onFailStatusReturnedOperation(String updatedParamName){
		this.listener.notifyFailedUpdateOperation(updatedParamName);
	}
	
	/**function called by an object (session or manager) when object update request has been successful*/
	public void onSuccessStatusReturnedOperation(String updatedParamName){
		this.listener.notifySuccessfulUpdateOperation(updatedParamName);
	}
	
	/**function called when an hosted session has been created on server**/
	public void onHostedSessionCreated(){
		this.listener.notifyHostedSessionCreation();
	}
}
