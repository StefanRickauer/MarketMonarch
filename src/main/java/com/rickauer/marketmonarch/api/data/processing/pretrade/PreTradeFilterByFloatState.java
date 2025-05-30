package com.rickauer.marketmonarch.api.data.processing.pretrade;

import java.util.HashMap;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.ib.client.Contract;
import com.ib.client.ContractDetails;
import com.rickauer.marketmonarch.MarketMonarch;

public class PreTradeFilterByFloatState extends PreTradeState {

	private static Logger _filterByFloatLogger = LogManager.getLogger(PreTradeFilterByFloatState.class.getName());
	
	public PreTradeFilterByFloatState(PreTradeContext context) {
		super(context);
	}

	@Override
	public void onEnter() {

		System.out.println("Entered Filter By Float State.");

		_filterByFloatLogger.info("Filtering scan results by company share float...");

		Map<String, Long> scanResultCompanyFloat = new HashMap<>();

		long floatShares = 0L;
		int numberOfStocksBeforeFiltering = _context.getScanResult().size();
		int failedSearchesCount = 0;

		for (Map.Entry<Integer, Contract> entry : _context.getScanResult().entrySet()) {

			String currentSymbol = entry.getValue().symbol();

			try {
				floatShares = _context.getAllCompanyFloats().get(currentSymbol.replace(" ", "-")); // The symbol
																											// "GTN A"
																											// wasn't
																											// found but
																											// list
																											// contains
																											// "GTN-A".
			} catch (NullPointerException e) {
				floatShares = -1L;
				_filterByFloatLogger.warn("Did not find company share float for symbol: '" + currentSymbol + "'.");
				failedSearchesCount++;
			}

			scanResultCompanyFloat.put(currentSymbol, floatShares);
		}

		_context.getScanResult().entrySet()
				.removeIf(entry -> scanResultCompanyFloat.get(entry.getValue().symbol()) > MarketMonarch.MAX_NUMBER_OF_SHARES
						|| scanResultCompanyFloat.get(entry.getValue().symbol()) < MarketMonarch.MIN_NUMBER_OF_SHARES);

		_filterByFloatLogger.info("Done filtering scan results by company share float. Removed "
				+ (numberOfStocksBeforeFiltering - _context.getScanResult().size()) + " out of "
				+ numberOfStocksBeforeFiltering + " entries. Failed searches in totoal: " + failedSearchesCount);

		synchronized (_context) {
			_context.notify();
		}

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

}
