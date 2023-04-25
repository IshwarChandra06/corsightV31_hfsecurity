package com.eikona.mata.util;

import static com.eikona.mata.constants.ApplicationConstants.DEPRECATION;

import java.util.Calendar;
import java.util.Date;

import org.springframework.stereotype.Component;

import com.eikona.mata.entity.Shift;

@Component
public class CalendarUtil {

	public Date getConvertedDate(Date date, int hr, int min, int sec) {
		Calendar calender = Calendar.getInstance();
		calender.setTime(date);
		calender.set(Calendar.HOUR, hr);
		calender.set(Calendar.MINUTE, min);
		calender.set(Calendar.SECOND, sec);
		
		return calender.getTime();
	}
	public Calendar getCalendar(Date date, int hr, int min, int sec) {
		Calendar calender = Calendar.getInstance();
		calender.setTime(date);
		calender.set(Calendar.HOUR, hr);
		calender.set(Calendar.MINUTE, min);
		calender.set(Calendar.SECOND, sec);
		
		return calender;
	}
	public Calendar dateToCalendar(Date date) {

		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		return calendar;

	}
	public Date getConvertedDate(Date date, int day, int hr, int min, int sec) {
		Calendar calender = Calendar.getInstance();
		calender.setTime(date);
		calender.set(Calendar.DATE, day);
		//calender.add(Calendar.DATE, day);
		calender.set(Calendar.HOUR, hr);
		calender.set(Calendar.MINUTE, min);
		calender.set(Calendar.SECOND, sec);
		
		return calender.getTime();
	}
	
	@SuppressWarnings(DEPRECATION)
	public Calendar getStartCalenderFromShift(Shift shift, Calendar currentCalendar) {
		 Calendar calender =  Calendar.getInstance();
		 calender.set(currentCalendar.get(Calendar.YEAR), currentCalendar.getTime().getMonth(),
				currentCalendar.getTime().getDate(), shift.getStartTime().getHours(),
				shift.getStartTime().getMinutes(), shift.getStartTime().getSeconds());
		 
		 return calender;
	}
	
	@SuppressWarnings(DEPRECATION)
	public Calendar getEndCalenderFromShift(Shift shift, Calendar currentCalendar) {
		 Calendar calender =  Calendar.getInstance();
		 calender.set(currentCalendar.get(Calendar.YEAR), currentCalendar.getTime().getMonth(),
				currentCalendar.getTime().getDate(), shift.getEndTime().getHours(),
				shift.getEndTime().getMinutes(), shift.getEndTime().getSeconds());
		 
		 return calender;
	}
	
	@SuppressWarnings(DEPRECATION)
	public Calendar getStartCalenderFromShift(Shift shift, Calendar currentCalendar, int hr) {
		 Calendar calender =  Calendar.getInstance();
		 calender.set(currentCalendar.get(Calendar.YEAR), currentCalendar.getTime().getMonth(),
				currentCalendar.getTime().getDate(), shift.getStartTime().getHours(),
				shift.getStartTime().getMinutes(), shift.getStartTime().getSeconds());
		 calender.add(Calendar.HOUR_OF_DAY, hr);
		 return calender;
	}
	
	@SuppressWarnings(DEPRECATION)
	public Calendar getEndCalenderFromShift(Shift shift, Calendar currentCalendar, int hr) {
		 Calendar calender =  Calendar.getInstance();
		 calender.set(currentCalendar.get(Calendar.YEAR), currentCalendar.getTime().getMonth(),
				currentCalendar.getTime().getDate(), shift.getEndTime().getHours(),
				shift.getEndTime().getMinutes(), shift.getEndTime().getSeconds());
		 calender.add(Calendar.HOUR_OF_DAY, hr);
		 return calender;
	}
}
