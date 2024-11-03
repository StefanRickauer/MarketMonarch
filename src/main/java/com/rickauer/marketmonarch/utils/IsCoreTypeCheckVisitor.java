package com.rickauer.marketmonarch.utils;

import com.rickauer.marketmonarch.configuration.ConfigReader;
import com.rickauer.marketmonarch.db.ApiKeyAccess;
import com.rickauer.marketmonarch.db.FinancialDataAccess;

public class IsCoreTypeCheckVisitor implements Visitor {

	private boolean isCoreType;
	
	public boolean getCoreType() {
		return isCoreType;
	}
	
	public IsCoreTypeCheckVisitor() {
		isCoreType = false;
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
