package com.example.sharedtracking.inputs;

import java.util.ArrayList;
import java.util.Arrays;

public class DialogInputConverter {
	//TODO better to construct array from resources
	public static String[] rateStrings = {"10 sec.","30 sec.","1 min.","2 min.","5 min.","15 min.","30 min."};
	public static Integer[] rateIntegers = {10000,30000,60000,120000,300000,900000,1800000};
	
	public static ArrayList<String> stringValues = new ArrayList<String>(Arrays.asList(rateStrings));
	public static ArrayList<Integer> intValues = new ArrayList<Integer>(Arrays.asList(rateIntegers));
	
	public static int convertRateToInt(String input){
		int result = 0;
		int index = stringValues.indexOf(input);
		if(index!=-1){
			result = intValues.get(index);
		}
		return result;
	}
	
	public static String convertRateToString(Integer input){
		String result = "";
		int index = intValues.indexOf(input);
		if(index!=-1){
			result = stringValues.get(index);
		}
		return result;
	}
	
}
