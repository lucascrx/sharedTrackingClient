package com.example.sharedtracking.types;

import java.sql.Timestamp;
import java.util.ArrayList;


/**Define the object returned when sample are read from the db*/

public class SampleList {
	
	/**The list of the requested samples*/
	private ArrayList<Sample> samples;
	/**The current update rate*/
	private Timestamp lastModificationTime;
	
	public SampleList(ArrayList<Sample> samples, Timestamp modifTime) {
		this.samples = samples;
		this.lastModificationTime = modifTime;
	}
	
	public SampleList() {
		this.samples = new ArrayList<Sample>();
		this.lastModificationTime = null;
	}
	
	public ArrayList<Sample> getSamples() {
		return samples;
	}
	
	public Timestamp getLastModificationTime() {
		return this.lastModificationTime;
	}

	public void setSamples(ArrayList<Sample> samples) {
		this.samples = samples;
	}

	public void setLastModificationTime(Timestamp modifTime) {
		this.lastModificationTime = modifTime;
	}
	
	public void addSample(Sample s){
		this.samples.add(s);
	}
}
