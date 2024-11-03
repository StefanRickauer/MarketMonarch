package com.rickauer.marketmonarch.utils;

import com.rickauer.marketmonarch.configuration.ConfigReader;
import com.rickauer.marketmonarch.db.ApiKeyAccess;
import com.rickauer.marketmonarch.db.FinancialDataAccess;

public interface Visitor {
	void visit(ConfigReader config);
	void visit(ApiKeyAccess apiAccess);
	void visit(FinancialDataAccess finAccess);
}
