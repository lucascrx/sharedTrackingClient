package com.example.sharedtracking.inputs;

import java.util.Calendar;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TabHost;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.sharedtracking.IInputListener;
import com.example.sharedtracking.R;

public class TrackingSessionCreationDialog extends DialogFragment {
	
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
       
        //setTitle
        builder.setTitle(R.string.tracking_session_creation_title_dialogue);
         
        // Get the layout inflater
        LayoutInflater inflater = getActivity().getLayoutInflater();
        // Set the View to the Dialog  
        final View view = createDialogContent(inflater);
        builder.setView(view);
        
        //set yes and cancel buttons
        builder.setPositiveButton(R.string.session_creation_validation, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
            	try{
	                //trigger session joining
            		//retrieving public ID
	            	EditText input = (EditText) view.findViewById(R.id.session_joining_publicID_editText);
	            	String typedToken = input.getText().toString();
	            	DialogInputSanitizer.sanitizeInputAsPublicID(typedToken);
	            	//calling callback
	                IInputListener callingActivity = (IInputListener) getActivity();
	                callingActivity.onSessionJoiningReady(typedToken);
            	}catch(DialogInputException e){
            		e.printStackTrace();
            		//printing what was wrong to user
            		Toast.makeText(getActivity(),e.getMessage(), Toast.LENGTH_LONG).show();
            	}
	
            }
        });
        builder.setNegativeButton(R.string.session_creation_cancelation, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
            	//TODO
            }
        });
        
     // Create the Alert Dialog  
        AlertDialog mDialog = builder.create(); 
        // Return the Dialog created  
        return mDialog; 
    }
    
	public View createDialogContent(LayoutInflater inflater){
    	View mView = inflater.inflate(R.layout.tracking_session_creation,null);  
        return mView;
    }

}
