package com.example.sharedtracking.types;

import java.io.Serializable;
import java.sql.Timestamp;

import android.os.Parcel;
import android.os.Parcelable;


public class Sample implements Serializable, Parcelable{	

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	
	private Timestamp time;
	private double latitude;
	private double longitude;
	private String deviceName;
	private String deviceID;

	public Sample(Timestamp time, double latitude,double longitude,String devName, String devID) {
		this.time = time;
		this.latitude = latitude;
		this.longitude = longitude;
		this.deviceName = devName;
		this.deviceID = devID;
	}
	
	
	public Timestamp getTime() {
		return time;
	}


	public double getLongitude() {
		return longitude;
	}
	
	
	public double getLatitude() {
		return latitude;
	}


	public String getDeviceName() {
		return deviceName;
	}


	public String getDeviceID() {
		return deviceID;
	}

	
	//Parcelable part

	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}


	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeValue(time);
		dest.writeDouble(latitude);
		dest.writeDouble(longitude);
		dest.writeString(deviceName);
		dest.writeString(deviceID);
	}
	
    public static final Parcelable.Creator<Sample> CREATOR  = new Parcelable.Creator<Sample>() {
		@Override
		public Sample createFromParcel(Parcel in) {
		    return new Sample(in);
		}
		
		// We just need to copy this and change the type to match our class.
		@Override
		public Sample[] newArray(int size) {
		    return new Sample[size];
		}
	};
	
	private Sample(Parcel in){
		time = (Timestamp) in.readValue(Timestamp.class.getClassLoader());
		latitude = in.readDouble();
		longitude = in.readDouble();
		deviceName = in.readString();
		deviceID = in.readString();
	}
	

}
