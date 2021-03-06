package com.example.sharedtracking;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;
import java.util.TimeZone;
import com.example.sharedtracking.constants.Constants;
import com.example.sharedtracking.session.HostedSession;
import com.example.sharedtracking.session.JoinedSession;
import com.example.sharedtracking.session.Session;
import com.example.sharedtracking.views.ConstantGUI;
import com.st.sharedtracking.R;
import android.annotation.SuppressLint;
import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class SessionListAdapter extends ArrayAdapter<Session>{
	
	
    public SessionListAdapter(Context context, ArrayList<Session> list) {
        super(context, 0, list);
     }

     @SuppressLint("NewApi")
	@Override
     public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        Session session = getItem(position);    
        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
           convertView = LayoutInflater.from(getContext()).inflate(R.layout.session_item, parent, false);
        }
        // Lookup view for data population
        TextView tvName = (TextView) convertView.findViewById(R.id.session_name);
        TextView tvStartingTime = (TextView) convertView.findViewById(R.id.starting_time);
        TextView tvEndingTime = (TextView) convertView.findViewById(R.id.ending_time);
        TextView tvPublicToken = (TextView) convertView.findViewById(R.id.public_token);        
        ImageView ivSessionType = (ImageView)convertView.findViewById(R.id.session_type);
        ImageView ivSessionStatus = (ImageView)convertView.findViewById(R.id.session_status);

        //constructing new view
        //setting Name
		String name = session.getName();
		if(name==null){
			//name is null when session is not configured
			name=ConstantGUI.DEFAULT_VALUE_SESSION_NAME;
		}
		tvName.setText(name);

		Locale current = getContext().getResources().getConfiguration().locale;
		SimpleDateFormat sdf = new SimpleDateFormat(ConstantGUI.TIME_DATE_FORMATTING_STRING,current);
		sdf.setTimeZone(TimeZone.getDefault());
		
		//Setting Starting time
		Timestamp timestampStart = session.getStartingTime();
		String start;
		if(timestampStart==null){
			//starting time is null when session is not configured
			start = ConstantGUI.DEFAULT_VALUE_START_DATE;
		}else{
			start = sdf.format(timestampStart);
		}
		tvStartingTime.setText(start);
		//Setting Ending time
		Timestamp timestampEnd = session.getEndingTime();
		String end;
		if(timestampEnd==null){
			end = ConstantGUI.DEFAULT_VALUE_END_DATE;
		}else{
			end = sdf.format(timestampEnd);
		}
		tvEndingTime.setText(end);
        //Setting public ID
		String publicID = session.getPublicID();
		tvPublicToken.setText(publicID);
		//setting Type picture
		int type = 0;
		if(session instanceof HostedSession){
			type = R.drawable.ic_followed;
		}else if(session instanceof JoinedSession){
			type = R.drawable.ic_following;
		}
		ivSessionType.setImageResource(type);
		
		//Setting status
		int status = session.getStatus();
		if (status==Constants.SESSION_STATUS_DONE){
			ivSessionStatus.setBackground(ContextCompat.getDrawable(getContext(),R.drawable.session_item_status_background_done));
		}else if(status==Constants.SESSION_STATUS_RUNNING){
			ivSessionStatus.setBackground(ContextCompat.getDrawable(getContext(),R.drawable.session_item_status_background_active));
		}else if(status==Constants.SESSION_STATUS_PENDING){
			ivSessionStatus.setBackground(ContextCompat.getDrawable(getContext(),R.drawable.session_item_status_background_pending));
		}else if(status==Constants.SESSION_STATUS_UNKNOWN){
			ivSessionStatus.setBackground(ContextCompat.getDrawable(getContext(),R.drawable.session_item_status_background_unknown));
		}else if(status==Constants.SESSION_STATUS_NOT_CONNECTED){
			ivSessionStatus.setBackground(ContextCompat.getDrawable(getContext(),R.drawable.session_item_status_background_not_connected));
		}else if(status==Constants.SESSION_STATUS_NOT_LOCALIZED){
			ivSessionStatus.setBackground(ContextCompat.getDrawable(getContext(),R.drawable.session_item_status_background_not_localized));
		}
		
		// Return the completed view to render on screen
        return convertView;
    }
     
     

}
