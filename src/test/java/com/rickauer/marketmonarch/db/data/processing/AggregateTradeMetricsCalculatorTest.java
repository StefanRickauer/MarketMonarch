package com.rickauer.marketmonarch.db.data.processing;

import static org.junit.jupiter.api.Assertions.*;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import com.rickauer.marketmonarch.db.data.TradeDto;

public class AggregateTradeMetricsCalculatorTest {

	private static List<TradeDto> testData;
	
	@BeforeAll
	static void initializeTestData() {
		testData = new ArrayList<>();
		TradeDto firstTrade = new TradeDto();
		firstTrade.setEntryPrice(100.0);
		firstTrade.setExitPrice(105.0);
		firstTrade.setQuantity(10);
		testData.add(firstTrade);
		
		TradeDto secondTrade = new TradeDto();
		secondTrade.setEntryPrice(200.0);
		secondTrade.setExitPrice(190.0);
		secondTrade.setQuantity(5);
		testData.add(secondTrade);

		TradeDto thirdTrade = new TradeDto();
		thirdTrade.setEntryPrice(150.0);
		thirdTrade.setExitPrice(165.0);
		thirdTrade.setQuantity(8);
		testData.add(thirdTrade);

		TradeDto fourthTrade = new TradeDto();
		fourthTrade.setEntryPrice(300.0);
		fourthTrade.setExitPrice(285.0);
		fourthTrade.setQuantity(4);
		testData.add(fourthTrade);
	}
	
	@Test
	void calculateAverageProfitTest() {
		double avgProfitAbsolute = AggregateTradeMetricsCalculator.calculateAverageReturnAbsolute(testData);
		assertEquals(15.0, avgProfitAbsolute);
	}
	
	@Test
	void calculateSharpeRatioTest() {
		double sharpeRatio = AggregateTradeMetricsCalculator.calculateSharpeRatio(testData, 0);
		assertEquals("0.1925", truncateUsingBigDecimal(sharpeRatio));
	}

	@Test
	void calculateStandardDeviationTest() {
		List<Double> test = List.of(0.05, -0.05, 0.1, -0.05);
		double stdDev = AggregateTradeMetricsCalculator.calculateStandardDeviation(test);
		assertEquals("0.0650", truncateUsingBigDecimal(stdDev));
	}
	
	@Test
	void calculateProfitFactorTest() {
		double profitFactor = AggregateTradeMetricsCalculator.calculateProfitFactor(testData);
		assertEquals("1.5455", truncateUsingBigDecimal(profitFactor));
	}
	
	@Test
	void calculateAllProfitsTest() {
		double allProfits = AggregateTradeMetricsCalculator.calculateAllProfitsAbsolute(testData);
		assertEquals(170.0, allProfits);
	}
	
	@Test
	void calculateAllLossesAbsoluteTest() {
		double allLosses = AggregateTradeMetricsCalculator.calculateAllLossesAbsolute(testData);
		assertEquals(-110.0, allLosses);
	}
	
	@Test
	void calculateSortinoRatioTest() {
		double sortinoRatio = AggregateTradeMetricsCalculator.calculateSortinoRatio(testData);
		assertEquals(0.25, sortinoRatio);
	}
	
	private static String truncateUsingBigDecimal(double value) {
		BigDecimal valueAsBD = new BigDecimal(value).setScale(4, RoundingMode.HALF_EVEN);
		return valueAsBD.toString();
	}
	
	void calculateOverallProfitTest() {
		fail();
	}
}
