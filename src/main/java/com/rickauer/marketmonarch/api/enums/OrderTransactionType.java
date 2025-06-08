package com.rickauer.marketmonarch.api.enums;

public enum OrderTransactionType {
	BUY("BUY"), 
	SELL("SELL");
	
	private String action;
	
	private OrderTransactionType(String action) {
		this.action = action;
	}
	
	public String getAction() {
		return action;
	}
}