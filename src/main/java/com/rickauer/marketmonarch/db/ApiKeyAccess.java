package com.rickauer.marketmonarch.db;

import com.rickauer.marketmonarch.configuration.ConfigReader;
import com.rickauer.marketmonarch.utils.Visitor;

public final class ApiKeyAccess extends DBAccess {

	public ApiKeyAccess(String dbUrl, String user, String password) {
		super(dbUrl, user, password);
	}

	@Override
	public void accept(Visitor visitor) {
		visitor.visit(this);
	}
	
	
}
