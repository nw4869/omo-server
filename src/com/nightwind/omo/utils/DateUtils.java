package com.nightwind.omo.utils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

import com.owlike.genson.Genson;
import com.owlike.genson.GensonBuilder;

public class DateUtils {

	public static DateFormat getDateFormat() { return new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss"); }

	public static Genson getGenson() { return  new GensonBuilder().useDateFormat(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")).create(); }
	
		
}
