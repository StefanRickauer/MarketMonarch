package com.rickauer.marketmonarch.api.data.processing;

import java.time.Duration;
import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.ta4j.core.Bar;
import org.ta4j.core.BaseBar;
import org.ta4j.core.BarSeries;
import org.ta4j.core.BaseBarSeriesBuilder;
import org.ta4j.core.BaseStrategy;
import org.ta4j.core.Indicator;
import org.ta4j.core.Rule;
import org.ta4j.core.Strategy;
import org.ta4j.core.indicators.RSIIndicator;
import org.ta4j.core.indicators.SMAIndicator;
import org.ta4j.core.indicators.helpers.ClosePriceIndicator;
import org.ta4j.core.indicators.helpers.VolumeIndicator;
import org.ta4j.core.num.DecimalNum;
import org.ta4j.core.num.Num;
import org.ta4j.core.rules.OverIndicatorRule;
import org.ta4j.core.rules.StopLossRule;
import org.ta4j.core.rules.UnderIndicatorRule;

import com.rickauer.marketmonarch.api.data.CandleStick;
import com.rickauer.marketmonarch.utils.StockUtils;

public class StrategyExecutor {

	String _timeStampZoneId;
	String _symbol;
	BarSeries _series;
	Strategy _strategy;
	double _entryPrice;
	
	public StrategyExecutor(String symbol) {
		_symbol = symbol;
		_series = new BaseBarSeriesBuilder()
				.withName(symbol)
				.withNumTypeOf(DecimalNum::valueOf)
				.build();
		_strategy = buildStrategy(_series);
	}
	
	private final Strategy buildStrategy(BarSeries series) {
		ClosePriceIndicator closePrice = new ClosePriceIndicator(series);
		VolumeIndicator volume = new VolumeIndicator(series);
		SMAIndicator sma20 = new SMAIndicator(closePrice, 20);
		SMAIndicator sma50 = new SMAIndicator(closePrice, 50);
		SMAIndicator sma200 =new SMAIndicator(closePrice, 200);
		RSIIndicator rsi = new RSIIndicator(closePrice, 14);
		SMAIndicator avgVolume = new SMAIndicator(volume, 30);
		
		Indicator<Num> volumeThreshold = new Indicator<>() {
			@Override
			public Num getValue(int index) {
				return avgVolume.getValue(index).multipliedBy(series.numOf(1.5));
			}
			
			@Override
			public BarSeries getBarSeries() {
				return series;
			}
			
			@Override
			public Num numOf(Number number) {
				return null;
			}
		};
		
		Rule trendRule = new OverIndicatorRule(sma50, sma200);
		Rule pullbackRule = new UnderIndicatorRule(closePrice, sma20);
		Rule rsiRule = new OverIndicatorRule(rsi, series.numOf(50));
		Rule volumeSpikeRule = new OverIndicatorRule(volume, volumeThreshold);
		
		Rule entryRule = trendRule.and(pullbackRule).and(rsiRule).and(volumeSpikeRule);
		Rule exitRule = new StopLossRule(closePrice, series.numOf(20));		
		
		return new BaseStrategy("AlphaEntry", entryRule, exitRule);
	}
	
	public void onHistoricalBar(Bar bar) {
		_series.addBar(bar);
	}
	
	public synchronized boolean onNewBar(Bar bar) {
		boolean shouldEnter = false;
		
		ZonedDateTime newEndTime = bar.getEndTime();
		ZonedDateTime lastEndTime = _series.getLastBar().getEndTime();
		
		if (!newEndTime.isAfter(lastEndTime)) {
			return shouldEnter;
		}
		
		_series.addBar(bar);
		int lastIndex = _series.getEndIndex();
		
		ClosePriceIndicator closePrice = new ClosePriceIndicator(_series);

		if (_strategy.shouldEnter(lastIndex)) {
			Num close = closePrice.getValue(lastIndex);
			_entryPrice = close.doubleValue();
			shouldEnter = true;
		}
		return shouldEnter;
	}
	
	public String getSymbol() {
		return _symbol;
	}
	
	public BarSeries getSeries() {
		return _series;
	}
	
	public void setZoneId(String time) {
		int index = time.lastIndexOf(' ');
		String zone = time.substring(index + 1); 

		_timeStampZoneId = zone;
	}
	
	public String getZoneId() {
		return _timeStampZoneId;
	}
	
	public double getEntryPrice() {
		return _entryPrice;
	}
}
