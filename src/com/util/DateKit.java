package com.util;

import java.text.SimpleDateFormat;
import java.util.Date;

public class DateKit {

	/**
	 * 获取系统日期
	 * @return
	 */
	public static String getDate(){
		long currentTime = System.currentTimeMillis();
		Date date = new Date(currentTime);
		SimpleDateFormat dateformat = new SimpleDateFormat("yyyy-MM-dd");
		String sDateSuffix = dateformat.format(date);
		date=null;
		return sDateSuffix;
	}
	
	/**
	 * 获取系统时间
	 * @return
	 */
	public static String getTime(){
		long currentTime = System.currentTimeMillis();
		Date date = new Date(currentTime);
		SimpleDateFormat dateformat = new SimpleDateFormat("HH:mm:ss");
		String sDateSuffix = dateformat.format(date);
		date=null;
		return sDateSuffix;
	}
	
	
	/**
	 * 获取延迟时间，单位：毫秒(30分钟为 30*60*1000=1800000)
	 * @return
	 */
	public static String getDelineTime(long millSeconds){
		long currentTime = System.currentTimeMillis();
		Date date = new Date(currentTime-millSeconds);
		SimpleDateFormat dateformat = new SimpleDateFormat("HH:mm:ss");
		String sDateSuffix = dateformat.format(date);
		date=null;
		return sDateSuffix;
	}
	
	
	/**
	 * 获取系统日期时间
	 * @return
	 */
	public static String getDateTime(){
		long currentTime = System.currentTimeMillis();
		Date date = new Date(currentTime);
		SimpleDateFormat dateformat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String sDateSuffix = dateformat.format(date);
		date=null;
		return sDateSuffix;
	}
}
