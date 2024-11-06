package com.rickauer.marketmonarch.api;

import com.rickauer.marketmonarch.MarketMonarch;
import com.rickauer.marketmonarch.utils.Visitor;

public class MailtrapServiceConnector extends ApiServiceConnector {

	
	public MailtrapServiceConnector(String provider, String token, String query) {
		super(provider, token, query);
		; // Hier weiter: Query siehe ApiKeyAccessTest
		MarketMonarch._apiAccess.executeSqlQuery("SELECT ");
	}

	@Override
	public void accept(Visitor visitor) {
		;// TODO Auto-generated method stub
		
	}
	
}
