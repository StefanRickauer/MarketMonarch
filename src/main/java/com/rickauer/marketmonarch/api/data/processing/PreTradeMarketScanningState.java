package com.rickauer.marketmonarch.api.data.processing;

import com.rickauer.marketmonarch.MarketMonarch;

public class PreTradeMarketScanningState extends PreTradeState {

	public PreTradeMarketScanningState(PreTradeContext context) {
		super(context);
	}

	@Override
	public void onEnter() {
		System.out.println("DEBUG: Entered Market Scanning State.");
		
		MarketMonarch._interactiveBrokersController.requestScannerSubscription("2", "20");
	}

	@Override
	public void processAccountSummary(String logMessage, int reqId, String account, String tag, String value, String currency) 	{	/* intentionally left blank */ }

	@Override
	public void processAccountSummaryEnd(int reqId) 	{	/* intentionally left blank */ }

	@Override
	public void processDataEnd(int reqId) {
		synchronized(_context) {
			_context.notify();
		}
		
	}

}
