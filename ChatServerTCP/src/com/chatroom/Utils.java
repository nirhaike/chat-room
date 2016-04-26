package com.chatroom;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;


public class Utils {

	/**
	 * @param args
	 * @pre: string off date: yyyy/mm/dd/hh
	 * h = hour
	 * @return the handShake
	 */
	public static String ChangeDateHandShake(String d){
		
		String[] dateArr = d.split("/");
		int arr[] = new int[4];
		for (int i = 0; i < dateArr.length; i++) {
			arr[i] = Integer.parseInt(dateArr[i]);
		}
		
		// change hour
		if (arr[3] == 0){
			if (arr[2] == 1){
				if (arr[1] == 1){
					arr[1] = 12;
				}
				else{
					arr[1]--;
				}
				arr[2] = 30;
			}
			else{
				arr[2]--;
			}
			arr[3] = 23;
		}
		else{
			arr[3] --;
		}
		
		// change day
		if (arr[2] == 1){
			if (arr[1] == 1){
				arr[1] = 12;
			}
			else{
				arr[1]--;
			}
			arr[2] = 30;
		}
		else{
			arr[2]--;
		}
	String resultS = "";
	for (int i = 0; i < arr.length; i++) {
		resultS += Integer.toString(arr[i]);
		if (i != arr.length -1){
			resultS += "/";
		}
	}

		return resultS;
		
	}

	/**
	 * @return the current date with the format "yyyy/mm/dd/hh"
	 */
	public static String getDate() {
		Calendar cal = Calendar.getInstance();
		DateFormat df = new SimpleDateFormat("yyyy/MM/dd/HH");
		Date d = cal.getTime();
		return df.format(d);
	}
	
	/**
	 * @return the current time with the format "hh:mm:ss"
	 */
	public static String getTime() {
		Calendar cal = Calendar.getInstance();
		DateFormat df = new SimpleDateFormat("HH:mm:ss");
		Date d = cal.getTime();
		return df.format(d);
	}

}
