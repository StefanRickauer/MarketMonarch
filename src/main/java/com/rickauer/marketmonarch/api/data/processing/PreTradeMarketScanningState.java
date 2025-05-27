package com.rickauer.marketmonarch.api.data.processing;

public class PreTradeMarketScanningState extends PreTradeState {

	public PreTradeMarketScanningState(PreTradeContext context) {
		super(context);
	}

	@Override
	public void onEnter() {
		System.out.println("DEBUG: Entered Market Scanning State.");
		
		synchronized(_context) {
			_context.notify();
		}
	}

	@Override
	public void processAccountSummary(String logMessage, int reqId, String account, String tag, String value, String currency) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void processAccountSummaryEnd(int reqId) {
		// TODO Auto-generated method stub
		
	}

}
