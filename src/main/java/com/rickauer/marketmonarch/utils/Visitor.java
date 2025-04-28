package com.rickauer.marketmonarch.utils;

import com.rickauer.marketmonarch.api.connect.AlphaVantageConnector;
import com.rickauer.marketmonarch.api.connect.FmpConnector;
import com.rickauer.marketmonarch.api.connect.MailtrapServiceConnector;
import com.rickauer.marketmonarch.api.connect.StockNewsConnector;
import com.rickauer.marketmonarch.api.controller.InteractiveBrokersApiController;
import com.rickauer.marketmonarch.configuration.DatabaseConnector;
import com.rickauer.marketmonarch.db.ApiKeyDao;
import com.rickauer.marketmonarch.db.FinancialDataDao;

public interface Visitor {
	void visit(DatabaseConnector config);
	
	void visit(ApiKeyDao apiAccess);
	void visit(FinancialDataDao finAccess);
	
	void visit(MailtrapServiceConnector mailtrap);
	void visit(StockNewsConnector stocknews);
	void visit(AlphaVantageConnector alphavantage);
	void visit(FmpConnector fmp);
	void visit(InteractiveBrokersApiController ibController);
}
