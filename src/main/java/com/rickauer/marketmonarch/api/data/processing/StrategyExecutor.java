package com.rickauer.marketmonarch.api.data.processing;

import java.time.ZonedDateTime;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.ta4j.core.Bar;
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

import com.rickauer.marketmonarch.constants.TradingConstants;
import com.rickauer.marketmonarch.utils.StockUtils;

public class StrategyExecutor {

	private static Logger _strategyLogger = LogManager.getLogger(StrategyExecutor.class.getName());
	private static ZonedDateTime _lastLoggedTime = null;
	
	String _timeStampZoneId;
	String _symbol;
	BarSeries _series;
	Strategy _strategy;
	double _bufferedEntryPrice;
	boolean _shouldEnter;
	
	public StrategyExecutor(String symbol) {
		_symbol = symbol;
		_series = new BaseBarSeriesBuilder()
				.withName(symbol)
				.withNumTypeOf(DecimalNum::valueOf)
				.build();
		_strategy = buildStrategy(_series);
		_shouldEnter = false;
	}
	
	private final Strategy buildStrategy(BarSeries series) {
		ClosePriceIndicator closePrice = new ClosePriceIndicator(series);
		VolumeIndicator volume = new VolumeIndicator(series);
		SMAIndicator sma60 = new SMAIndicator(closePrice, 60);
		SMAIndicator sma240 = new SMAIndicator(closePrice, 240);
		SMAIndicator sma600 = new SMAIndicator(closePrice, 600);
		RSIIndicator rsi = new RSIIndicator(closePrice, 168);
		SMAIndicator avgVolume = new SMAIndicator(volume, 360);
		SMAIndicator shortSma = new SMAIndicator(closePrice, 3);
		SMAIndicator longSma = new SMAIndicator(closePrice, 6);
		
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
				return series.numOf(number);
			}
		};
		
		Rule trendRule = new OverIndicatorRule(sma60, sma240).and(new OverIndicatorRule(sma240, sma600));
		Rule rsiRule = new OverIndicatorRule(rsi, series.numOf(50));
		Rule volumeSpikeRule = new OverIndicatorRule(volume, volumeThreshold);
		
		Rule phase1Rule = trendRule.and(rsiRule).and(volumeSpikeRule);
		Rule phase2Rule = new OverIndicatorRule(shortSma, longSma);
		
		Rule stagedEntryRule = new StagedEntryRule(phase1Rule, phase2Rule);	
		Rule dummyExitRule = new StopLossRule(closePrice, series.numOf(20));		
		
		return new BaseStrategy("IntradayBreakout-Staged", stagedEntryRule, dummyExitRule);
	}
	
	public void onHistoricalBar(Bar bar) {
		_series.addBar(bar);
	}
	
	public synchronized void onNewBar(Bar bar) {
		
		ZonedDateTime newEndTime = bar.getEndTime();
		ZonedDateTime lastEndTime = _series.getLastBar().getEndTime();
		
		if (!newEndTime.isAfter(lastEndTime)) {
			return;
		}
		
		_series.addBar(bar);
		int lastIndex = _series.getEndIndex();
		
		ClosePriceIndicator closePrice = new ClosePriceIndicator(_series);

		if (_strategy.shouldEnter(lastIndex)) {
			Num close = closePrice.getValue(lastIndex);
			_bufferedEntryPrice = StockUtils.calculateTargetPrice(close.doubleValue(), TradingConstants.BUY_LIMIT_PUFFER);
			_shouldEnter = true;
		}
		
		if ( (!newEndTime.equals(_lastLoggedTime)) && (newEndTime.getMinute() % 2 == 0) && (newEndTime.getSecond() == 0) ) {
			_lastLoggedTime = newEndTime;
			_strategyLogger.info("Connectivity check: Market data stream confirmed active. Next status log in 2 minutes.");
		}
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
	
	public double getBufferedEntryPrice() {
		return _bufferedEntryPrice;
	}
	
	public boolean getShouldEnter() {
		return _shouldEnter;
	}
}
