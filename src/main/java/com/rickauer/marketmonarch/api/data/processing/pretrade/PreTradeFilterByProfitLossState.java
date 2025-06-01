package com.rickauer.marketmonarch.api.data.processing.pretrade;

import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.ib.client.Bar;
import com.ib.client.ContractDetails;
import com.rickauer.marketmonarch.MarketMonarch;
import com.rickauer.marketmonarch.api.data.StockMetrics;

public class PreTradeFilterByProfitLossState extends PreTradeState {

	private static Logger _filterByProfitLossLogger = LogManager
			.getLogger(PreTradeFilterByProfitLossState.class.getName());

	public PreTradeFilterByProfitLossState(PreTradeContext context) {
		super(context);
	}

	@Override
	public void onEnter() {
		_filterByProfitLossLogger.info("Entered filter by P&L state.");
		
		_filterByProfitLossLogger.info("Filtering stocks by profit and loss (P&L)...");

		int numberOfStocksBeforeFiltering = _context.getScanResult().size();
		
		_context.getHistoricalData().entrySet()
				.removeIf(entry -> Math.floor(entry.getValue().getProfitLossChange()) < 10);

		for (Map.Entry<Integer, StockMetrics> filteredResult : _context.getHistoricalData().entrySet()) {
			MarketMonarch._contractsToObserve.add(filteredResult.getValue().getContract());
		}

		_filterByProfitLossLogger.info("Done filtering stocks by profit and loss (P&L) and relative trading volume. Removed "
				+ (numberOfStocksBeforeFiltering - _context.getHistoricalData().size()) + " entries.");

		_context.setState(new PreTradeInactiveState(_context));
	}

	@Override
	public void processAccountSummary(String logMessage, int reqId, String account, String tag, String value,
			String currency) {
		// TODO Auto-generated method stub

	}

	@Override
	public void processAccountSummaryEnd(int reqId) {
		// TODO Auto-generated method stub

	}

	@Override
	public void processScannerData(int reqId, int rank, ContractDetails contractDetails, String distance,
			String benchmark, String projection, String legsStr) {
		// TODO Auto-generated method stub

	}

	@Override
	public void processDataEnd(int reqId) {
		// TODO Auto-generated method stub

	}

	@Override
	public void processHistoricalData(int reqId, Bar bar) {
		// TODO Auto-generated method stub

	}

	@Override
	public void processHistoricalDataEnd(int reqId, String startDateStr, String endDateStr) {
		// TODO Auto-generated method stub

	}

}
