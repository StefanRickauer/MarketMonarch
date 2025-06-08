package com.rickauer.marketmonarch.api.data.processing.trade;

import java.time.Duration;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.ta4j.core.Bar;
import org.ta4j.core.BarSeries;
import org.ta4j.core.BaseBar;
import org.ta4j.core.BaseBarSeriesBuilder;
import org.ta4j.core.num.DecimalNum;

import com.ib.client.Contract;
import com.ib.client.Decimal;
import com.rickauer.marketmonarch.MarketMonarch;
import com.rickauer.marketmonarch.api.data.CandleStick;
import com.rickauer.marketmonarch.api.data.StockMetrics;
import com.rickauer.marketmonarch.api.data.processing.StrategyExecutor;
import com.rickauer.marketmonarch.api.data.processing.pretrade.PreTradeAccountValidationState;
import com.rickauer.marketmonarch.constants.TradingConstants;

public class TradeEntryScanningState extends TradeMonitorState {

	private static Logger _entryScanLogger = LogManager.getLogger(TradeEntryScanningState.class.getName());

	Object _lockHistoricalData;
	Object _lockLiveData;
	Map<String, Contract> _stockWatchlist;
	volatile boolean _foundEntry;
	
	public TradeEntryScanningState(TradeMonitorContext context) {
		super(context);
		_lockHistoricalData = new Object();
		_lockLiveData = new Object();
		_stockWatchlist = initializeStockWatchlist();
		_foundEntry = false;
	}

	@Override
	public void onEnter() {
		_entryScanLogger.info("Entered trading phase.");
		_entryScanLogger.info("Entered trade scanning state.");

		List<String> watchlistKeys = new ArrayList<>(_stockWatchlist.keySet());
		for (int i = 0; i < watchlistKeys.size(); i++) {

			int requestId = 0;

			synchronized (_lockHistoricalData) {
				try {
					String symbol = watchlistKeys.get(i);
					requestId = _context.getController().getNextRequestId();

					_context.getStockAnalysisManager().updateSymbolLookupTable(requestId, symbol);
					_context.getStockAnalysisManager().getExecutors().put(symbol, new StrategyExecutor(symbol));
					_context.getController().getSocket().reqHistoricalData(
							requestId, 
							_stockWatchlist.get(symbol),
							TradingConstants.END_DATE_TIME_UNTIL_NOW, 
							TradingConstants.LOOKBACK_PERIOD_TWO_HOURS_FIVE_MINUTES_IN_SECONDS,
							TradingConstants.BARSIZE_SETTING_FIVE_SECONDS, 
							TradingConstants.SHOW_TRADES,
							TradingConstants.USE_REGULAR_TRADING_HOUR_DATA_INTEGER, 
							TradingConstants.FORMAT_DATE,
							TradingConstants.KEEP_UP_TO_DATE, 
							null
							);
					_lockHistoricalData.wait(TradingConstants.FIVE_MINUTES_TIMEOUT_MS);

					if (_foundEntry) {
						break;
					}
					
					if (_hasReceivedApiResponse == true) {
						_entryScanLogger.info("Received resoponse for symbol: " + symbol);
						requestId = _context.getController().getNextRequestId();
						_context.getStockAnalysisManager().updateSymbolLookupTable(requestId, symbol);
						_context.getController().getSocket().reqRealTimeBars(
								requestId, 
								_stockWatchlist.get(symbol),
								5, 
								TradingConstants.SHOW_TRADES, 
								TradingConstants.USE_REGULAR_TRADING_HOUR_DATA_BOOLEAN, 
								null
								);
						_hasReceivedApiResponse = false;
						_entryScanLogger.info("Requested live feed for symbol: " + symbol);
					} else {
						_entryScanLogger.warn("Did not receive response for symbol: " + symbol + ". Repeating request.");
						i--;
					}

				} catch (InterruptedException e) {
					throw new RuntimeException("Error fetching data.", e);
				}
			}
		}
		
		// only true if entry found during historical data request
		if (_foundEntry == false) {			
			synchronized (_lockLiveData) {
				try {
					_lockLiveData.wait(TradingConstants.TWO_HOURS_TIMEOUT_MS);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
		
		if (_foundEntry) {
			_context.setState(new TradeBuyProcessingState(_context));
		}
		_entryScanLogger.info("Timeout reached. No entry found. Restarting pre trade phase.");
	}

	@Override
	public void processOrderData(String msg, String status, Decimal filled, Decimal remaining, double avgFillPrice) {
		// intentionally left blank 
	}

	private Map<String, Contract> initializeStockWatchlist() {
		Map<String, Contract> watchlist = new HashMap<>();

		for (Map.Entry<Integer, Contract> entry : MarketMonarch._preTradeContext.getScanResult().entrySet()) {
			watchlist.put(entry.getValue().symbol(), entry.getValue());
		}
		return watchlist;
	}

	@Override
	public void processHistoricalData(int reqId, ZonedDateTime time, double open, double high, double low, double close,
			double volume) {
		String symbol = _context.getStockAnalysisManager().getSymbolById(reqId);
		if (_context.getStockAnalysisManager().getExecutorBySymbol(symbol) != null) {
			Bar baseBar = new BaseBar(
					Duration.ofMillis(5), 
					time, 
					DecimalNum.valueOf(open), 
					DecimalNum.valueOf(high),
					DecimalNum.valueOf(low), 
					DecimalNum.valueOf(close), 
					DecimalNum.valueOf(volume),
					DecimalNum.valueOf(0));
			_context.getStockAnalysisManager().handleHistoricalBar(reqId, baseBar);
		}
	}

	@Override
	public void processHistoricalDataEnd(int reqId, String startDateStr, String endDateStr) {

		String symbol = _context.getStockAnalysisManager().getSymbolById(reqId);
		
		if (_context.getStockAnalysisManager().getExecutorBySymbol(symbol) != null) {
			
			synchronized (_lockHistoricalData) {
				_context.getStockAnalysisManager().getExecutorBySymbol(symbol).setZoneId(endDateStr);
				_hasReceivedApiResponse = true;
				_lockHistoricalData.notify();
			}
		}
	}

	@Override
	public void processRealtimeBar(int reqId, ZonedDateTime time, double open, double high, double low, double close, Decimal volume, Decimal wap, int count) {
		
		String symbol = _context.getStockAnalysisManager().getSymbolById(reqId);
		
		if (_context.getStockAnalysisManager().getExecutorBySymbol(symbol) != null) {
			
			double vol = Double.parseDouble(volume.toString());
			
			Bar baseBar = new BaseBar(
					Duration.ofMillis(5),
					time,
					DecimalNum.valueOf(open),
					DecimalNum.valueOf(high),
					DecimalNum.valueOf(low),
					DecimalNum.valueOf(close),
					DecimalNum.valueOf(vol),
					DecimalNum.valueOf(0));
			; // Daten wie Einstiegspreis, StopLoss usw. noch speichern!
			boolean _foundEntry = _context.getStockAnalysisManager().handleNewBar(reqId, baseBar); 
			
			if (_foundEntry) {
				
				_entryScanLogger.info("Found entry for symbol: " + _context.getStockAnalysisManager().getSymbolById(reqId) + ". Canceling live feeds.");
				_context.getStockAnalysisManager().getSymbolLookupTable().entrySet().stream().forEach(entry -> _context.getController().getSocket().cancelRealTimeBars(entry.getKey()));
				_context.setRestartSession(false);
				
				synchronized (_lockLiveData) {
					_lockLiveData.notify();
				}
			}
		}
	}
	
}
