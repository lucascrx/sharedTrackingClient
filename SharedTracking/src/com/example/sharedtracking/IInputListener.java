package com.example.sharedtracking;

import java.sql.Timestamp;

/**interface for callback coming from dialog fragments*/
public interface IInputListener {
	
	public void onImmediateSessionCreationReady(String name,int rate);
	
	public void onPreparedSessionCreationReady(String name, int rate, String publicID, String password,
			String startDate, String endDate);
	
	public void onSessionContributionReady(String publicID, String password);
	
	public void onSessionJoiningReady(String publicID);

	public void onSessionDeletionReady(int index);

}
