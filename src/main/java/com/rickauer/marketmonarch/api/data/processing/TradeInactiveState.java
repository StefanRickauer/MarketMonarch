package com.rickauer.marketmonarch.api.data.processing;

public class TradeInactiveState extends TradeMonitorState {

	TradeInactiveState(TradeMonitor context) {
		super(context);
	}

	@Override
	protected void onEnter() 			{	/* intentionally left blank */ }

	@Override
	protected void processTradingData() {	/* intentionally left blank */ }

	@Override
	protected void processOrderData() 	{	/* intentionally left blank */ }

}
