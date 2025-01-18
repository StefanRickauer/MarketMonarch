package com.rickauer.marketmonarch.api.connect;

import com.rickauer.marketmonarch.utils.Visitor;

public final class AlphaVantageConnector extends ApiServiceConnector {
	
	public AlphaVantageConnector(String provider, String token) {
		super(provider, token);
	}
	
	@Override
	public void accept(Visitor visitor) {
		visitor.visit(this);
	}
}
