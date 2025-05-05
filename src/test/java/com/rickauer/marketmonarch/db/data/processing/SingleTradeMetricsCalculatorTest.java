package com.rickauer.marketmonarch.db.data.processing;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import com.rickauer.marketmonarch.db.data.TradeDto;

import static com.rickauer.marketmonarch.db.data.processing.SingleTradeMetricsCalculator.*;

public class SingleTradeMetricsCalculatorTest {

	public static TradeDto testData;
	
	@BeforeAll
	static void initializeTestData() {
		testData = new TradeDto();
		
		testData.setSymbol("TEST");
		testData.setBuyOrderId(1);
		testData.setSellOrderId(2);
		testData.setEntryPrice(10.0);
		testData.setExitPrice(20.0);
		testData.setQuantity(200);
		testData.setEntryTime(LocalDateTime.parse("2025-04-23T09:15:00"));
		testData.setExitTime(LocalDateTime.parse("2025-04-23T11:20:10"));
		testData.setStopLoss(5.0);
		testData.setTakeProfit(20.0);
		testData.setOrderEfficiencyRatio(0.2);
	}
	
	@Test
	void calculateProfitPerStockTest() {
		assertEquals(10.0, calculateProfitPerStock(testData));
	}
	
	@Test
	void calculateProfitPercentageTest() {
		assertEquals(100.0, calculateProfitPercentage(testData));
	}
	
	@Test
	void calculateProfitDecimalTest() {
		assertEquals(1.0, calculateProfitDecimal(testData));
	}

	@Test
	void calculateProfitAbsoluteTest() {
		assertEquals(2000.0, calculateProfitAbsolute(testData));
	}
	
	@Test
	void calculateHoldingPeriodTest() {
		assertEquals("02:05:10", calculateHoldingPeriod(testData));
	}
	
	@Test
	void calculateExpectedRiskPerShareAbsoluteTest() {
		assertEquals(5.0, calculateExpectedRiskPerShareAbsolute(testData));
	}
	
	@Test
	void calcualteExpectedRiskPercentTest() {
		assertEquals(50.0, calcualteExpectedRiskPercent(testData));
	}
	
	@Test
	void calculateExpectedRiskPerTradeAbsoluteTest() {
		assertEquals(1000.0, calculateExpectedRiskPerTradeAbsolute(testData));
	}
	
	@Test
	void calculateExpectedProfitTest() {
		assertEquals(10.0, calculateExpectedProfit(testData));
	}
	
	@Test
	void calculateChanceRiskRatioTest() {
		assertEquals(2.0, calculateChanceRiskRatio(testData));
	}
	
	@Test
	void calculateRewardToRiskMultipleTest() {
		assertEquals(2.0, calculateRewardToRiskMultiple(testData));
	}
}
