package com.rickauer.marketmonarch.data;

import java.util.ArrayList;
import java.util.List;

import org.joda.time.DateTime;

import com.ib.client.Contract;
import com.rickauer.marketmonarch.utils.StockUtils;

public class StockMetrics {

	private Object _lock;

	private Contract _contract;
	private List<CandleStick> _candleChart;
	private double[] _historicalVolumesByInterval;
	private double[] _volumeCountsPerInterval;
	private double _rvol;
	private double _profitLossChange;
	
	
	public StockMetrics(Contract contract, Object lock) {
		
		_lock = lock;

		_contract = contract;
		_candleChart = new ArrayList<>();
		_historicalVolumesByInterval = new double[StockUtils.TRADING_DAY_INTERVALS];
		_volumeCountsPerInterval = new double[StockUtils.TRADING_DAY_INTERVALS];
		_rvol = 0;
		_profitLossChange = 0;
	}
	
	public String getSymbol() {
		return _contract.symbol();
	}
	
	public double getRelativeVolume() {
		return _rvol;
	}
	
	public double getProfitLossChange() {
		return _profitLossChange;
	}
	
	// call from within InteractiveBrokersApiRequestHandler::historicalData
	public void addCandleStick(CandleStick candleStick) {
		_candleChart.add(candleStick);
	}
	
	// call from within InteractiveBrokersApiRequestHandler::historicalDataEnd; executed once the last candle is received
	public void calculateRelativeTradingVolume() {
		
		for (CandleStick candleStick : _candleChart) {
			if (candleStick.getDate().getDayOfMonth() == _candleChart.getLast().getDate().getDayOfMonth()) {
				continue;
			}
			
			_historicalVolumesByInterval[StockUtils.timeToIndex(candleStick.getDate().getMinuteOfDay())] += Double.parseDouble(candleStick.toString());
			_volumeCountsPerInterval[StockUtils.timeToIndex(candleStick.getDate().getMinuteOfDay())]++;
		}
		
		updateRelativeVolume();
	}
	
	public void updateRelativeVolume() {
		_rvol = Double.parseDouble(_candleChart.getLast().toString()) / getAverageTradingVolumeForInterval(_candleChart.getLast().getDate());
	}

	private double getAverageTradingVolumeForInterval(DateTime date) {
		return _historicalVolumesByInterval[StockUtils.timeToIndex(date.getMinuteOfDay())] / _volumeCountsPerInterval[StockUtils.timeToIndex(date.getMinuteOfDay())];
	}
	
	; // Add method that adds a candle stick and update the rvol. This is for live data. However,
	// live data sticks are only 5 second intervals and not 5 minute intervals!
	// The method to receive live data is InteractiveBrokersApiRequestHandler::realtimeBar
	
	public void calculateProfitLossChange() {
		
		double actualPrice = _candleChart.getLast().getClose();
		double yesterdaysClosePrice = 0;
		
		// If today is Monday, subtract three days to get Friday, otherwise one day to get yesterday
		int subtrahend = _candleChart.getLast().getDate().getDayOfWeek() == 1 ? 3 : 1;
		
		for (CandleStick candleStick : _candleChart) {
			if (candleStick.getDate().getDayOfMonth() != ( _candleChart.getLast().getDate().getDayOfMonth() - subtrahend ) ) {
				continue;
			}
			
			// Code will reach this point only for yesterday's (or, in case today is Monday, Friday's) entries
			if (candleStick.getDate().getMinuteOfDay() == StockUtils.getMinuteOfLastEntry(5))	// 955 is 15:55 PM, which is the last entry for a trading day (5-Minute-Intervalls)
				yesterdaysClosePrice = candleStick.getClose();
		}
		
		_profitLossChange = ( (actualPrice - yesterdaysClosePrice) / yesterdaysClosePrice ) * 100; 
	}
}
