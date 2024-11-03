package com.rickauer.marketmonarch.db;

import com.rickauer.marketmonarch.configuration.ConfigReader;

public class FinancialDataAccess extends DBAccess {

	public FinancialDataAccess(boolean essential, String dbUrl, String user, String password) {
		super(essential, dbUrl, user, password);
	}

	@Override
	public void visit(ConfigReader config) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void visit(ApiKeyAccess apiAccess) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void visit(FinancialDataAccess finAccess) {
		// TODO Auto-generated method stub
		
	}

}
