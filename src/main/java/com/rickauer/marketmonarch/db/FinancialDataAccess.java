package com.rickauer.marketmonarch.db;

import com.rickauer.marketmonarch.configuration.ConfigReader;
import com.rickauer.marketmonarch.utils.Visitor;

public class FinancialDataAccess extends DBAccess {

	public FinancialDataAccess(String dbUrl, String user, String password) {
		super(dbUrl, user, password);
	}

	@Override
	public void accept(Visitor visitor) {
		visitor.visit(this);
	}

}
