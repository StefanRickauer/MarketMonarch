package com.rickauer.marketmonarch.api.data.processing;

public class PreTradeMarketScanningState extends PreTradeState {

	public PreTradeMarketScanningState(PreTradeContext context) {
		super(context);
	}

	@Override
	public void onEnter() {
		System.out.println("DEBUG: Entered Market Scanning State.");
		
	}

}
