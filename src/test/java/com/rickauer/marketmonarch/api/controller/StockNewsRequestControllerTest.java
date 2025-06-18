package com.rickauer.marketmonarch.api.controller;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import com.rickauer.marketmonarch.api.connect.StockNewsConnector;
import com.rickauer.marketmonarch.api.enums.SentimentFilterPeriod;
import com.rickauer.marketmonarch.configuration.DatabaseConnector;
import com.rickauer.marketmonarch.db.ApiKeyDao;

public class StockNewsRequestControllerTest {

	// StockNews-API key and account were deleted. Refresh to use the StockNews-API. Until then, all tests will fail.
	
	private static ApiKeyDao _apiAccess;
	private static StockNewsConnector _stockNewsConnector;
	private static StockNewsRequestController _controller;
	
	@BeforeAll
	public static void initializeTestData() {
		DatabaseConnector.INSTANCE.initializeDatabaseConnector();
		_apiAccess = new ApiKeyDao(DatabaseConnector.INSTANCE.getUrlAPIKey(), DatabaseConnector.INSTANCE.getUsername(), DatabaseConnector.INSTANCE.getPassword());
		_stockNewsConnector = new StockNewsConnector("stocknewsapi", _apiAccess.executeSqlQueryAndGetFirstResultAsString("SELECT token FROM credentials where provider = 'stocknewsapi'", "token"));
		_controller = new StockNewsRequestController(_stockNewsConnector.getToken());
		DatabaseConnector.INSTANCE.flushDatabaseConnectionEssentials();
	}
	
	@Test
	void requestSentimentScoreWithDataTest() {
		assertTrue(_controller.requestSentimentScore("AAPL", SentimentFilterPeriod.TODAY) != 0.0);
	}

	// In case this test fails, that just means that data was received. I used 'XXXXX' but after a few successful tries received a ConnectionException. So I thought it was better to use something else.
	@Test
	void requestSentimentScoreEmptyResponseTest() {
		assertTrue(_controller.requestSentimentScore("HMBL", SentimentFilterPeriod.TODAY) == 0.0);
	}

}
