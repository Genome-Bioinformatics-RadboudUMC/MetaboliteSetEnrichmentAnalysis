package org.umcn.tml.shared.util;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.joda.time.DateTime;
import org.joda.time.Interval;
import org.joda.time.Period;
import org.joda.time.PeriodType;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

public class TimeMethods {
	
	public static String getCurrentDate (String format) {
		DateFormat dateFormat = new SimpleDateFormat(format);
		Date date = new Date();
		return dateFormat.format(date);
	}
	
	public static String getCurrentDate () {
		return getCurrentDate("yyyy-MM-dd HH:mm:ss");
	}
	
	public static Period getPeriod (String firstDate, String secondDate, String datePattern, PeriodType periodType) {		
		DateTimeFormatter formatter = DateTimeFormat.forPattern(datePattern);
		DateTime firstDateLD = formatter.parseDateTime(firstDate);
		DateTime secondDateLD = formatter.parseDateTime(secondDate);
		
		Interval interval = new Interval(firstDateLD, secondDateLD);
		Period period = interval.toPeriod(periodType);
		return period;
	}
	
	public static String getCurrentAge (String birthdate) {
		String today = getCurrentDate("dd-MM-yyyy");		
		return getSamplingAge(birthdate, today);
	}
	
	public static String getSamplingAge (String birthdate, String samplingDate) {
		Period period = TimeMethods.getPeriod(birthdate, samplingDate, "dd-MM-yyyy", PeriodType.yearMonthDayTime());			
		String age = period.getYears() + "y " + period.getMonths() + "m " + period.getDays() + "d";	
		
		return age;
	}
}
