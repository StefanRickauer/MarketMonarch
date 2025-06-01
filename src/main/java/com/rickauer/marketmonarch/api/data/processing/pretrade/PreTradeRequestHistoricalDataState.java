package com.rickauer.marketmonarch.api.data.processing.pretrade;

import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.ib.client.Bar;
import com.ib.client.Contract;
import com.ib.client.ContractDetails;
import com.rickauer.marketmonarch.MarketMonarch;
import com.rickauer.marketmonarch.api.data.CandleStick;
import com.rickauer.marketmonarch.api.data.StockMetrics;
import com.rickauer.marketmonarch.constants.TradingConstants;

public class PreTradeRequestHistoricalDataState extends PreTradeState {

	private static Logger _reqHistDataLogger = LogManager.getLogger(PreTradeRequestHistoricalDataState.class.getName());
	
	public PreTradeRequestHistoricalDataState(PreTradeContext context) {
		super(context);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void onEnter() {
		
		_reqHistDataLogger.info("Entered Request Historical Data State (pre trade).");
		
		_reqHistDataLogger.info("Filtering stocks by profit and loss (P&L) and relative trading volume...");
		
		int numberOfStocksBeforeFiltering = _context.getScanResult().size();
		
		for (Map.Entry<Integer, Contract> entry : _context.getScanResult().entrySet()) {

			int requestId = 0;

			synchronized (_context.getHistoricalData()) {
				try {
					requestId = _context.getIbController().getNextRequestId();
					_context.getHistoricalData().put(requestId, new StockMetrics(entry.getValue()));
					
					_context.getIbController().getSocket().reqHistoricalData(requestId, entry.getValue(), TradingConstants.END_DATE_TIME_UNTIL_NOW, TradingConstants.LOOKBACK_PERIOD_TWO_DAYS, TradingConstants.BARSIZE_SETTING_FIVE_MINUTES, TradingConstants.WHAT_TO_SHOW, TradingConstants.USE_REGULAR_TRADING_HOUR_DATA, TradingConstants.FORMAT_DATE,
							TradingConstants.KEEP_UP_TO_DATE, null);
					_context.getHistoricalData().wait();
					_reqHistDataLogger.info(_context.getHistoricalData().get(requestId).getSymbol() + ": P&L = "
							+ _context.getHistoricalData().get(requestId).getProfitLossChange() + ", RVOL = "
							+ _context.getHistoricalData().get(requestId).getRelativeVolume());
				} catch (InterruptedException e) {
					throw new RuntimeException("Error fetching data.", e);
				}
			}
		}
		
		_context.getHistoricalData().entrySet().removeIf(entry -> Math.floor(entry.getValue().getProfitLossChange()) < 10);
		
		for (Map.Entry<Integer, StockMetrics> filteredResult : _context.getHistoricalData().entrySet()) {
			MarketMonarch._contractsToObserve.add(filteredResult.getValue().getContract());
		}
		
		_reqHistDataLogger.info("Done filtering stocks by profit and loss (P&L) and relative trading volume. Removed " + (numberOfStocksBeforeFiltering - _context.getHistoricalData().size()) + " entries.");
		
		
	}

	@Override
	public void processAccountSummary(String logMessage, int reqId, String account, String tag, String value,
			String currency) {
		// intentionally left blank
	}

	@Override
	public void processAccountSummaryEnd(int reqId) {
		// intentionally left blank
	}

	@Override
	public void processScannerData(int reqId, int rank, ContractDetails contractDetails, String distance,
			String benchmark, String projection, String legsStr) {
		// intentionally left blank
	}

	@Override
	public void processDataEnd(int reqId) {
		// intentionally left blank
	}

	@Override
	public void processHistoricalData(int reqId, Bar bar) {
		if (_context.getHistoricalData().get(reqId) != null) {
			_context.getHistoricalData().get(reqId).addCandleStick(new CandleStick(bar.time(), bar.open(), bar.close(), bar.high(), bar.low(), bar.volume()));			
		}
	}

	@Override
	public void processHistoricalDataEnd(int reqId, String startDateStr, String endDateStr) {
		if (_context.getHistoricalData().get(reqId) != null) {
			_reqHistDataLogger.info("Gathered historical data for Symbol: '" + _context.getHistoricalData().get(reqId).getSymbol() + "', Request-ID: " + reqId + ".");

			synchronized(_context.getHistoricalData()) {
				_context.getHistoricalData().get(reqId).calculateProfitLossChange();
				_context.getHistoricalData().get(reqId).calculateRelativeTradingVolume();
				_context.getHistoricalData().notify();			
			}
		}
		
	}

}
