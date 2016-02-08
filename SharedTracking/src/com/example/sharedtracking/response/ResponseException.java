package com.example.sharedtracking.response;

/**Exception sent by SessionListener class when processing the response*/
public class ResponseException extends Exception{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public ResponseException(String message){
		super(message);
	}

}
