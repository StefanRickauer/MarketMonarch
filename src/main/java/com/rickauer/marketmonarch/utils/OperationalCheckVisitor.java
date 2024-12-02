package com.rickauer.marketmonarch.utils;

import com.rickauer.marketmonarch.api.MailtrapServiceConnector;
import com.rickauer.marketmonarch.api.StockNewsConnector;
import com.rickauer.marketmonarch.configuration.ConfigReader;
import com.rickauer.marketmonarch.db.ApiKeyAccess;
import com.rickauer.marketmonarch.db.FinancialDataAccess;

public class OperationalCheckVisitor implements Visitor {

	private boolean _isOperational;
	
	public OperationalCheckVisitor() {
		_isOperational = false;
	}
	
	public boolean getOperational() {
		return _isOperational;
	}
	
	@Override
	public void visit(ConfigReader config) {
		
		ConfigReader.INSTANCE.initializeConfigReader();
		
		if (ConfigReader.INSTANCE.getUrlFinancialData().isEmpty())
			_isOperational = false;
		
		_isOperational = true;
		
		ConfigReader.INSTANCE.flushDatabaseConnectionEssentials();
	}

	@Override
	public void visit(ApiKeyAccess apiAccess) {
		_isOperational = apiAccess.isReadyForOperation(5);
	}

	@Override
	public void visit(FinancialDataAccess finAccess) {
		_isOperational = finAccess.isReadyForOperation(5);
	}
	
	@Override
	public void visit(MailtrapServiceConnector mailtrap) {
		_isOperational = mailtrap.isOperational();
	}
	
	@Override
	public void visit(StockNewsConnector stocknews) {
		_isOperational = stocknews.isOperational();
	}
}
