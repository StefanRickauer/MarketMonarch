package com.rickauer.marketmonarch.api.controller;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import com.rickauer.marketmonarch.api.connect.AlphaVantageConnector;
import com.rickauer.marketmonarch.configuration.DatabaseConnector;
import com.rickauer.marketmonarch.db.ApiKeyAccess;

public class AlhaVantageRequestControllerTest {

	private static ApiKeyAccess _apiAccess;
	private static AlphaVantageConnector _alphaVantageConnector;
	private static AlphaVantageRequestController _controller;
	
	@BeforeAll
	public static void initializeTestData() {
		DatabaseConnector.INSTANCE.initializeConfigReader();
		_apiAccess = new ApiKeyAccess(DatabaseConnector.INSTANCE.getUrlAPIKey(), DatabaseConnector.INSTANCE.getUsername(), DatabaseConnector.INSTANCE.getPassword());
		_alphaVantageConnector = new AlphaVantageConnector("alphavantageapi", _apiAccess.executeSqlQueryAndGetFirstResultAsString("SELECT token FROM credentials where provider = 'alphavantage'", "token"));
		_controller = new AlphaVantageRequestController(_alphaVantageConnector.getToken());
		DatabaseConnector.INSTANCE.flushDatabaseConnectionEssentials();
	}
	
	@Test
	void requestSentimentScoreWithDataTest() {
		assertTrue(_controller.requestSentimentScore("AAPL") != -2.0);
	}
	
	@Test
	void requestSentimentScoreEmptyResponseTest() {
		assertTrue(_controller.requestSentimentScore("MOND") == -2.0);
	}
}
