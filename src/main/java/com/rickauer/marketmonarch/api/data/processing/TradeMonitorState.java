package com.rickauer.marketmonarch.api.data.processing;

import com.ib.client.Decimal;

public abstract class TradeMonitorState {
	
	TradeMonitorContext _context;
	
	TradeMonitorState(TradeMonitorContext context) {
		_context = context;
	}
	
	public abstract void onEnter();
	public abstract void processTradingData();
	public abstract void processOrderData(String msg, String status, Decimal filled, Decimal remaining, double avgFillPrice);
	public abstract void dispose();
}
