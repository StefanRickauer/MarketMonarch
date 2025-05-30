package com.rickauer.marketmonarch.api.data.processing.pretrade;

import java.util.LinkedList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.ib.client.ContractDetails;
import com.ib.client.ScannerSubscription;
import com.ib.client.TagValue;
import com.rickauer.marketmonarch.MarketMonarch;
import com.rickauer.marketmonarch.constants.TradingConstants;

public class PreTradeMarketScanningState extends PreTradeState {

	private static Logger _marketScanningLogger = LogManager.getLogger(PreTradeMarketScanningState.class.getName());
	
	public PreTradeMarketScanningState(PreTradeContext context) {
		super(context);
	}

	@Override
	public void onEnter() {
		_marketScanningLogger.info("Entered Market Scanning State.");
		_marketScanningLogger.info("Setting up market scanner subscription and requesting scan results...");
//		MarketMonarch._interactiveBrokersController.requestScannerSubscription("2", "20");
		
		int requestId = _context.getIbController().getNextRequestId();

		ScannerSubscription subscription = new ScannerSubscription();
		subscription.instrument(TradingConstants.SCANNER_INSTRUMENT);
		subscription.locationCode(TradingConstants.SCANNER_LOCATION_CODE);
		subscription.scanCode(TradingConstants.SCANNER_SCAN_CODE);
		
		List<TagValue> filterTagValues = new LinkedList<>();
		filterTagValues.add(new TagValue(TradingConstants.FILTER_TAG_PRICE_ABOVE, TradingConstants.FILTER_VALUE_PRICE_ABOVE));
		filterTagValues.add(new TagValue(TradingConstants.FILTER_TAG_PRICE_BELOW, TradingConstants.FILTER_VALUE_PRICE_BELOW));
		
		_marketScanningLogger.info("Requesting market scanner subscription using request id: '" + requestId + "'...");
		_context.getIbController().getSocket().reqScannerSubscription(requestId, subscription, null, filterTagValues);
		
		
	}

	@Override
	public void processAccountSummary(String logMessage, int reqId, String account, String tag, String value, String currency) 	{	/* intentionally left blank */ }

	@Override
	public void processAccountSummaryEnd(int reqId) 	{	/* intentionally left blank */ }

	@Override
	public void processScannerData(int reqId, int rank, ContractDetails contractDetails, String distance, String benchmark, String projection, String legsStr) {
		_context.getScanResult().put(rank, contractDetails.contract());
	}

	@Override
	public void processDataEnd(int reqId) {
		_context.getIbController().getSocket().cancelScannerSubscription(reqId);
		_marketScanningLogger.info("Received scan results for request ID: '"  + reqId + "'. Changing state...");
		_context.setState(new PreTradeFilterByFloatState(_context));
	}


}
