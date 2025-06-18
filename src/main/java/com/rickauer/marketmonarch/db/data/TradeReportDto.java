package com.rickauer.marketmonarch.db.data;

import java.util.List;

import com.rickauer.marketmonarch.db.FinancialDataDao;
import com.rickauer.marketmonarch.db.data.processing.AggregateTradeMetricsCalculator;
import com.rickauer.marketmonarch.db.data.processing.SingleTradeMetricsCalculator;

public class TradeReportDto {

	// general data
	List<TradeDto> _allTrades;
	TradeDto _tradingData;
	double _profitPerStock;
	double _profitPercentage;
	double _profitAbsolute;
	String _holdingPeriod;
	
	// trade specific data
	double _expectedRiskPerShareAbsolute;
	double _expectedRiskPercent;
	double _expectedRiskPerTradeAbsolute;
	double _expectedProfit;
	double _chanceRiskRatio;
	double _rewardToRiskMultiple;
	
	// all trades data
	double _averageReturnAbsolute;
	double _sharpeRatio;
	double _profitFactor;
	double _allProfitsAbsolute;
	double _allLossesAbsolute;
	double _sortinoRatio;
	
	; // overall profit -> implement in AggregateTradeMetricsCalculator and create new DB only for account balances!
	; // Database call from constructor is a bad idea! Use in createSessionReport (this class!)
	public TradeReportDto(FinancialDataDao db, TradeDto tradingData) {
		_allTrades = db.getAllTrades();
		_tradingData = tradingData;
		_profitPerStock = SingleTradeMetricsCalculator.calculateProfitPerStock(tradingData);
		_profitPercentage = SingleTradeMetricsCalculator.calculateProfitPercentage(tradingData);
		_profitAbsolute = SingleTradeMetricsCalculator.calculateProfitAbsolute(tradingData);
		_holdingPeriod = SingleTradeMetricsCalculator.calculateHoldingPeriod(tradingData);
		_expectedRiskPerShareAbsolute = SingleTradeMetricsCalculator.calculateExpectedRiskPerShareAbsolute(tradingData);
		_expectedRiskPercent = SingleTradeMetricsCalculator.calcualteExpectedRiskPercent(tradingData);
		_expectedRiskPerTradeAbsolute = SingleTradeMetricsCalculator.calculateExpectedRiskPerTradeAbsolute(tradingData);
		_expectedProfit = SingleTradeMetricsCalculator.calculateExpectedProfit(tradingData);
		_chanceRiskRatio = SingleTradeMetricsCalculator.calculateChanceRiskRatio(tradingData);
		_rewardToRiskMultiple = SingleTradeMetricsCalculator.calculateRewardToRiskMultiple(tradingData);
		_averageReturnAbsolute = AggregateTradeMetricsCalculator.calculateAverageReturnAbsolute(_allTrades);
		_sharpeRatio = AggregateTradeMetricsCalculator.calculateSharpeRatio(_allTrades);
		_profitFactor = AggregateTradeMetricsCalculator.calculateProfitFactor(_allTrades);
		_allProfitsAbsolute = AggregateTradeMetricsCalculator.calculateAllProfitsAbsolute(_allTrades);
		_allLossesAbsolute = AggregateTradeMetricsCalculator.calculateAllLossesAbsolute(_allTrades);
		_sortinoRatio = AggregateTradeMetricsCalculator.calculateSortinoRatio(_allTrades);
	}
	
	public double getProfitPerStock() {
		return _profitPerStock;
	}
	
	public double getProfitPercentage() {
		return _profitPercentage;
	}
	
	public double getProfitAbsolute() {
		return _profitAbsolute;
	}
	
	public String getHoldingPeriod() {
		return _holdingPeriod;
	}
	
	public double getExcpetedRiskPerShareAbsolute() {
		return _expectedRiskPerShareAbsolute;
	}
	
	public double getExpectedRiskPercent() {
		return _expectedRiskPercent;
	}
	
	public double getExpectedRiskPerTradeAbsolute() {
		return _expectedRiskPerTradeAbsolute;
	}
	
	public double getExpectedProfit() {
		return _expectedProfit;
	}
	
	public double getChanceRiskRatio() {
		return _chanceRiskRatio;
	}
	
	public double getRewardToRiskMultiple() {
		return _rewardToRiskMultiple;
	}
	
	public double getAverageReturnAbsolute() {
		return _averageReturnAbsolute;
	}
	
	public double getSharpeRatio() {
		return _sharpeRatio;
	}
	
	public double getProfitFactor() {
		return _profitFactor;
	}
	
	public double getAllProfitsAbsolute() {
		return _allProfitsAbsolute;
	}
	
	public double getAllLossesAbsolute() {
		return _allLossesAbsolute;
	}
	
	public double getSortinoRatio() {
		return _sortinoRatio;
	}
}
