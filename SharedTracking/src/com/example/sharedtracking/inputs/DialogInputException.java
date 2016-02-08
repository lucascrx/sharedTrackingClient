package com.example.sharedtracking.inputs;

/**Exception thrown when user configure session with invalid input*/
public class DialogInputException extends Exception{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public DialogInputException(String message){
		super(message);
	}

}
