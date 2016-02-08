package com.example.sharedtracking.response;

import java.sql.Timestamp;

import com.example.sharedtracking.types.SessionMetaData;

/**Response message for synchronization query*/

public class SynchronizationResponse extends Response{
	
	/**Flag indication if server operation went well or not*/
	private boolean operationStatus;
	/**session meta data for client synchronization*/
	private SessionMetaData metadata;
	
	public SynchronizationResponse(boolean operationStatus, SessionMetaData metadata) {
		this.operationStatus = operationStatus;
		this.metadata = metadata;
	}
	
	public SynchronizationResponse() {
		this.operationStatus = false;
		this.metadata = null;
	}
	
	public boolean getOperationStatus() {
		return operationStatus;
	}
	
	public SessionMetaData getMetadata() {
		return metadata;
	}
	
	
	

}
