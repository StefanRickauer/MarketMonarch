package com.rickauer.marketmonarch.api.data.processing.pretrade;

import java.util.Map;

import com.ib.client.Bar;
import com.ib.client.ContractDetails;
import com.rickauer.marketmonarch.MarketMonarch;
import com.rickauer.marketmonarch.api.data.StockMetrics;

public class PreTradeGenerateCandiatesToObserveState extends PreTradeState {

	public PreTradeGenerateCandiatesToObserveState(PreTradeContext context) {
		super(context);
	}

	@Override
	public void onEnter() {
		
		for (Map.Entry<Integer, StockMetrics> filteredResult : _context.getHistoricalData().entrySet()) {
			MarketMonarch._contractsToObserve.add(filteredResult.getValue().getContract());
		}
		
		_context.setState(new PreTradeInactiveState(_context));
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
