package com.rickauer.marketmonarch.utils;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

public class StockUtils {
	
	public static final DateTimeFormatter FORMATTER = DateTimeFormat.forPattern("yyyyMMdd HH:mm:ss");
	public static final int TRADING_DAY_INTERVALS = 13;
	
	
	private StockUtils() {
		throw new UnsupportedOperationException(StockUtils.class.getCanonicalName() + " is not meant to be instantiated.");
	}
	
	public static int timeToIndex(int time) {
		if (isInRange(time, TradingTime.NINE_THIRTY.toMinutes(), TradingTime.TEN.toMinutes()))			
			return 0;
		else if (isInRange(time, TradingTime.TEN.toMinutes(), TradingTime.TEN_THIRTY.toMinutes()))		
			return 1;
		else if (isInRange(time, TradingTime.TEN_THIRTY.toMinutes(), TradingTime.ELEVEN.toMinutes()))		
			return 2;
		else if (isInRange(time, TradingTime.ELEVEN.toMinutes(), TradingTime.ELEVEN_THIRTY.toMinutes()))		
			return 3;
		else if (isInRange(time, TradingTime.ELEVEN_THIRTY.toMinutes(), TradingTime.TWELVE.toMinutes()))		
			return 4;
		else if (isInRange(time, TradingTime.TWELVE.toMinutes(), TradingTime.TWELVE_THIRTY.toMinutes()))		
			return 5;
		else if (isInRange(time, TradingTime.TWELVE_THIRTY.toMinutes(), TradingTime.THIRTEEN.toMinutes()))		
			return 6;
		else if (isInRange(time, TradingTime.THIRTEEN.toMinutes(), TradingTime.THIRTEEN_THIRTY.toMinutes()))		
			return 7;
		else if (isInRange(time, TradingTime.THIRTEEN_THIRTY.toMinutes(), TradingTime.FOURTEEN.toMinutes()))		
			return 8;
		else if (isInRange(time, TradingTime.FOURTEEN.toMinutes(), TradingTime.FOURTEEN_THIRTY.toMinutes()))		
			return 9;
		else if (isInRange(time, TradingTime.FOURTEEN_THIRTY.toMinutes(), TradingTime.FIFTEEN.toMinutes()))		
			return 10;
		else if (isInRange(time, TradingTime.FIFTEEN.toMinutes(), TradingTime.FIFTEEN_THIRTY.toMinutes()))		
			return 11;
		else if (isInRange(time, TradingTime.FIFTEEN_THIRTY.toMinutes(), TradingTime.SIXTEEN.toMinutes()))		
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
		return TradingTime.SIXTEEN.toMinutes() - intervalLength;
	}
}
