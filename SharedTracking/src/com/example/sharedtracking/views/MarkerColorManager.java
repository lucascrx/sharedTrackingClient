package com.example.sharedtracking.views;

import java.util.ArrayList;
import java.util.HashMap;

import android.R.color;
import android.graphics.Color;
import android.util.Log;

/**Set Colors to map marker according to deviceID*/
public class MarkerColorManager {
	
	/**Log Tag For debugging purposes*/
	private final static String LogTag = "Marker Color Manager";
	
	/**Color List*/
	private final static int[] colors = {Color.RED,Color.BLUE,Color.GREEN,Color.MAGENTA,Color.YELLOW,Color.CYAN};

	
	/**mapping Color<-->ListOfDeviceID*/
	private HashMap<Integer,ArrayList<String>> markerColorMapping;
	/**pointer for uniform color repartition*/
	private int index;
	
	public MarkerColorManager(){
		this.markerColorMapping = new HashMap<Integer,ArrayList<String>>();
		for(int i=0;i<colors.length;i++){
			this.markerColorMapping.put(colors[i], new ArrayList<String>());
		}
		this.index=0;
	}
	
	public int getColorForDevice(String deviceID){
		Log.d(LogTag, "Resoloving color for device : "+deviceID);
		int color=0;
		ArrayList<String> currentList;
		boolean colorFound=false;
		for(int currentColor : this.markerColorMapping.keySet()){
			currentList = this.markerColorMapping.get(currentColor);
			if(currentList.contains(deviceID)){
				//color already set for this device
				colorFound=true;
				color = currentColor;
				Log.d(LogTag, "Device : "+deviceID+" is already associated to the color : "+color);
				break;
			}
		}
		if(!colorFound){
			//color not found for device name : registering new device name
			color = colors[this.index];
			ArrayList<String> hostingList = this.markerColorMapping.get(color);
			hostingList.add(deviceID);
			Log.d(LogTag, "Device : "+deviceID+" is not associated yet to a color, assigning it color : "+color);
			//incrementing counter, wrapping at the end of colors array
			if(this.index==colors.length-1){
				this.index=0;
			}else{
				this.index++;
			}
		}
		//return transparent color
		int newColor = color & 16777215;
		return newColor;
		
	}
	
	
	

}
