package com.bjxapp.worker.model;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import com.bjxapp.worker.utils.Utils;

public class DateTime {
	private int year;
	private int month;
	private int day;
	private int hour;
	private int minute;

	public int getYear() {
		return year;
	}

	public void setYear(int year) {
		this.year = year;
	}

	public int getMonth() {
		return month;
	}

	public void setMonth(int month) {
		this.month = month;
	}
	
	public int getDay() {
		return day;
	}

	public void setDay(int day) {
		this.day = day;
	}
	
	public int getHour() {
		return hour;
	}

	public void setHour(int hour) {
		this.hour = hour;
	}
	
	public int getMinute() {
		return minute;
	}

	public void setMinute(int minute) {
		this.minute = minute;
	}
	
	public String getDateString(){
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		Calendar cal = Calendar.getInstance();
		cal.set(year, month, day, hour, minute, 0);
		return format.format(cal.getTime());
	}
	
	public String getTimeString(){
		SimpleDateFormat format = new SimpleDateFormat("HH:mm");
		Calendar cal = Calendar.getInstance();
		cal.set(year, month, day, hour, minute, 0);
		return format.format(cal.getTime());
	}
	
	public String getDateTimeString(){
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm");
		Calendar cal = Calendar.getInstance();
		cal.set(year, month, day, hour, minute, 0);
		return format.format(cal.getTime());
	}
	
	public void setDateTimeString(String datetime){
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm");
		Calendar cal = Calendar.getInstance();
		Date dateResult;
		try {
			if(Utils.isNotEmpty(datetime)){
				dateResult = format.parse(datetime);
			}
			else{
				dateResult = new Date();
			}
			cal.setTime(dateResult);
			year = cal.get(Calendar.YEAR);
			month = cal.get(Calendar.MONTH);
			day = cal.get(Calendar.DAY_OF_MONTH);
			hour = cal.get(Calendar.HOUR_OF_DAY);
			minute = cal.get(Calendar.MINUTE);
		}
		catch (Exception e) {}
	}
	
	public static String getTodayDateTimeString(){
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		Calendar cal = Calendar.getInstance();
		return format.format(cal.getTime());
	}
	
	public static String getTomorrowDateTimeString(){
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.DATE, 1);
		return format.format(cal.getTime());
	}
	
	public static String getTodayString(){
		SimpleDateFormat format = new SimpleDateFormat("M月d日");
		Calendar cal = Calendar.getInstance();
		return format.format(cal.getTime());
	}
	
	public static String getTomorrowString(){
		SimpleDateFormat format = new SimpleDateFormat("M月d日");
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.DATE, 1);
		return format.format(cal.getTime());
	}
}
