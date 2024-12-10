package com.rickauer.marketmonarch.api.connect;

import java.sql.SQLException;

import com.rickauer.marketmonarch.MarketMonarch;
import com.rickauer.marketmonarch.utils.Visitor;

public final class MailtrapServiceConnector extends ApiServiceConnector {

	
	public MailtrapServiceConnector(String provider, String token) {
		super(provider, token);
	}
	
	@Override
	public void accept(Visitor visitor) {
		visitor.visit(this);
	}
	
}
