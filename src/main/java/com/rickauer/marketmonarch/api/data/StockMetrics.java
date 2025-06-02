package com.rickauer.marketmonarch.api.data;

import java.util.ArrayList;
import java.util.List;

import org.joda.time.DateTime;

import com.ib.client.Contract;
import com.rickauer.marketmonarch.utils.StockUtils;

public class StockMetrics {

	private CandleSeries _candleStickChart;
	private double[] _historicalVolumesByInterval;
	private double[] _volumeCountsPerInterval;
	private double _rvol;
	private double _profitLossChange;
	private long _companyShareFloat;
	
	public StockMetrics(Contract contract) {
		
		_candleStickChart = new CandleSeries(contract);
		_historicalVolumesByInterval = new double[StockUtils.TRADING_DAY_INTERVALS];
		_volumeCountsPerInterval = new double[StockUtils.TRADING_DAY_INTERVALS];
		_rvol = 0;
		_profitLossChange = 0;
		_companyShareFloat = 0;
	}
	
	public Contract getContract() {
		return _candleStickChart.getContract();
	}
	
	public String getSymbol() {
		return _candleStickChart.getSymbol();
	}
	
	public double getRelativeVolume() {
		return _rvol;
	}
	
	public double getProfitLossChange() {
		return _profitLossChange;
	}
	
	public long getCompanyShareFloat() {
		return _companyShareFloat;
	}
	
	public void setCompanyShareFloat(long companyShareFloat) {
		_companyShareFloat = companyShareFloat;
	}
	
	// call from within InteractiveBrokersApiRequestHandler::historicalData
	public void addCandleStick(CandleStick candleStick) {
		if (!StockUtils.isValidTradingTime(candleStick.getJodaDateTime().getMinuteOfDay())) {
			throw new IllegalArgumentException("Invalid argument: " + candleStick.getDateAsString());
		}
		_candleStickChart.addCandleStick(candleStick);
	}
	
	// call from within InteractiveBrokersApiRequestHandler::historicalDataEnd; executed once the last candle is received
	public void calculateRelativeTradingVolume() {
		
		for (CandleStick candleStick : _candleStickChart.getSeries()) {
			if (candleStick.getJodaDateTime().getDayOfMonth() ==  _candleStickChart.getSeries().getLast().getJodaDateTime().getDayOfMonth()) {
				continue;
			}
			
			_historicalVolumesByInterval[StockUtils.timeToIndex(candleStick.getJodaDateTime().getMinuteOfDay())] += Double.parseDouble(candleStick.getVolume().toString());
			_volumeCountsPerInterval[StockUtils.timeToIndex(candleStick.getJodaDateTime().getMinuteOfDay())]++;
		}
		_rvol = Double.parseDouble(_candleStickChart.getSeries().getLast().getVolume().toString()) / getAverageTradingVolumeForInterval(_candleStickChart.getSeries().getLast().getJodaDateTime());
	}
	

	private double getAverageTradingVolumeForInterval(DateTime date) {
		double historicalVolumesByInterval = _historicalVolumesByInterval[StockUtils.timeToIndex(date.getMinuteOfDay())];
		double volumeCountsByInterval = _volumeCountsPerInterval[StockUtils.timeToIndex(date.getMinuteOfDay())];
		
		if (historicalVolumesByInterval != 0 && volumeCountsByInterval != 0) {
			return historicalVolumesByInterval / volumeCountsByInterval;			
		}
		
		return -1.0;
	}
	
	
	public void calculateProfitLossChange() {
		
		double actualPrice = _candleStickChart.getSeries().getLast().getClose();
		double yesterdaysClosePrice = 0;
		
		// If today is Monday, subtract three days to get Friday, otherwise one day to get yesterday
		int subtrahend = _candleStickChart.getSeries().getLast().getJodaDateTime().getDayOfWeek() == 1 ? 3 : 1;
		
		for (CandleStick candleStick : _candleStickChart.getSeries()) {
			
			if (!isDateYesterday(candleStick.getJodaDateTime(), _candleStickChart.getSeries().getLast().getJodaDateTime(), subtrahend) ) {
				continue;
			}
			
			// Code will reach this point only for yesterday's (or, in case today is Monday, Friday's) entries
			if (candleStick.getJodaDateTime().getMinuteOfDay() == StockUtils.getMinuteOfLastEntry(5))	// 955 is 15:55 PM, which is the last entry for a trading day (5-Minute-Intervalls)
				yesterdaysClosePrice = candleStick.getClose();
		}
		
		_profitLossChange = ( (actualPrice - yesterdaysClosePrice) / yesterdaysClosePrice ) * 100; 
	}
	
	private boolean isDateYesterday(DateTime date, DateTime todaysDate, int subtrahend) {
		
		int lastTradingDay;	
		
		if (todaysDate.getDayOfMonth() == 1) {
			// subtract one month, get the last day of last month, subtract the subtrahend and add one day because getting the previous day already subtracts one day
			lastTradingDay = todaysDate.minusMonths(1).dayOfMonth().withMaximumValue().getDayOfMonth() - subtrahend + 1; 
		} else if (todaysDate.getDayOfMonth() == 2 && todaysDate.getDayOfWeek() == 1) {
			// will be the case if it is the 2nd and also Monday
			lastTradingDay = todaysDate.minusMonths(1).dayOfMonth().withMaximumValue().getDayOfMonth() - subtrahend + 2;
		}
		else {
			lastTradingDay = todaysDate.getDayOfMonth() - subtrahend;
		}
		
		return (date.getDayOfMonth() == lastTradingDay ); 
	}
}
