package com.rickauer.marketmonarch.utils;

public class StockUtils {
	
	private StockUtils() {
		throw new UnsupportedOperationException(StockUtils.class.getCanonicalName() + " is not meant to be instantiated.");
	}
	
	public static int timeToIndex(int time) {
		if (isInRange(time, 570, 630))			// 09:30 to 09:59
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
		else if (isInRange(time, 930, 960))		// 15:30 to 15:59
			return 12;
		
		throw new IllegalArgumentException("Invalid argument: " + time);
	}
	
	private static boolean isInRange(int number, int lowerInclusive, int upperExclusive) {
		return number >= lowerInclusive && number < upperExclusive;
	}
}
