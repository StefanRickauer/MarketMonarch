package com.rickauer.marketmonarch.api;

import com.rickauer.marketmonarch.utils.Visitor;

public class StockNewsConnector extends ApiServiceConnector {

	public StockNewsConnector(String provider, String token) {
		super(provider, token);
	}
	
	@Override
	public void accept(Visitor visitor) {
		visitor.visit(this);
	}
}
