package com.rickauer.marketmonarch.api.data.processing;

public abstract class TradeMonitorState {
	
	TradeMonitor _context;
	
	TradeMonitorState(TradeMonitor context) {
		_context = context;
	}
	
	protected abstract void onEnter();
	protected abstract void processTradingData();
	protected abstract void processOrderData();
}
