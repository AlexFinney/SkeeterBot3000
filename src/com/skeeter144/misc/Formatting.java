package com.skeeter144.misc;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class Formatting {
	public static String msToReadable(long duration) {
		Date date = new Date(duration);
		DateFormat formatter = new SimpleDateFormat("HH:mm:ss.SSS");
		formatter.setTimeZone(TimeZone.getTimeZone("UTC"));
		
		return  formatter.format(date);
	}
}
