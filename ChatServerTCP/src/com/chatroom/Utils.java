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
		DateFormat df = new SimpleDateFormat("HH:mm:ss");
		Date d = cal.getTime();
		return df.format(d);
	}

}
