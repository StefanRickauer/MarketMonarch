package com.rickauer.marketmonarch.db.data.processing;

import java.time.Duration;
import java.time.temporal.ChronoUnit;

import com.rickauer.marketmonarch.db.data.TradeDto;

public class SingleTradeMetricsCalculator {

	; // write unit tests
	
	public static double calculateProfitAbsolute(TradeDto data) {
		return ( data.getExitPrice() - data.getEntryPrice() ) * data.getQuantity();
	}
	
	public static double calculateProfitPercentage(TradeDto data) {
		return ( ( data.getExitPrice() - data.getEntryPrice() ) * 100 ) / data.getEntryPrice();
	}
	
	public static double calculateProfitDecimal(TradeDto data) {
		return  ( data.getExitPrice() - data.getEntryPrice() ) / data.getEntryPrice();
	}
	
	public static String calculateHoldingPeriod(TradeDto data) {
		long durationInSeconds = data.getEntryTime().until(data.getExitTime(), ChronoUnit.SECONDS);
		long hours = durationInSeconds / 3600;
		long minutes = ( durationInSeconds % 3600 ) / 60;
		long seconds = durationInSeconds % 60;
		
		return String.format("%02d:%02d:%02d", hours, minutes, seconds);
	}
	
	public static double calculateExpectedRisk(TradeDto data) {
		return data.getEntryPrice() - data.getStopLoss();
	}
	
	public static double calculateExpectedProfit(TradeDto data) {
		return data.getTakeProfit() - data.getEntryPrice();
	}
	
	public static double calculateChanceRiskRatio(TradeDto data) {
		return calculateExpectedProfit(data) / calculateExpectedRisk(data);
	}
	
	/**
	 * R-Multiple (Reward-to-Risk Multiple) is a metric that shows the ratio of the realized profit/loss in 
	 * relation to the expected risk.
	 * R = 3.0 means that triple the amount of the expected risk was earned.
	 * 
	 * Interpretation:
	 * R < 0: Loss
	 * R = 0: Break-Even, no profit no loss
	 * 0 < R < 1: Profit less than risk
	 * R = 1: Profit and risk are equal
	 * R > 1: Profit exceeds risk
	 * 
	 * Use:
	 * 1. Compare Trades
	 * 2. Performance analysis, e.g. how often is > 2R reached?
	 * 3. Strategy optimization
	 * 4. Monte-Carlo-Analysis
	 */
	public static double calculateRewardToRiskMultiple(TradeDto data) {
		return calculateProfitAbsolute(data) / calculateExpectedRisk(data);
	}
}
