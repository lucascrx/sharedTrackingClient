package com.example.sharedtracking;

/**interface implemented by activities hosting a dynamic object so that they can be
 * dynamically updated*/
public interface IGraphicalListener {
	
	/**function called by an object (session or manager) after having been modified.
	 * It can also be called by the listener itself at creation*/
	public void updateGUI();
	
	/**function called by an object when it encounter network issue while updating*/
	public void notifyNetworkIssue();
	
	/**function called by an object when it has updated successfully one of its parameters*/
	public void notifySuccessfulUpdateOperation(String ParameterName);
	
	/**function called by an object when it failed to update one of its parameters*/
	public void notifyFailedUpdateOperation(String ParameterName);
}
