package com.rickauer.marketmonarch.utils;

import com.rickauer.marketmonarch.configuration.ConfigReader;
import com.rickauer.marketmonarch.db.ApiKeyAccess;
import com.rickauer.marketmonarch.db.FinancialDataAccess;

public class OperationalCheckVisitor implements Visitor {

	private boolean isOperational;
	
	public OperationalCheckVisitor() {
		isOperational = false;
	}
	
	public boolean getOperational() {
		return isOperational;
	}
	
	@Override
	public void visit(ConfigReader config) {
		resetOperationalFlag();
		
		ConfigReader.INSTANCE.initializeConfigReader();
		
		if (ConfigReader.INSTANCE.getFinancialData().isEmpty())
			isOperational = false;
		
		isOperational = true;
		
		ConfigReader.INSTANCE.flushDatabaseConnectionEssentials();
	}

	@Override
	public void visit(ApiKeyAccess apiAccess) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void visit(FinancialDataAccess finAccess) {
		// TODO Auto-generated method stub
		
	}
	
	private void resetOperationalFlag() {
		isOperational = false;
	}
	
}
