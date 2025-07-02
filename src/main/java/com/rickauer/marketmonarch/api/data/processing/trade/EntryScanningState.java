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
import org.ta4j.core.BaseBar;
import org.ta4j.core.num.DecimalNum;

import com.ib.client.Contract;
import com.ib.client.Decimal;
import com.ib.client.Order;
import com.ib.client.OrderState;
import com.rickauer.marketmonarch.MarketMonarch;
import com.rickauer.marketmonarch.api.data.processing.StrategyExecutor;
import com.rickauer.marketmonarch.constants.TradingConstants;

public class EntryScanningState extends TradeState {

	private static Logger _entryScanLogger = LogManager.getLogger(EntryScanningState.class.getName());

	Object _lockHistoricalData;
	Object _lockLiveData;
	Map<String, Contract> _stockWatchlist;
	volatile boolean _foundEntry;
	volatile boolean _timeoutReached;
	
	public EntryScanningState(TradeContext context) {
		super(context);
		_lockHistoricalData = new Object();
		_lockLiveData = new Object();
		_stockWatchlist = initializeStockWatchlist();
		_foundEntry = false;
		_timeoutReached = true;
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
					_lockHistoricalData.wait(TradingConstants.ONE_MINUTE_TIMEOUT_MS);

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
		
		_entryScanLogger.warn("Note: Please consider the trading hours of the respective exchange.");
		_entryScanLogger.info("Waiting for scanner to detect entry. Logging connectivity check every two minutes!");
		
		// only true if entry found during historical data request, if no entry is found program will start new
		if (_foundEntry == false) {			
			synchronized (_lockLiveData) {
				try {
					_lockLiveData.wait(TradingConstants.SCAN_INTERVAL_THREE_MINUTES_IN_MS);
				} catch (InterruptedException e) {
					_entryScanLogger.error("Error waiting for notification.");
				}
			}
		}
		
		cancelLiveFeeds();
		
		if (_foundEntry) {
			_foundEntry = false;
			_timeoutReached = false;
			_entryScanLogger.info("Changing state.");
			_context.setState(new BuyProcessingState(_context));
		} 
		
		if (_timeoutReached) {			
			_entryScanLogger.info("Timeout reached. No entry found.");
			_entryScanLogger.info("Restarting pre trade phase");
		}
	}

	@Override
	public void processOrderStatus(String msg, int orderId, String status, Decimal filled, Decimal remaining, double avgFillPrice) {
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
			
			_context.getStockAnalysisManager().handleNewBar(reqId, baseBar); 
			
			if (_context.getStockAnalysisManager().getExecutorBySymbol(symbol).getShouldEnter() && !_foundEntry) {
				_foundEntry = true;
				_entryScanLogger.info("Found entry for symbol: " + _context.getStockAnalysisManager().getSymbolById(reqId) + ".");
				
				_entryScanLogger.info("Updating trading context.");
				_context.setEntryPrice(_context.getStockAnalysisManager().getExecutorBySymbol(symbol).getBufferedEntryPrice());
				_context.setContract(_stockWatchlist.get(symbol)); 
				_context.setEntryTime(baseBar.getEndTime());
				_context.setRestartSession(false);
				synchronized (_lockLiveData) {
					_lockLiveData.notify();
				}
			}
		}
	}
	
	private void cancelLiveFeeds() {
		_entryScanLogger.info("Canceling live feeds.");
		_context.getStockAnalysisManager().getSymbolLookupTable().entrySet().stream().forEach(entry -> _context.getController().getSocket().cancelRealTimeBars(entry.getKey()));
	}

	@Override
	public void processOpenOrder(String msg, int orderId, Contract contract, Order order, OrderState orderState) {
		// intentionally left blank 
	}
	
}
