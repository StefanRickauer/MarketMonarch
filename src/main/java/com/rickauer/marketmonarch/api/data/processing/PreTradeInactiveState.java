package com.rickauer.marketmonarch.api.data.processing;

public class PreTradeInactiveState extends PreTradeState {

	public PreTradeInactiveState(PreTradeContext context) {
		super(context);
	}

	@Override
	public void onEnter() 	{	/* intentionally left blank */ }

	@Override
	public void processAccountSummary(String logMessage, int reqId, String account, String tag, String value, String currency) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void processAccountSummaryEnd(int reqId) {
		// TODO Auto-generated method stub
		
	}
}
