package com.rickauer.marketmonarch.utils;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

public class StockUtilsTest {

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
}
