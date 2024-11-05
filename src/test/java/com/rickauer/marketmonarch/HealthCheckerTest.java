package com.rickauer.marketmonarch;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.rickauer.marketmonarch.configuration.ConfigReader;
import com.rickauer.marketmonarch.db.ApiKeyAccess;
import com.rickauer.marketmonarch.db.FinancialDataAccess;

class HealthCheckerTest {

	private static HealthChecker _healthChecker;
	private static ApiKeyAccess _apiValid;
	private static FinancialDataAccess _finValid;
	
	@BeforeAll
	public static void initializeTestData() {
		ConfigReader.INSTANCE.initializeConfigReader();
		_healthChecker = new HealthChecker();
		_apiValid = new ApiKeyAccess(ConfigReader.INSTANCE.getUrlAPIKey(), ConfigReader.INSTANCE.getUsername(), ConfigReader.INSTANCE.getPassword());
		_finValid = new FinancialDataAccess(ConfigReader.INSTANCE.getUrlFinancialData(), ConfigReader.INSTANCE.getUsername(), ConfigReader.INSTANCE.getPassword());
		ConfigReader.INSTANCE.flushDatabaseConnectionEssentials();
	}
	
	@Test
	void isOperationalConfigReaderTrue() {
		_healthChecker.add(ConfigReader.INSTANCE);
		_healthChecker.runHealthCheck();
		assertTrue(_healthChecker.isTypeOperational(ConfigReader.INSTANCE));
	}
	
	@Test
	void isOperationalApiAccessTrue() {
		_healthChecker.add(_apiValid);
		_healthChecker.runHealthCheck();
		assertTrue(_healthChecker.isTypeOperational(_apiValid));
	}

	@Test
	void isOperationalFinacilaDataAccessTrue() {
		_healthChecker.add(_finValid);
		_healthChecker.runHealthCheck();
		assertTrue(_healthChecker.isTypeOperational(_finValid));
	}
}
