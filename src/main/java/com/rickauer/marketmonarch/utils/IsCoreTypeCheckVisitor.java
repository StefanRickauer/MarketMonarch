package com.rickauer.marketmonarch.utils;

import com.rickauer.marketmonarch.api.connect.AlphaVantageConnector;
import com.rickauer.marketmonarch.api.connect.FmpConnector;
import com.rickauer.marketmonarch.api.connect.MailtrapServiceConnector;
import com.rickauer.marketmonarch.api.connect.StockNewsConnector;
import com.rickauer.marketmonarch.api.controller.InteractiveBrokersApiController;
import com.rickauer.marketmonarch.configuration.DatabaseConnector;
import com.rickauer.marketmonarch.db.ApiKeyDao;
import com.rickauer.marketmonarch.db.FinancialDataDao;

public final class IsCoreTypeCheckVisitor implements Visitor {

	private boolean _isCoreType;
	
	public boolean getCoreType() {
		return _isCoreType;
	}
	
	public IsCoreTypeCheckVisitor() {
		_isCoreType = false;
	}
	
	@Override
	public void visit(DatabaseConnector config) {
		_isCoreType = true;
	}

	@Override
	public void visit(ApiKeyDao apiAccess) {
		_isCoreType = true;
	}

	@Override
	public void visit(FinancialDataDao finAccess) {
		_isCoreType = false;
	}
	
	@Override
	public void visit(MailtrapServiceConnector mailtrap) {
		_isCoreType = false;
	}
	
	@Override
	public void visit(StockNewsConnector stocknews) {
		_isCoreType = true;
	}
	
	@Override
	public void visit(AlphaVantageConnector alphavantage) {
		_isCoreType = true;
	}
	
	@Override
	public void visit(FmpConnector fmp) {
		_isCoreType = true;
	}
	
	@Override
	public void visit(InteractiveBrokersApiController ibController) {
		_isCoreType = true;
	}
}
