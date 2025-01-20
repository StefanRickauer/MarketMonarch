package com.rickauer.marketmonarch.api.connect;

import com.rickauer.marketmonarch.utils.Visitor;

public final class FmpConnector extends ApiServiceConnector {

	public FmpConnector(String provider, String token) {
		super(provider, token);
	}
	
	@Override
	public void accept(Visitor visitor) {
		visitor.visit(this);
	}
}
