package com.example.sharedtracking.views;

import com.google.android.gms.maps.model.Marker;

public class PositionedMarker {

	/**Marker Object printed on the map*/
	private Marker marker;
	/**position of the sample represented by the marker*/
	private int position;
	
	
	
	public PositionedMarker(Marker marker, int position) {
		super();
		this.marker = marker;
		this.position = position;
	}
	
	public Marker getMarker() {
		return marker;
	}
	public int getPosition() {
		return position;
	}
	
	
	
	

}
