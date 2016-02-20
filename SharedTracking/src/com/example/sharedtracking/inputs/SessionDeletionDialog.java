package com.example.sharedtracking.inputs;

import com.example.sharedtracking.IInputListener;
import com.st.sharedtracking.R;
import com.example.sharedtracking.views.ConstantGUI;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

public class SessionDeletionDialog extends DialogFragment {
	
	 @Override
	 public Dialog onCreateDialog(Bundle savedInstanceState) {
		 AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		 //retrieve session index
		 Bundle passedArgs = getArguments();
		 //sessionIndex is always higher than 0 : incremented by the caller
		 int incrIndex = passedArgs.getInt("incrementedIndex");
		 String sessionName = passedArgs.getString("name");
		 if(incrIndex!=0){
			 final int realIndex = incrIndex-1;
			 //setTitle
			 builder.setTitle(R.string.session_deletion_title_dialogue);
			 // Get the layout inflater
		     LayoutInflater inflater = getActivity().getLayoutInflater();
		     // Set the View to the Dialog  
		     final View view = createDialogContent(inflater,sessionName);
		     builder.setView(view);
		     //set yes and cancel buttons
		     builder.setPositiveButton(R.string.session_creation_validation, new DialogInterface.OnClickListener() {
		     @Override
		     public void onClick(DialogInterface dialog, int id) {
		    	 //trigger session deletion
		    	 //calling callback
		    	 IInputListener callingActivity = (IInputListener) getActivity();
		    	 callingActivity.onSessionDeletionReady(realIndex);
			}});
		     
		    builder.setNegativeButton(R.string.session_creation_cancelation, new DialogInterface.OnClickListener() {
		    	public void onClick(DialogInterface dialog, int id) {
		            }
		        });	        
		 }
	     // Create the Alert Dialog  
		 AlertDialog mDialog = builder.create(); 
		 // Return the Dialog created  
		 return mDialog; 
	    }
	    
		public View createDialogContent(LayoutInflater inflater,String name){
	    	View mView = inflater.inflate(R.layout.delete_session,null);  
	    	TextView descriptionTV = (TextView) mView.findViewById(R.id.session_deletion_view);
	    	String toAppend;
	    	if(name!=null){
	    		toAppend=name;
	    	}else{
	    		toAppend=ConstantGUI.DEFAULT_VALUE_SESSION_NAME;
	    	}
	    	descriptionTV.append(toAppend);
	        return mView;
	    }

}
