package com.rickauer.marketmonarch.api.data.processing.trade;

import java.time.ZonedDateTime;

import com.ib.client.Bar;
import com.ib.client.Decimal;

public class TradeBuyProcessingState extends TradeMonitorState {

	TradeBuyProcessingState(TradeMonitorContext context) {
		super(context);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void onEnter() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void processOrderData(String msg, String status, Decimal filled, Decimal remaining, double avgFillPrice) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void processHistoricalData(int reqId, ZonedDateTime time, double open, double high, double low, double close,
			double volume) {
		// intentionally left blank 
	}
	
	@Override
	public void processHistoricalDataEnd(int reqId, String startDateStr, String endDateStr) {
		// intentionally left blank 
	}


}
