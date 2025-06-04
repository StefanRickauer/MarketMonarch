package com.rickauer.marketmonarch.api.data.processing.pretrade;

import com.ib.client.Bar;
import com.ib.client.ContractDetails;

public abstract class PreTradeState {

	PreTradeContext _context;
	boolean _hasReceivedApiResponse;
	
	public PreTradeState(PreTradeContext context) {
		_context = context;
		_hasReceivedApiResponse = false;
	}
	
	public abstract void onEnter();
	public abstract void processAccountSummary(String logMessage, int reqId, String account, String tag, String value, String currency);
	public abstract void processAccountSummaryEnd(int reqId);
	public abstract void processScannerData(int reqId, int rank, ContractDetails contractDetails, String distance, String benchmark, String projection, String legsStr);
	public abstract void processDataEnd(int reqId);
	public abstract void processHistoricalData(int reqId, Bar bar);
	public abstract void processHistoricalDataEnd(int reqId, String startDateStr, String endDateStr);
}
