package com.rickauer.marketmonarch.api.data.processing;

import com.ib.client.ContractDetails;

public class PreTradeInactiveState extends PreTradeState {

	public PreTradeInactiveState(PreTradeContext context) {
		super(context);
	}

	@Override
	public void onEnter() 		{	/* intentionally left blank */ }

	@Override
	public void processAccountSummary(String logMessage, int reqId, String account, String tag, String value, String currency) 	{	/* intentionally left blank */ }

	@Override
	public void processAccountSummaryEnd(int reqId) 	{	/* intentionally left blank */ }

	@Override
	public void processDataEnd(int reqId) {	/* intentionally left blank */ }

	@Override
	public void processScannerData(int reqId, int rank, ContractDetails contractDetails, String distance, String benchmark, String projection, String legsStr) {	/* intentionally left blank */ }
}
