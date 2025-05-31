package com.rickauer.marketmonarch.api.data.processing.pretrade;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.ib.client.Bar;
import com.ib.client.ContractDetails;

public class PreTradeRequestHistoricalDataState extends PreTradeState {

	private static Logger _reqHistDataLogger = LogManager.getLogger(PreTradeRequestHistoricalDataState.class.getName());
	
	public PreTradeRequestHistoricalDataState(PreTradeContext context) {
		super(context);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void onEnter() {
		
		_reqHistDataLogger.info("Entered Request Historical Data State (pre trade).");
		
		synchronized (_context) {
			_context.notify();
		}
		
	}

	@Override
	public void processAccountSummary(String logMessage, int reqId, String account, String tag, String value,
			String currency) {
		// intentionally left blank
	}

	@Override
	public void processAccountSummaryEnd(int reqId) {
		// intentionally left blank
	}

	@Override
	public void processScannerData(int reqId, int rank, ContractDetails contractDetails, String distance,
			String benchmark, String projection, String legsStr) {
		// intentionally left blank
	}

	@Override
	public void processDataEnd(int reqId) {
		// intentionally left blank
	}

	@Override
	public void processHistoricalData(int reqId, Bar bar) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void processHistoricalDataEnd(int reqId, String startDateStr, String endDateStr) {
		// TODO Auto-generated method stub
		
	}

}
