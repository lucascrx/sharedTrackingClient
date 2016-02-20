package com.example.sharedtracking.background;

import com.example.sharedtracking.constants.Constants;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

/**Session Manager has a broadcast receiver that they configure to 
 * wake them up when upload time is reached*/
public class AlarmReceiver extends BroadcastReceiver{

	/**log tag for debugging*/
	private static String Log_Tag = "Alarm Receiver : ";
	
	@Override
	public void onReceive(Context context, Intent intent) {
		//extract session public ID from intent
		String publicID = intent.getStringExtra(Constants.SESSION_PUBLIC_ID_INTENT_EXTRA_LABEL);
		if(publicID!=null){
			Log.d(Log_Tag,"Alarm intent received by receiver with public ID : "+publicID);
			//inform Manager whose session has reached upload time
			//retrieving MainService from Context
			IBinder binder = peekService(context, new Intent(context, MainService.class));
        	if (binder != null){
        		MainService service = ((MainService.MainBinder) binder).getMainService();
        		service.asynchronousUpload(publicID);
        	}
		}
	}

}
