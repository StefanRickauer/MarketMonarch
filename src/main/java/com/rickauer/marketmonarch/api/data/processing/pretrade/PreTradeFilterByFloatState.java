package com.rickauer.marketmonarch.api.data.processing.pretrade;

import com.ib.client.ContractDetails;

public class PreTradeFilterByFloatState extends PreTradeState {

	public PreTradeFilterByFloatState(PreTradeContext context) {
		super(context);
	}

	@Override
	public void onEnter() {
		
		System.out.println("Entered Filter By Float State.");
		
		synchronized(_context) {
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
