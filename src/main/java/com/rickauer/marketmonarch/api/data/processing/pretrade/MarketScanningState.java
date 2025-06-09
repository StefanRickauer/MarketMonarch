package com.rickauer.marketmonarch.api.data.processing.pretrade;

import java.util.LinkedList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.ib.client.Bar;
import com.ib.client.ContractDetails;
import com.ib.client.ScannerSubscription;
import com.ib.client.TagValue;
import com.rickauer.marketmonarch.MarketMonarch;
import com.rickauer.marketmonarch.constants.TradingConstants;

public class MarketScanningState extends PreTradeState {

	private static Logger _marketScanningLogger = LogManager.getLogger(MarketScanningState.class.getName());
	
	public MarketScanningState(PreTradeContext context) {
		super(context);
	}

	@Override
	public void onEnter() {
		_marketScanningLogger.info("Entered Market Scanning State.");
		_marketScanningLogger.info("Setting up market scanner subscription and requesting scan results...");
		
		int requestId = _context.getIbController().getNextRequestId();

		ScannerSubscription subscription = new ScannerSubscription();
		subscription.instrument(TradingConstants.SCANNER_INSTRUMENT);
		subscription.locationCode(TradingConstants.SCANNER_LOCATION_CODE);
		subscription.scanCode(TradingConstants.SCANNER_SCAN_CODE);
		
		List<TagValue> filterTagValues = new LinkedList<>();
		filterTagValues.add(new TagValue(TradingConstants.FILTER_TAG_PRICE_ABOVE, TradingConstants.FILTER_VALUE_PRICE_ABOVE));
		filterTagValues.add(new TagValue(TradingConstants.FILTER_TAG_PRICE_BELOW, TradingConstants.FILTER_VALUE_PRICE_BELOW));
		
		_marketScanningLogger.info("Requesting market scanner subscription using request id: '" + requestId + "'...");
		
		synchronized (_context.getHistoricalData()) {
			try {
				_context.getIbController().getSocket().reqScannerSubscription(requestId, subscription, null, filterTagValues);
				_context.getHistoricalData().wait(TradingConstants.FIVE_MINUTES_TIMEOUT_MS);
			} catch (InterruptedException e) {
				throw new RuntimeException("Error fetching historical data.");
			}
		}
		
		if (_hasReceivedApiResponse == true) {
			_marketScanningLogger.info("Received scan results for request ID: '"  + requestId + "'. Changing state...");
			_context.setState(new FilterByFloatState(_context));
		} else {
			_context.getIbController().getSocket().cancelScannerSubscription(requestId);
			_marketScanningLogger.warn("Timeout reached. Did not receive API response. Resetting scan results and repeating state.");
			_context.getScanResult().clear();
			_context.setState(new MarketScanningState(_context));
		}
	}

	@Override
	public void processAccountSummary(String logMessage, int reqId, String account, String tag, String value, String currency) 	{	
		// intentionally left blank 
	}

	@Override
	public void processAccountSummaryEnd(int reqId) 	{	
		// intentionally left blank 
	}

	@Override
	public void processScannerData(int reqId, int rank, ContractDetails contractDetails, String distance, String benchmark, String projection, String legsStr) {
		_context.getScanResult().put(rank, contractDetails.contract());
	}

	@Override
	public void processDataEnd(int reqId) {
		synchronized (_context.getHistoricalData()) {
			_context.getIbController().getSocket().cancelScannerSubscription(reqId);
			_hasReceivedApiResponse = true;
			_context.getHistoricalData().notify();
		}
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
