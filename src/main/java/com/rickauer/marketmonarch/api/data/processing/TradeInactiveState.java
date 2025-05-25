package com.rickauer.marketmonarch.api.data.processing;

import com.ib.client.Decimal;

public class TradeInactiveState extends TradeMonitorState {

	TradeInactiveState(TradeMonitorContext context) {
		super(context);
	}

	@Override
	public void onEnter() 				{	/* intentionally left blank */ }

	@Override
	public void processTradingData() 	{	/* intentionally left blank */ }

	@Override
	public void processOrderData(String msg, String status, Decimal filled, Decimal remaining, double avgFillPrice) 		{	/* intentionally left blank */ }

}
