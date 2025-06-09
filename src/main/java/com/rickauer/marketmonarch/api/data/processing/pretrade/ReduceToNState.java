package com.rickauer.marketmonarch.api.data.processing.pretrade;

import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.ib.client.Bar;
import com.ib.client.Contract;
import com.ib.client.ContractDetails;
import com.rickauer.marketmonarch.constants.TradingConstants;

public class ReduceToNState extends PreTradeState {

	private static Logger _reduceResultLogger = LogManager.getLogger(ReduceToNState.class.getName());
	
	public ReduceToNState(PreTradeContext context) {
		super(context);
	}

	@Override
	public void onEnter() {
		_reduceResultLogger.info("Entered reduce to N state.");
		_reduceResultLogger.info("Reducing scan results from " + _context.getScanResult().size() + 
				" to " + TradingConstants.MAXIMUM_NUMBER_OF_SCAN_RESULTS + " entries to increase performance.");
		
		Map<Integer, Contract> firstN = _context.getScanResult().entrySet()
				.stream().limit(TradingConstants.MAXIMUM_NUMBER_OF_SCAN_RESULTS)
				.collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, TreeMap::new));
		
		_context.getScanResult().clear();
		_context.setScanResult(firstN);
		
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
