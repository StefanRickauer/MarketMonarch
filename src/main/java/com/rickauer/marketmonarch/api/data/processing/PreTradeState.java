package com.rickauer.marketmonarch.api.data.processing;

import com.ib.client.ContractDetails;

public abstract class PreTradeState {

	PreTradeContext _context;
	
	public PreTradeState(PreTradeContext context) {
		_context = context;
	}
	
	public abstract void onEnter();
	public abstract void processAccountSummary(String logMessage, int reqId, String account, String tag, String value, String currency);
	public abstract void processAccountSummaryEnd(int reqId);
	public abstract void processScannerData(int reqId, int rank, ContractDetails contractDetails, String distance, String benchmark, String projection, String legsStr);
	public abstract void processDataEnd(int reqId);
}
