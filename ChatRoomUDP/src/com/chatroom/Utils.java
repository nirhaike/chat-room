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
	public static String changeDateHandShake(String str){
		DateFormat format = new SimpleDateFormat("yyyy/MM/dd/HH");
		Date d;
		try {
			d = format.parse(str);
		} catch (ParseException e) {
			return "---";
		}
		Date hourBef = new Date(d.getTime()-1000*60*60);
		return format.format(hourBef);
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
		DateFormat df = new SimpleDateFormat("HH:mm:ss.SSS");
		Date d = cal.getTime();
		return df.format(d);
	}
	/**
	 * tries to parse the string to an integer
	 * @param str any string
	 * @return integer represents 'str'
	 */
	public static Integer tryParse(String str) {
		  int value;
		  try {
			  value = Integer.parseInt(str);
		  } catch (NumberFormatException nfe) {
			  value = -1;
		  }
		  return value;
	}
	
	public static int getPacketId(String str) {
		if (str == null)
			return -1;
		if (str.contains(",")) {
			return Utils.tryParse(str.substring(0, str.indexOf(",")));
		}
		return -1;
	}
	
	public static String removeIdFromPacket(String str) {
		return str.substring(str.indexOf(",") + 1);
	}

}
