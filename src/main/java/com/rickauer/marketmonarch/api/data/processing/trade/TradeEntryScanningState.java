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
import com.rickauer.marketmonarch.constants.TradingConstants;

public class TradeEntryScanningState extends TradeMonitorState {

	private static Logger _entryScanLogger = LogManager.getLogger(TradeEntryScanningState.class.getName());

	Map<String, Contract> _stockWatchlist;

	public TradeEntryScanningState(TradeMonitorContext context) {
		super(context);
		_stockWatchlist = initializeStockWatchlist();
	}

	@Override
	public void onEnter() {
		_entryScanLogger.info("Entered trading phase.");
		_entryScanLogger.info("Entered trade scanning state.");

		List<String> watchlistKeys = new ArrayList<>(_stockWatchlist.keySet());
		for (int i = 0; i < watchlistKeys.size(); i++) {

			int requestId = 0;

			synchronized (_context.getHistoricalData()) {
				try {
					String symbol = watchlistKeys.get(i);
					requestId = _context.getController().getNextRequestId();
					BarSeries series = new BaseBarSeriesBuilder().withName(symbol)
							.withNumTypeOf(DecimalNum::valueOf).build();
					_context.getHistoricalData().put(requestId, series);
					_context.getController().getSocket().reqHistoricalData(
							requestId, 
							_stockWatchlist.get(symbol),
							TradingConstants.END_DATE_TIME_UNTIL_NOW, 
							TradingConstants.LOOKBACK_PERIOD_FOUR_HOURS_TEN_MINUTES_IN_SECONDS,
							TradingConstants.BARSIZE_SETTING_FIVE_SECONDS, 
							TradingConstants.WHAT_TO_SHOW,
							TradingConstants.USE_REGULAR_TRADING_HOUR_DATA, 
							TradingConstants.FORMAT_DATE,
							TradingConstants.KEEP_UP_TO_DATE, 
							null
							);
					_context.getHistoricalData().wait(TradingConstants.FIVE_MINUTES_TIMEOUT_MS);

					if (_hasReceivedApiResponse == true) {
						System.out.println("==================================================================================================");
						System.out.println("==================================================================================================");
						System.out.println("==================================================================================================");
						System.out.println("==================================================================================================");
						System.out.println("==================================================================================================");
						System.out.println("==================================================================================================");
						System.out.println("==================================================================================================");
						_entryScanLogger.info("Received resoponse for symbol: " + symbol);
						_hasReceivedApiResponse = false;
					} else {
						_entryScanLogger
								.warn("Did not receive response for symbol: " + symbol + ". Repeating request.");
						i--;
					}

				} catch (InterruptedException e) {
					throw new RuntimeException("Error fetching data.", e);
				}
			}
		}
		
		
		; // Anschließend Live-Daten abfragen und analysieren
		; // prüfen, ob für die Abfrage der historischen Daten eine separate Datenstruktur verwendet werden soll, sodass sich historische Daten und
		; // Live-Daten nicht gegenseitig blockieren
		
		
		_context.setState(new TradeInactiveState(_context));
	}

	@Override
	public void processOrderData(String msg, String status, Decimal filled, Decimal remaining, double avgFillPrice) {
		// TODO Auto-generated method stub

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
		if (_context.getHistoricalData().get(reqId) != null) {
			Bar baseBar = new BaseBar(
					Duration.ofMillis(5), 
					time, 
					DecimalNum.valueOf(open), 
					DecimalNum.valueOf(high),
					DecimalNum.valueOf(low), 
					DecimalNum.valueOf(close), 
					DecimalNum.valueOf(volume),
					DecimalNum.valueOf(0));
			_context.getHistoricalData().get(reqId).addBar(baseBar);
		}

	}

	@Override
	public void processHistoricalDataEnd(int reqId, String startDateStr, String endDateStr) {
		System.out.println("==");
		if (_context.getHistoricalData().get(reqId) != null) {

			synchronized (_context.getHistoricalData()) {
				_hasReceivedApiResponse = true;
				_context.getHistoricalData().notify();
			}
		}
	}
}
