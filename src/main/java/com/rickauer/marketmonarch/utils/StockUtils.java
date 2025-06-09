package com.rickauer.marketmonarch.utils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Timestamp;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Comparator;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.ta4j.core.Bar;
import org.ta4j.core.BarSeries;
import org.ta4j.core.BaseBar;
import org.ta4j.core.BaseBarSeriesBuilder;
import org.ta4j.core.indicators.helpers.LowPriceIndicator;
import org.ta4j.core.indicators.helpers.LowestValueIndicator;
import org.ta4j.core.num.DecimalNum;

import com.rickauer.marketmonarch.MarketMonarch;
import com.rickauer.marketmonarch.api.data.CandleStick;
import com.rickauer.marketmonarch.api.enums.TradingTime;
import com.rickauer.marketmonarch.constants.TradingConstants;
import com.rickauer.marketmonarch.db.data.TradeDto;

public class StockUtils {

	public static final DateTimeFormatter FORMATTER = DateTimeFormat.forPattern("yyyyMMdd HH:mm:ss");
	public static final int TRADING_DAY_INTERVALS = 13;

	private StockUtils() {
		throw new UnsupportedOperationException(
				StockUtils.class.getCanonicalName() + " is not meant to be instantiated.");
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
		int zoneStartIndex = dateWithZone.lastIndexOf(' ');
		return dateWithZone.substring(0, zoneStartIndex);
	}

	public static int getMinuteOfLastEntry(int intervalLength) {
		return TradingTime.SIXTEEN.toMinutes() - intervalLength;
	}

	public static ZonedDateTime stringToZonedDateTime(String string) {
		
		int zoneStartIndex = string.lastIndexOf(' ');
		String dateTimeStr = string.substring(0, zoneStartIndex);
		LocalDateTime localDateTime = stringToLocalDateTime(dateTimeStr);

		String zoneStr = string.substring(zoneStartIndex + 1);
		ZoneId zoneId = ZoneId.of(zoneStr);
		ZonedDateTime zonedDateTime = ZonedDateTime.of(localDateTime, zoneId);

		return zonedDateTime;
	}
	
	public static ZonedDateTime longToZonedDateTime(long date, String zoneId) {
		return Instant.ofEpochSecond(date).atZone(ZoneId.of(zoneId));
	}

	public static LocalDateTime stringToLocalDateTime(String time) {
		return LocalDateTime.parse(time, java.time.format.DateTimeFormatter.ofPattern("yyyyMMdd HH:mm:ss"));
	}

	public static double calculateStopLoss(BarSeries series, int numberOfBarsToCheck) {
		if (series.getBarCount() < numberOfBarsToCheck) {
			return Double.NaN;
		}

		LowPriceIndicator lowPrice = new LowPriceIndicator(series);
		LowestValueIndicator lowestLow = new LowestValueIndicator(lowPrice, numberOfBarsToCheck);

		int lastIndex = series.getEndIndex();
		return lowestLow.getValue(lastIndex).doubleValue();
	}

	public static double calculateTargetPrice(double actualPrice, double factor) {
		BigDecimal raw = BigDecimal.valueOf(actualPrice * factor);
	    return raw.setScale(2, RoundingMode.HALF_UP).doubleValue();
	}

	public static double calculateQuantity(double totalCash, double entryPrice) {
		BigDecimal puffer = BigDecimal.valueOf(TradingConstants.PUFFER_FACTOR);
		BigDecimal cash = BigDecimal.valueOf(totalCash).multiply(puffer).setScale(2, RoundingMode.FLOOR);
		
		BigDecimal price = BigDecimal.valueOf(entryPrice);
		
		BigDecimal quantity = cash.divide(price, 0, RoundingMode.FLOOR);

		return quantity.doubleValue();
	}
	
	public static LocalDateTime timestampToLocalDateTime(Timestamp time) {
		return time.toLocalDateTime();
	}

	public static Timestamp localDateTimeToTimestamp(LocalDateTime time) {
		return Timestamp.valueOf(time);
	}

	public static TradeDto getFirstByIndex(List<TradeDto> trades) {
		return trades.stream()
				.min(Comparator.comparingInt(TradeDto::getId))
				.orElse(null);
	}

	public static TradeDto getLastByIndex(List<TradeDto> trades) {
		return trades.stream()
				.max(Comparator.comparingInt(TradeDto::getId))
				.orElse(null);
	}
	
	public static String getCurrentTimestampAsString() {
		return String.valueOf(System.currentTimeMillis());
	}
	
	public static boolean isWithinTradingWindow(ZonedDateTime localTime) {
		ZonedDateTime newYorkTime = localTime.withZoneSameInstant(ZoneId.of("US/Eastern"));
		LocalTime nyTime = newYorkTime.toLocalTime();
		
		LocalTime start = LocalTime.of(10, 15);
		LocalTime end = LocalTime.of(13, 30);
		
		return !nyTime.isBefore(start) && !nyTime.isAfter(end);
	}
}
