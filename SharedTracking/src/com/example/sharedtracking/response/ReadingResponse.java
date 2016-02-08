package com.example.sharedtracking.response;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.ArrayList;

import com.example.sharedtracking.types.SampleList;


public class ReadingResponse extends Response implements Serializable{
	
	
	/**Flag indication if server operation went well or not*/
	private boolean operationStatus;
	/**Sample List retrieved from the database*/
	private SampleList samples;

	public ReadingResponse(boolean flag, SampleList sampleList) {
		super();
		this.operationStatus = flag;
		this.samples = sampleList;
	}
	
	public ReadingResponse() {
		super();
		this.operationStatus = false;
		this.samples = null;
	}
	
	public boolean getOperationStatus() {
		return operationStatus;
	}

	public SampleList getSamples() {
		return samples;
	}
	
}