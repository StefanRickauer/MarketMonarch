package com.rickauer.marketmonarch.api.data.processing;

import com.ib.client.Decimal;

public abstract class TradeMonitorState {
	
	TradeMonitor _context;
	
	TradeMonitorState(TradeMonitor context) {
		_context = context;
		onEnter();
	}
	
	public abstract void onEnter();
	public abstract void processTradingData();
	public abstract void processOrderData(String msg, String status, Decimal filled, Decimal remaining, double avgFillPrice);
}
