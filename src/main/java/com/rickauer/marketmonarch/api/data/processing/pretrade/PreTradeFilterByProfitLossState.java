package com.rickauer.marketmonarch.api.data.processing.pretrade;

import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.ib.client.Bar;
import com.ib.client.ContractDetails;
import com.rickauer.marketmonarch.MarketMonarch;
import com.rickauer.marketmonarch.api.data.StockMetrics;
import com.rickauer.marketmonarch.constants.TradingConstants;

public class PreTradeFilterByProfitLossState extends PreTradeState {

	private static Logger _filterByProfitLossLogger = LogManager.getLogger(PreTradeFilterByProfitLossState.class.getName());

	public PreTradeFilterByProfitLossState(PreTradeContext context) {
		super(context);
	}

	@Override
	public void onEnter() {
		_filterByProfitLossLogger.info("Entered filter by P&L state.");
		
		_filterByProfitLossLogger.info("Filtering stocks by profit and loss (P&L)...");

		int numberOfStocksBeforeFiltering = _context.getScanResult().size();
		
		_context.getHistoricalData().entrySet()
				.removeIf(entry -> Math.floor(entry.getValue().getProfitLossChange()) < TradingConstants.MINIMUM_PROFIT_LOSS_IN_PERCENT);

		_filterByProfitLossLogger.info("Done filtering stocks by profit and loss (P&L) and relative trading volume. Removed "
				+ (numberOfStocksBeforeFiltering - _context.getHistoricalData().size()) + " entries. Changing state.");

		if (_context.getHistoricalData().isEmpty()) {
			_filterByProfitLossLogger.info("No eligible stocks remaining. Restarting pre-trading phase in 15 minutes.");
			
			try {
				Thread.sleep(TradingConstants.FIFTEEN_MINUTES_TIMEOUT_MS);
			} catch (InterruptedException e) {
				throw new RuntimeException("Error during wait.");
			}
			
			_context.setState(new PreTradeAccountValidationState(_context));
		} else {
			_filterByProfitLossLogger.info("Number of elibible stocks: " + _context.getHistoricalData().size() + ". Pre-trading phase finished.");
			_context.setState(new PreTradeInactiveState(_context));
		}
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
		// intentionally left blank
	}

	@Override
	public void processHistoricalDataEnd(int reqId, String startDateStr, String endDateStr) {
		// intentionally left blank
	}
}
