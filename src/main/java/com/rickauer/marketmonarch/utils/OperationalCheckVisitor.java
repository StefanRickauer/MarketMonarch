package com.rickauer.marketmonarch.utils;

import com.rickauer.marketmonarch.api.connect.AlphaVantageConnector;
import com.rickauer.marketmonarch.api.connect.FmpConnector;
import com.rickauer.marketmonarch.api.connect.MailtrapServiceConnector;
import com.rickauer.marketmonarch.api.connect.StockNewsConnector;
import com.rickauer.marketmonarch.api.controller.InteractiveBrokersApiController;
import com.rickauer.marketmonarch.configuration.DatabaseConnector;
import com.rickauer.marketmonarch.db.ApiKeyDao;
import com.rickauer.marketmonarch.db.FinancialDataDao;

public final class OperationalCheckVisitor implements Visitor {

	private boolean _isOperational;
	
	public OperationalCheckVisitor() {
		_isOperational = false;
	}
	
	public boolean getOperational() {
		return _isOperational;
	}
	
	@Override
	public void visit(DatabaseConnector config) {
		
		DatabaseConnector.INSTANCE.initializeDatabaseConnector();
		
		if (DatabaseConnector.INSTANCE.getUrlFinancialData().isEmpty())
			_isOperational = false;
		
		_isOperational = true;
		
		DatabaseConnector.INSTANCE.flushDatabaseConnectionEssentials();
	}

	@Override
	public void visit(ApiKeyDao apiAccess) {
		_isOperational = apiAccess.isReadyForOperation(5);
	}

	@Override
	public void visit(FinancialDataDao finAccess) {
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
	
	@Override
	public void visit(AlphaVantageConnector alphavantage) {
		_isOperational = alphavantage.isOperational();
	}
	
	@Override
	public void visit(FmpConnector fmp) {
		_isOperational = fmp.isOperational();
	}
	
	@Override 
	public void visit(InteractiveBrokersApiController ibController) {
		_isOperational = ibController.isOperational();
	}
}
