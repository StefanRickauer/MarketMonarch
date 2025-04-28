package com.rickauer.marketmonarch.db;

import com.rickauer.marketmonarch.configuration.DatabaseConnector;
import com.rickauer.marketmonarch.utils.Visitor;

public final class FinancialDataDao extends DatabaseDao {

	public FinancialDataDao(String dbUrl, String user, String password) {
		super(dbUrl, user, password);
	}

	@Override
	public void accept(Visitor visitor) {
		visitor.visit(this);
	}

}
