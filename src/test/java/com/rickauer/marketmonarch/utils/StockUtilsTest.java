package com.rickauer.marketmonarch.utils;

import static org.junit.jupiter.api.Assertions.*;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import com.rickauer.marketmonarch.api.enums.TradingTime;
import com.rickauer.marketmonarch.configuration.DatabaseConnector;
import com.rickauer.marketmonarch.constants.TradingConstants;
import com.rickauer.marketmonarch.db.data.TradeDto;

public class StockUtilsTest {

	
	@BeforeAll
	public static void initializeTestData() {
		DatabaseConnector.INSTANCE.initializeDatabaseConnector();
		DatabaseConnector.INSTANCE.flushDatabaseConnectionEssentials();
	}
	
	@Test
	void firstIntervalLowerBound() {
		String date = "20250221 09:30:00 US/Eastern";
		int result = StockUtils.timeToIndex(StockUtils.convertStringToDateTime(date).getMinuteOfDay());
		assertEquals(0, result);
	}

	@Test
	void firstIntervalUpperBound() {
		String date = "20250221 09:59:59 US/Eastern";
		int result = StockUtils.timeToIndex(StockUtils.convertStringToDateTime(date).getMinuteOfDay());
		assertEquals(0, result);
	}

	@Test
	void secondIntervalLowerBound() {
		String date = "20250221 10:00:00 US/Eastern";
		int result = StockUtils.timeToIndex(StockUtils.convertStringToDateTime(date).getMinuteOfDay());
		assertEquals(1, result);
	}

	@Test
	void secondIntervalUpperBound() {
		String date = "20250221 10:29:59 US/Eastern";
		int result = StockUtils.timeToIndex(StockUtils.convertStringToDateTime(date).getMinuteOfDay());
		assertEquals(1, result);
	}

	@Test
	void thirdIntervalLowerBound() {
		String date = "20250221 10:30:00 US/Eastern";
		int result = StockUtils.timeToIndex(StockUtils.convertStringToDateTime(date).getMinuteOfDay());
		assertEquals(2, result);
	}
	
	@Test
	void thirdIntervalUpperBound() {
		String date = "20250221 10:59:59 US/Eastern";
		int result = StockUtils.timeToIndex(StockUtils.convertStringToDateTime(date).getMinuteOfDay());
		assertEquals(2, result);
	}
	
	@Test
	void fourthIntervalLowerBound() {
		String date = "20250221 11:00:00 US/Eastern";
		int result = StockUtils.timeToIndex(StockUtils.convertStringToDateTime(date).getMinuteOfDay());
		assertEquals(3, result);
	}

	@Test
	void fourthIntervalUpperBound() {
		String date = "20250221 11:29:59 US/Eastern";
		int result = StockUtils.timeToIndex(StockUtils.convertStringToDateTime(date).getMinuteOfDay());
		assertEquals(3, result);
	}

	@Test
	void fifthIntervalLowerBound() {
		String date = "20250221 11:30:00 US/Eastern";
		int result = StockUtils.timeToIndex(StockUtils.convertStringToDateTime(date).getMinuteOfDay());
		assertEquals(4, result);
	}

	@Test
	void fifthIntervalUpperBound() {
		String date = "20250221 11:59:59 US/Eastern";
		int result = StockUtils.timeToIndex(StockUtils.convertStringToDateTime(date).getMinuteOfDay());
		assertEquals(4, result);
	}

	@Test
	void sixthIntervalLowerBound() {
		String date = "20250221 12:00:00 US/Eastern";
		int result = StockUtils.timeToIndex(StockUtils.convertStringToDateTime(date).getMinuteOfDay());
		assertEquals(5, result);
	}

	@Test
	void sixthIntervalUpperBound() {
		String date = "20250221 12:29:59 US/Eastern";
		int result = StockUtils.timeToIndex(StockUtils.convertStringToDateTime(date).getMinuteOfDay());
		assertEquals(5, result);
	}

	@Test
	void seventhIntervalLowerBound() {
		String date = "20250221 12:30:00 US/Eastern";
		int result = StockUtils.timeToIndex(StockUtils.convertStringToDateTime(date).getMinuteOfDay());
		assertEquals(6, result);
	}

	@Test
	void seventhIntervalUpperBound() {
		String date = "20250221 12:59:59 US/Eastern";
		int result = StockUtils.timeToIndex(StockUtils.convertStringToDateTime(date).getMinuteOfDay());
		assertEquals(6, result);
	}

	@Test
	void eighthIntervalLowerBound() {
		String date = "20250221 13:00:00 US/Eastern";
		int result = StockUtils.timeToIndex(StockUtils.convertStringToDateTime(date).getMinuteOfDay());
		assertEquals(7, result);
	}

	@Test
	void eighthIntervalUpperBound() {
		String date = "20250221 13:29:59 US/Eastern";
		int result = StockUtils.timeToIndex(StockUtils.convertStringToDateTime(date).getMinuteOfDay());
		assertEquals(7, result);
	}

	@Test
	void ninthIntervalLowerBound() {
		String date = "20250221 13:30:00 US/Eastern";
		int result = StockUtils.timeToIndex(StockUtils.convertStringToDateTime(date).getMinuteOfDay());
		assertEquals(8, result);
	}

	@Test
	void ninthIntervalUpperBound() {
		String date = "20250221 13:59:59 US/Eastern";
		int result = StockUtils.timeToIndex(StockUtils.convertStringToDateTime(date).getMinuteOfDay());
		assertEquals(8, result);
	}

	@Test
	void tenthIntervalLowerBound() {
		String date = "20250221 14:00:00 US/Eastern";
		int result = StockUtils.timeToIndex(StockUtils.convertStringToDateTime(date).getMinuteOfDay());
		assertEquals(9, result);
	}

	@Test
	void tenthIntervalUpperBound() {
		String date = "20250221 14:29:59 US/Eastern";
		int result = StockUtils.timeToIndex(StockUtils.convertStringToDateTime(date).getMinuteOfDay());
		assertEquals(9, result);
	}

	@Test
	void eleventhIntervalLowerBound() {
		String date = "20250221 14:30:00 US/Eastern";
		int result = StockUtils.timeToIndex(StockUtils.convertStringToDateTime(date).getMinuteOfDay());
		assertEquals(10, result);
	}

	@Test
	void eleventhIntervalUpperBound() {
		String date = "20250221 14:59:59 US/Eastern";
		int result = StockUtils.timeToIndex(StockUtils.convertStringToDateTime(date).getMinuteOfDay());
		assertEquals(10, result);
	}

	@Test
	void twelfthIntervalLowerBound() {
		String date = "20250221 15:00:00 US/Eastern";
		int result = StockUtils.timeToIndex(StockUtils.convertStringToDateTime(date).getMinuteOfDay());
		assertEquals(11, result);
	}

	@Test
	void twelfthIntervalUpperBound() {
		String date = "20250221 15:29:59 US/Eastern";
		int result = StockUtils.timeToIndex(StockUtils.convertStringToDateTime(date).getMinuteOfDay());
		assertEquals(11, result);
	}

	@Test
	void thirteenthIntervalLowerBound() {
		String date = "20250221 15:30:00 US/Eastern";
		int result = StockUtils.timeToIndex(StockUtils.convertStringToDateTime(date).getMinuteOfDay());
		assertEquals(12, result);
	}

	@Test
	void thirteenthIntervalUpperBound() {
		String date = "20250221 15:59:59 US/Eastern";
		int result = StockUtils.timeToIndex(StockUtils.convertStringToDateTime(date).getMinuteOfDay());
		assertEquals(12, result);
	}
	
	@Test
	void invalidInputLowerBound() {
		String date = "20250221 09:29:59 US/Eastern";
		assertThrows(IllegalArgumentException.class, () -> {
			StockUtils.timeToIndex(StockUtils.convertStringToDateTime(date).getMinuteOfDay());
		});
	}

	@Test
	void invalidInputUpperBound() {
		String date = "20250221 16:00:00 US/Eastern";
		assertThrows(IllegalArgumentException.class, () -> {
			StockUtils.timeToIndex(StockUtils.convertStringToDateTime(date).getMinuteOfDay());
		});
	}
	
	@Test
	void getMinuteOfLastEntryTest() {
		int interval = 5;
		int result = StockUtils.getMinuteOfLastEntry(interval);
		assertEquals(955, result);
	}
	
	@Test
	void isValidTradingTimeTooLow() {
		boolean result = StockUtils.isValidTradingTime(TradingTime.NINE_THIRTY.toMinutes() - 1);
		assertFalse(result);
	}
	
	@Test
	void isValidTradingTimeCorrect1() {
		boolean result = StockUtils.isValidTradingTime(TradingTime.NINE_THIRTY.toMinutes());
		assertTrue(result);
	}

	@Test
	void isValidTradingTimeCorrect2() {
		boolean result = StockUtils.isValidTradingTime(TradingTime.SIXTEEN.toMinutes() - 1);
		assertTrue(result);
	}

	@Test
	void isValidTradingTimeTooHigh() {
		boolean result = StockUtils.isValidTradingTime(TradingTime.SIXTEEN.toMinutes());
		assertFalse(result);
	}
	
	@Test
	void calculateTargetPriceProfitTest() {
		double first = 1.0;
		assertEquals(1.05, StockUtils.calculateTargetPrice(first, TradingConstants.TAKE_PROFIT_FACTOR));
	}

	@Test
	void calculateTargetPriceProfitTest2() {
		double first = 1.1;
		assertEquals(1.16, StockUtils.calculateTargetPrice(first, TradingConstants.TAKE_PROFIT_FACTOR));
	}

	@Test
	void calculateTargetPriceStopPriceTest() {
		double first = 1.0;
		assertEquals(0.86, StockUtils.calculateTargetPrice(first, TradingConstants.STOP_LIMIT_STOP_PRICE_FACTOR));
	}

	@Test
	void calculateTargetPriceStopPriceTest2() {
		double first = 1.14;
		assertEquals(0.98, StockUtils.calculateTargetPrice(first, TradingConstants.STOP_LIMIT_STOP_PRICE_FACTOR));
	}

	@Test
	void calculateTargetPriceStopLimitTest() {
		double first = 1.0;
		assertEquals(0.85, StockUtils.calculateTargetPrice(first, TradingConstants.STOP_LIMIT_LIMIT_PRICE_FACTOR));
	}

	@Test
	void calculateTargetPriceStopLimitTest2() {
		double first = 1.14;
		assertEquals(0.97, StockUtils.calculateTargetPrice(first, TradingConstants.STOP_LIMIT_LIMIT_PRICE_FACTOR));
	}
	
	@Test
	void conversionTest1() {
		LocalDateTime ldt = LocalDateTime.parse("1879-03-14T11:30:00.00");
		Timestamp ts = Timestamp.valueOf("1879-03-14 11:30:00.00");
		
		assertEquals(ldt, StockUtils.timestampToLocalDateTime(ts));
	}

	@Test
	void conversionTest2() {
		LocalDateTime ldt = LocalDateTime.parse("1879-03-14T11:30:00.00");
		Timestamp ts = Timestamp.valueOf("1879-03-14 11:30:00.00");
		
		assertEquals(ts, StockUtils.localDateTimeToTimestamp(ldt));
	}
	
	@Test
	void getFirstByIndex() {
		List<TradeDto> testData = new ArrayList<>();
		
		TradeDto first = new TradeDto();
		first.setId(1);
		testData.add(first);

		TradeDto second = new TradeDto();
		second.setId(2);
		testData.add(second);
		
		TradeDto third = new TradeDto();
		third.setId(3);
		testData.add(third);
		
		assertEquals(1, StockUtils.getFirstByIndex(testData).getId());
	}

	@Test
	void getLastByIndex() {
		List<TradeDto> testData = new ArrayList<>();
		TradeDto first = new TradeDto();
		first.setId(1);
		testData.add(first);
		
		TradeDto second = new TradeDto();
		second.setId(2);
		testData.add(second);
		
		TradeDto third = new TradeDto();
		third.setId(3);
		testData.add(third);
		
		assertEquals(3, StockUtils.getLastByIndex(testData).getId());
	}
	
	@Test
	void longToZonedDateTimeTest() {
		                    
		long epochSeconds = 1747763400L;
		String zone = "US/Eastern";
		ZoneId zoneId = ZoneId.of(zone);
		
		ZonedDateTime actual = StockUtils.longToZonedDateTime(epochSeconds, zone);
		ZonedDateTime expected = ZonedDateTime.of(2025, 5, 20, 13, 50, 0, 0, zoneId);
		
		assertEquals(expected, actual);
	}
	
	@Test
	void calculateQuantityTestBasic() {
		double totalCash = 1000.00;
		double entryPrice = 12.34;
		
		double expected = 79;
		double actual = StockUtils.calculateQuantity(totalCash, entryPrice);
		
		assertEquals(expected, actual);
	}

	@Test
	void calculateQuantityTestExactDivision() {
		double totalCash = 980.00;
		double entryPrice = 10.00;
		
		double expected = 96;
		double actual = StockUtils.calculateQuantity(totalCash, entryPrice);
		
		assertEquals(expected, actual);
	}

	@Test
	void calculateQuantityTestSmallCash() {
		double totalCash = 20.00;
		double entryPrice = 25.00;
		
		double expected = 0;
		double actual = StockUtils.calculateQuantity(totalCash, entryPrice);
		
		assertEquals(expected, actual);
	}
	
	@Test
	void isWithinTradingWindowBeforeTest() {
		assertFalse(StockUtils.isWithinTradingWindow(berlinTime(15, 0)));
	}
	
	@Test
	void isWithinTradingWindowAtStartTest() {
		assertTrue(StockUtils.isWithinTradingWindow(berlinTime(16, 15)));
	}
	
	@Test
	void isWithinTradingWindowMiddleTest() {
		assertTrue(StockUtils.isWithinTradingWindow(berlinTime(16, 45)));
	}
	
	@Test
	void isWithinTradingWindowAtEndTest() {
		assertTrue(StockUtils.isWithinTradingWindow(berlinTime(17, 00)));
	}
	
	@Test
	void isWithinTradingWindowAfterTest() {
		assertFalse(StockUtils.isWithinTradingWindow(berlinTime(20, 0)));
	}
	private ZonedDateTime berlinTime(int hour, int minute) {
		return ZonedDateTime.of(2025, 6, 9, hour, minute, 0, 0, ZoneId.of("Europe/Berlin"));
	}
}
