package com.rickauer.marketmonarch.api.data.processing.trade;

import java.time.ZonedDateTime;

import com.ib.client.Bar;
import com.ib.client.Decimal;

public abstract class TradeMonitorState {
	
	TradeMonitorContext _context;
	boolean _hasReceivedApiResponse;
	
	TradeMonitorState(TradeMonitorContext context) {
		_context = context;
		_hasReceivedApiResponse = false;
	}
	
	public abstract void onEnter();
	public abstract void processOrderData(String msg, String status, Decimal filled, Decimal remaining, double avgFillPrice);
	public abstract void processHistoricalData(int reqId, ZonedDateTime time, double open, double high, double low, double close, double volume); 
	public abstract void processHistoricalDataEnd(int reqId, String startDateStr, String endDateStr);
}
