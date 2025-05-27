package com.rickauer.marketmonarch.api.data.processing;

public abstract class PreTradeState {

	PreTradeContext _preTradeContext;
	
	public PreTradeState(PreTradeContext context) {
		_preTradeContext = context;
		onEnter();
	}
	
	public abstract void onEnter();
	public abstract void processAccountSummary(String logMessage, int reqId, String account, String tag, String value, String currency);
	public abstract void processAccountSummaryEnd(int reqId);
}
