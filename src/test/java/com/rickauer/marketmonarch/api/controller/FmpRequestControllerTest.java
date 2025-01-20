package com.rickauer.marketmonarch.api.controller;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import com.rickauer.marketmonarch.api.connect.FmpConnector;
import com.rickauer.marketmonarch.configuration.ConfigReader;
import com.rickauer.marketmonarch.db.ApiKeyAccess;

public class FmpRequestControllerTest {

	private static ApiKeyAccess _apiAccess;
	private static FmpConnector _fmpConnector;
	private static FmpRequestController _controller;
	
	@BeforeAll
	public static void initializeTestData() {
		ConfigReader.INSTANCE.initializeConfigReader();
		_apiAccess = new ApiKeyAccess(ConfigReader.INSTANCE.getUrlAPIKey(), ConfigReader.INSTANCE.getUsername(), ConfigReader.INSTANCE.getPassword());
		_fmpConnector = new FmpConnector("fmp", _apiAccess.executeSqlQueryAndGetFirstResultAsString("SELECT token FROM credentials where provider = 'FMP'", "token"));
		_controller = new FmpRequestController(_fmpConnector.getToken());
		ConfigReader.INSTANCE.flushDatabaseConnectionEssentials();
	}
	
	; // This test should succeed, as soon as a subscription for the premium tier is purchased
	@Test
	void requestCompanyShareFloatTest() {
		assertTrue(_controller.requestCompanyShareFloat("AAPL") != -1L);
	}
	
	@Test
	void requestCompanyShareFloatEmptyResponseTest() {
		assertFalse(_controller.requestCompanyShareFloat("MOND") != -1L);
	}
}
