package com.rickauer.marketmonarch.utils;

import com.rickauer.marketmonarch.configuration.ConfigReader;
import com.rickauer.marketmonarch.db.ApiKeyAccess;
import com.rickauer.marketmonarch.db.FinancialDataAccess;

public class IsCoreTypeCheckVisitor implements Visitor {

	private boolean _isCoreType;
	
	public boolean getCoreType() {
		return _isCoreType;
	}
	
	public IsCoreTypeCheckVisitor() {
		_isCoreType = false;
	}
	
	@Override
	public void visit(ConfigReader config) {
		_isCoreType = true;
	}

	@Override
	public void visit(ApiKeyAccess apiAccess) {
		_isCoreType = true;
	}

	@Override
	public void visit(FinancialDataAccess finAccess) {
		_isCoreType = false;
	}
}
