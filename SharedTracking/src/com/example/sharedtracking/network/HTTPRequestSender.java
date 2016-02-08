package com.example.sharedtracking.network;


import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

import android.os.AsyncTask;
import android.util.Log;

/**Send an HTTP POST request with the provided post parameters*/
public class HTTPRequestSender extends AsyncTask<Void, String, String>{

	/**Log Tag for debugging purpose*/
	private String LOG_TAG = "HTTPRequestServer";
	
	/**parameters to include in the POST request*/
	private HashMap<String,String> postParams;
	/**URL to reach**/
	private String targetURL;
	/**Callback object to call on HTTP exchange completion*/
	private INetworkOperationCallback callbackObject;
	
    public HTTPRequestSender(HashMap<String,String> params,String url,INetworkOperationCallback callback) {
        super();
        this.postParams=params;
        this.targetURL=url;
        this.callbackObject = callback;
    }
	
    @Override
    protected String doInBackground(Void... params) {
		URL url;
		HttpURLConnection urlConnection = null;
		String response = null;
		
		try {
			Log.v(LOG_TAG,"Setting up input parameters");
			url = new URL(this.targetURL);
			//encoding parameters
			String param = getPostDataString(this.postParams);
			
			//opening connection
			Log.v(LOG_TAG,"Setting up connection parameters");
			urlConnection = (HttpURLConnection) url.openConnection();
			urlConnection.setConnectTimeout(10000);
			urlConnection.setDoOutput(true);
			urlConnection.setRequestMethod("POST");
			
			urlConnection.setFixedLengthStreamingMode(param.getBytes().length);
			urlConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
			
			PrintWriter out = new PrintWriter(urlConnection.getOutputStream());
			out.print(param);
			out.close();
			Log.v(LOG_TAG,"waiting for server response");
			int responseCode = urlConnection.getResponseCode();
			if(responseCode == 200){
				StringBuffer sb = new StringBuffer();
				InputStream is;
				is = new BufferedInputStream(urlConnection.getInputStream());
		        BufferedReader br = new BufferedReader(new InputStreamReader(is));
		        String inputLine = "";
		        while ((inputLine = br.readLine()) != null) {
		            sb.append(inputLine);
		        }
		        response = sb.toString();
				Log.v(LOG_TAG,"HTTP 200, Server Response : "+ response);
			}else{
				Log.v(LOG_TAG, "HTTP error : "+ responseCode);
			}
			
		} catch (MalformedURLException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (UnsupportedEncodingException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}finally {
			if(urlConnection != null)
				urlConnection.disconnect();
		}
		return response;
	}
    
    @Override
    protected void onPostExecute(String result) {
    	Log.v(LOG_TAG,"Async task ending");
        super.onPostExecute(result);
        this.callbackObject.onNetworkActionDone(result);
    }
    
    /**HashMap Parameters encoding into HTTP POST parameters*/
    private String getPostDataString(HashMap<String, String> params) throws UnsupportedEncodingException{
        StringBuilder result = new StringBuilder();
        boolean first = true;
        for(Map.Entry<String, String> entry : params.entrySet()){
            if (first)
                first = false;
            else
                result.append("&");

            result.append(URLEncoder.encode(entry.getKey(), "UTF-8"));
            result.append("=");
            result.append(URLEncoder.encode(entry.getValue(), "UTF-8"));
        }

        return result.toString();
    }
    
	
}

