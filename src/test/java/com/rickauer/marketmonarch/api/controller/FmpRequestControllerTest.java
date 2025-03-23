package com.rickauer.marketmonarch.api.controller;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import com.rickauer.marketmonarch.api.connect.FmpConnector;
import com.rickauer.marketmonarch.api.enums.FmpServiceRequest;
import com.rickauer.marketmonarch.configuration.ConfigReader;
import com.rickauer.marketmonarch.db.ApiKeyAccess;

public class FmpRequestControllerTest {

	private static ApiKeyAccess _apiAccess;
	private static FmpConnector _fmpConnector;
	private static FmpRequestController _controller;
	private static String _response;
	
	@BeforeAll
	public static void initializeTestData() {
		ConfigReader.INSTANCE.initializeConfigReader();
		_apiAccess = new ApiKeyAccess(ConfigReader.INSTANCE.getUrlAPIKey(), ConfigReader.INSTANCE.getUsername(), ConfigReader.INSTANCE.getPassword());
		_fmpConnector = new FmpConnector("fmp", _apiAccess.executeSqlQueryAndGetFirstResultAsString("SELECT token FROM credentials where provider = 'FMP'", "token"));
		_controller = new FmpRequestController(_fmpConnector.getToken(), FmpServiceRequest.ALL_SHARES_FLOAT);
		_response = _controller.requestAllShareFloat();
		ConfigReader.INSTANCE.flushDatabaseConnectionEssentials();
	}
	
	@Test
	void requestAllShareFloatTest() {
		assertFalse(_response.equals(""));
	}
}
