package com.rickauer.marketmonarch.api.data.processing;

public abstract class PreTradeState {

	PreTradeContext _preTradeContext;
	
	public PreTradeState(PreTradeContext context) {
		_preTradeContext = context;
		onEnter();
	}
	
	public abstract void onEnter();
}
