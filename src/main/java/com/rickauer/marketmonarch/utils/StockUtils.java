package com.rickauer.marketmonarch.utils;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.ta4j.core.BarSeries;
import org.ta4j.core.indicators.helpers.LowPriceIndicator;
import org.ta4j.core.indicators.helpers.LowestValueIndicator;

import com.rickauer.marketmonarch.MarketMonarch;
import com.rickauer.marketmonarch.api.enums.TradingTime;

public class StockUtils {

	public static final DateTimeFormatter FORMATTER = DateTimeFormat.forPattern("yyyyMMdd HH:mm:ss");
	public static final int TRADING_DAY_INTERVALS = 13;
	private static final int DATESTRING_LENGTH_WITHOUT_TIMEZONE = 17;
	
	
	private StockUtils() {
		throw new UnsupportedOperationException(StockUtils.class.getCanonicalName() + " is not meant to be instantiated.");
	}
	
	public static int timeToIndex(int minuteOfDay) {
		if (isInRange(minuteOfDay, TradingTime.NINE_THIRTY.toMinutes(), TradingTime.TEN.toMinutes()))			
			return 0;
		else if (isInRange(minuteOfDay, TradingTime.TEN.toMinutes(), TradingTime.TEN_THIRTY.toMinutes()))		
			return 1;
		else if (isInRange(minuteOfDay, TradingTime.TEN_THIRTY.toMinutes(), TradingTime.ELEVEN.toMinutes()))		
			return 2;
		else if (isInRange(minuteOfDay, TradingTime.ELEVEN.toMinutes(), TradingTime.ELEVEN_THIRTY.toMinutes()))		
			return 3;
		else if (isInRange(minuteOfDay, TradingTime.ELEVEN_THIRTY.toMinutes(), TradingTime.TWELVE.toMinutes()))		
			return 4;
		else if (isInRange(minuteOfDay, TradingTime.TWELVE.toMinutes(), TradingTime.TWELVE_THIRTY.toMinutes()))		
			return 5;
		else if (isInRange(minuteOfDay, TradingTime.TWELVE_THIRTY.toMinutes(), TradingTime.THIRTEEN.toMinutes()))		
			return 6;
		else if (isInRange(minuteOfDay, TradingTime.THIRTEEN.toMinutes(), TradingTime.THIRTEEN_THIRTY.toMinutes()))		
			return 7;
		else if (isInRange(minuteOfDay, TradingTime.THIRTEEN_THIRTY.toMinutes(), TradingTime.FOURTEEN.toMinutes()))		
			return 8;
		else if (isInRange(minuteOfDay, TradingTime.FOURTEEN.toMinutes(), TradingTime.FOURTEEN_THIRTY.toMinutes()))		
			return 9;
		else if (isInRange(minuteOfDay, TradingTime.FOURTEEN_THIRTY.toMinutes(), TradingTime.FIFTEEN.toMinutes()))		
			return 10;
		else if (isInRange(minuteOfDay, TradingTime.FIFTEEN.toMinutes(), TradingTime.FIFTEEN_THIRTY.toMinutes()))		
			return 11;
		else if (isInRange(minuteOfDay, TradingTime.FIFTEEN_THIRTY.toMinutes(), TradingTime.SIXTEEN.toMinutes()))		
			return 12;
		
		throw new IllegalArgumentException("Invalid argument: " + minuteOfDay);
	}
	
	private static boolean isInRange(int minuteOfDay, int lowerInclusive, int upperExclusive) {
		return minuteOfDay >= lowerInclusive && minuteOfDay < upperExclusive;
	}
	
	public static boolean isValidTradingTime(int minuteOfDay) {
		return minuteOfDay >= TradingTime.NINE_THIRTY.toMinutes() && minuteOfDay < TradingTime.SIXTEEN.toMinutes();
	}
	
	public static DateTime convertStringToDateTime(String date) {
		return DateTime.parse(extractDate(date), FORMATTER);	
	}
	
	private static String extractDate(String dateWithZone) {
		return dateWithZone.substring(0, DATESTRING_LENGTH_WITHOUT_TIMEZONE);
	}
	
	public static int getMinuteOfLastEntry(int intervalLength) {
		return TradingTime.SIXTEEN.toMinutes() - intervalLength;
	}
	
	public static ZonedDateTime toZonedDateTime(String string) {
		String dateTimeStr = string.substring(0, DATESTRING_LENGTH_WITHOUT_TIMEZONE);
		java.time.format.DateTimeFormatter formatter = java.time.format.DateTimeFormatter.ofPattern("yyyyMMdd HH:mm:ss");
		LocalDateTime localDateTime = LocalDateTime.parse(dateTimeStr, formatter);
		
		ZoneId systemZone = ZoneId.systemDefault();
		ZonedDateTime zonedDateTime = ZonedDateTime.of(localDateTime, systemZone);
		
		return zonedDateTime;
	}
	
	public static double calculateStopLoss(BarSeries series, int periodInBars) {
		if (series.getBarCount() < periodInBars) {
			return Double.NaN;
		}
		
		LowPriceIndicator lowPrice = new LowPriceIndicator(series);
		LowestValueIndicator lowestLow = new LowestValueIndicator(lowPrice, periodInBars);
		
		int lastIndex = series.getEndIndex();
		return lowestLow.getValue(lastIndex).doubleValue();
	}
}
