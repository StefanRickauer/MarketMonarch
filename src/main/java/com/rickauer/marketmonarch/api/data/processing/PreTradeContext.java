package com.rickauer.marketmonarch.api.data.processing;

import java.util.ArrayList;
import java.util.List;

import com.rickauer.marketmonarch.api.controller.InteractiveBrokersApiController;
import com.rickauer.marketmonarch.api.data.AccountSummaryItem;

public class PreTradeContext {

	PreTradeState _state;
	private final InteractiveBrokersApiController _controller;
	private List<AccountSummaryItem> _accountSummary;
	
	public PreTradeContext(InteractiveBrokersApiController controller) {
		_controller = controller;
		_accountSummary = new ArrayList<>();
	}
	
	public InteractiveBrokersApiController getController() {
		return _controller;
	}
	
	public PreTradeState getState() {
		return _state;
	}
	
	public void setState(PreTradeState state) {
		_state = state;
	}
	
	public List<AccountSummaryItem> getAccountDetails() {
		return _accountSummary;
	}
}
