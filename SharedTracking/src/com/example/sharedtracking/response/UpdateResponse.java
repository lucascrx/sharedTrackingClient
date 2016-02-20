package com.example.sharedtracking.response;

import java.io.Serializable;
import java.sql.Timestamp;


public class UpdateResponse extends Response implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	/**Flag indication if server operation went well or not*/
	private boolean operationStatus;
	/**Last modification time of session meta data, in case of parameter update response,
	 * this correspond to the time the triggered update occurred (no synchronization needed in this precise case)*/
	private Timestamp lastModificationTime;

	public UpdateResponse(boolean flag, Timestamp lastModifTime) {
		super();
		this.operationStatus=flag;
		this.lastModificationTime=lastModifTime;
	}
	
	public UpdateResponse() {
		super();
		this.operationStatus=false;
		this.lastModificationTime=null;
	}
	
	public boolean getOperationStatus() {
		return operationStatus;
	}

	public Timestamp getLastModificationTime() {
		return lastModificationTime;
	}

}
