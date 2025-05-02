package com.rickauer.marketmonarch.db.data.processing;

import java.util.ArrayList;
import java.util.List;

import com.rickauer.marketmonarch.db.data.TradeDto;

public class AggregateTradeMetricsCalculator {

	public static double calculateAverageProfitAbsolute(List<TradeDto> trades) {
		return trades.stream()
				.mapToDouble(trade -> SingleTradeMetricsCalculator.calculateProfitAbsolute(trade))
				.average()
				.orElse(0.0);
	}
	
	public static double calculateSharpeRatio(List<TradeDto> trades) {
		return calculateSharpeRatio(trades, 0.0);
	}
	
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
}
