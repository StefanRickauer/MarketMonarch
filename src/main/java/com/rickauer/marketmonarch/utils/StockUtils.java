package com.rickauer.marketmonarch.utils;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

public class StockUtils {
	
	public static final DateTimeFormatter FORMATTER = DateTimeFormat.forPattern("yyyyMMdd HH:mm:ss");
	public static final int TRADING_DAY_INTERVALS = 13;
	
	; // Restliche Werte ergänzen!
	private static final int FOUR_PM = 960;
	
	private StockUtils() {
		throw new UnsupportedOperationException(StockUtils.class.getCanonicalName() + " is not meant to be instantiated.");
	}
	
	public static int timeToIndex(int time) {
		if (isInRange(time, 570, 600))			// 09:30 to 09:59
			return 0;
		else if (isInRange(time, 600, 630))		// 10:00 to 10:29
			return 1;
		else if (isInRange(time, 630, 660))		// 10:30 to 10:59
			return 2;
		else if (isInRange(time, 660, 690))		// 11:00 to 11:29
			return 3;
		else if (isInRange(time, 690, 720))		// 11:30 to 11:59
			return 4;
		else if (isInRange(time, 720, 750))		// 12:00 to 12:29
			return 5;
		else if (isInRange(time, 750, 780))		// 12:30 to 12:59
			return 6;
		else if (isInRange(time, 780, 810))		// 13:00 to 13:29
			return 7;
		else if (isInRange(time, 810, 840))		// 13:30 to 13:59
			return 8;
		else if (isInRange(time, 840, 870))		// 14:00 to 14:29
			return 9;
		else if (isInRange(time, 870, 900))		// 14:30 to 14:59
			return 10;
		else if (isInRange(time, 900, 930))		// 15:00 to 15:29
			return 11;
		else if (isInRange(time, 930, FOUR_PM))		// 15:30 to 15:59
			return 12;
		
		throw new IllegalArgumentException("Invalid argument: " + time);
	}
	
	private static boolean isInRange(int number, int lowerInclusive, int upperExclusive) {
		return number >= lowerInclusive && number < upperExclusive;
	}
	
	public static DateTime convertStringToDateTime(String date) {
		return DateTime.parse(extractDate(date), FORMATTER);	
	}
	
	private static String extractDate(String dateWithZone) {
		String lower = dateWithZone.toLowerCase();
		String upper = dateWithZone.toUpperCase();
		
		int index = 0;
		
		for (char c : lower.toCharArray()) {
			if (!(c == upper.toCharArray()[index])) 
				return dateWithZone.substring(0, index).trim();
			index++;
			
		}
		return dateWithZone;
	}
	
	public static int getMinuteOfLastEntry(int intervalLength) {
		return FOUR_PM - intervalLength;
	}
}
