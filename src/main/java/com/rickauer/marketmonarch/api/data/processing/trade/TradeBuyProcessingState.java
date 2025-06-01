package com.rickauer.marketmonarch.api.data.processing.trade;

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
	public void processTradingData() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void processOrderData(String msg, String status, Decimal filled, Decimal remaining, double avgFillPrice) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void dispose() {
		// TODO Auto-generated method stub
		
	}

}
