package com.rickauer.marketmonarch.db.data.processing;

import java.util.ArrayList;
import java.util.List;

import com.rickauer.marketmonarch.db.data.TradeDto;

public class AggregateTradeMetricsCalculator {

	public static double calculateAverageReturnAbsolute(List<TradeDto> trades) {
		return trades.stream()
				.mapToDouble(trade -> SingleTradeMetricsCalculator.calculateProfitAbsolute(trade))
				.average()
				.orElse(0.0);
	}
	
	public static double calculateAverageReturnDecimal(List<TradeDto> trades) {
		return trades.stream()
				.mapToDouble(trade -> SingleTradeMetricsCalculator.calculateProfitDecimal(trade))
				.average()
				.orElse(0.0);
	}
	
	public static double calculateSharpeRatio(List<TradeDto> trades) {
		return calculateSharpeRatio(trades, 0.0);
	}
	
	/*
	 * Shows the overall volatility
	 */
	public static double calculateSharpeRatio(List<TradeDto> trades, double riskFreeRate) {
		List<Double> returns = trades.stream()
				.map(trade -> SingleTradeMetricsCalculator.calculateProfitDecimal(trade))
				.toList();
		
		double averageReturnDecimal = returns.stream()
				.mapToDouble(Double::doubleValue)
				.average()
				.orElse(0.0);
		double standardDeviation = calculateStandardDeviation(returns);
		
		return ( averageReturnDecimal - riskFreeRate) / standardDeviation;
	}
	
	public static double calculateStandardDeviation(List<Double> returns) {
	    double mean = returns.stream()
	    		.mapToDouble(Double::doubleValue)
	    		.average()
	    		.orElse(0.0);
	    double variance = returns.stream()
	    		.mapToDouble(r -> Math.pow(r - mean, 2))
	    		.average().orElse(0.0);
	    return Math.sqrt(variance);
	}
	
	/*
	 * Shows the win loss ratio
	 * 1: 	Break-Even
	 * <1:	Loss
	 */
	public static double calculateProfitFactor(List<TradeDto> trades) {
		return calculateAllProfitsAbsolute(trades) / Math.abs(calculateAllLossesAbsolute(trades));
	}
	
	public static double calculateAllProfitsAbsolute(List<TradeDto> trades) {
		return trades.stream()
				.map(trade -> SingleTradeMetricsCalculator.calculateProfitAbsolute(trade))
				.filter(profit -> profit > 0)
				.mapToDouble(Double::doubleValue)
				.sum();
	}
	
	public static double calculateAllLossesAbsolute(List<TradeDto> trades) {
		double allLosses = trades.stream()
				.map(trade -> SingleTradeMetricsCalculator.calculateProfitAbsolute(trade))
				.filter(profit -> profit <= 0)
				.mapToDouble(Double::doubleValue)
				.sum();
		
		return allLosses == 0 ? 1.0 : allLosses; 
	}
	
	public static double calculateSortinoRatio(List<TradeDto> trades) {
		return calculateSortinoRatio(trades, 0.0);
	}

	/*
	 * The Sortino Ratio shows the return in relation to the loss risk
	 * Sortino Ratio < 0.0:			lossy 
	 * 0.0 <= Sortino Ratio < 0.5:	poor
	 * 0.5 <= Sortino Ratio < 1.0:	medium - returns to low in relation to the risk taken
	 * 1.0 <= Sortino Ratio < 2.0:	good
	 * Sortino Ratio > 2.0:			very good
	 */
	public static double calculateSortinoRatio(List<TradeDto> trades, double riskFreeRate) {
		double averageReturnDecimal = calculateAverageReturnDecimal(trades);
		double downsideDeviation = calculateDownsideDeviation(trades);
		
		return downsideDeviation == 0 ? Double.POSITIVE_INFINITY : ( ( averageReturnDecimal - riskFreeRate) / downsideDeviation );
	}
	
	public static double calculateDownsideDeviation(List<TradeDto> trades) {
		return calculateDownsideDeviation(trades, 0.0);
	}

	public static double calculateDownsideDeviation(List<TradeDto> trades, double riskFreeRate) {
		List<Double> returns = trades.stream()
				.map(trade -> SingleTradeMetricsCalculator.calculateProfitDecimal(trade))
				.toList();
		
		return Math.sqrt(
				returns.stream()
					.map(r -> r - riskFreeRate)
					.filter(diff -> diff < 0)
					.mapToDouble(diff -> diff * diff)
					.average()
					.orElse(0.0)
				);
	}
}
