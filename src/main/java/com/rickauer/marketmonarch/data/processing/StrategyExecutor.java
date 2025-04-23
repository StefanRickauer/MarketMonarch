package com.rickauer.marketmonarch.data.processing;

import java.time.Duration;
import java.util.List;

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

import com.rickauer.marketmonarch.data.CandleStick;
import com.rickauer.marketmonarch.utils.StockUtils;

public class StrategyExecutor {

	private StrategyExecutor() {
		throw new UnsupportedOperationException(StrategyExecutor.class + " ist not meant to be instanciated.");
	}
	
	public static BarSeries buildSeriesFromCandleBars(List<CandleStick> candles) {
		BarSeries series = new BaseBarSeriesBuilder()
				.withName("stock_series")
				.withNumTypeOf(DecimalNum::valueOf)
				.build();
		
		for (CandleStick candle : candles) {
			Bar bar = new BaseBar(
					Duration.ofMillis(5),
					candle.getZonedDateTime(),
					DecimalNum.valueOf(candle.getOpen()),
					DecimalNum.valueOf(candle.getHigh()),
					DecimalNum.valueOf(candle.getLow()),
					DecimalNum.valueOf(candle.getClose()),
					DecimalNum.valueOf(candle.getVolumeAsDouble()),
					DecimalNum.valueOf(0)
					);
			
			series.addBar(bar);
		}
		
		return series;
	}
	
	public static Strategy buildStrategy(BarSeries series) {
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
	
	; // einbauen, wie bei shouldEnter weitergemacht wird (prüfen, welche Datentypen bei Orders mitgegeben werden müssen)
	public static void isEntry(BarSeries series) {
		Strategy strategy = buildStrategy(series);
		ClosePriceIndicator closePrice = new ClosePriceIndicator(series);
		int lastIndex = series.getEndIndex();
		double exit = StockUtils.calculateStopLoss(series, 720);
		
		if (strategy.shouldEnter(lastIndex)) {
			Num price = closePrice.getValue(lastIndex);
//			price.doubleValue();
		}
	}
}
