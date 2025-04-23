package com.rickauer.marketmonarch;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.rickauer.marketmonarch.configuration.DatabaseConnector;
import com.rickauer.marketmonarch.db.ApiKeyAccess;
import com.rickauer.marketmonarch.db.FinancialDataAccess;

class HealthCheckerTest {

	private static HealthChecker _healthChecker;
	private static ApiKeyAccess _apiValid;
	private static FinancialDataAccess _finValid;
	
	@BeforeAll
	public static void initializeTestData() {
		DatabaseConnector.INSTANCE.initializeConfigReader();
		_healthChecker = new HealthChecker();
		_apiValid = new ApiKeyAccess(DatabaseConnector.INSTANCE.getUrlAPIKey(), DatabaseConnector.INSTANCE.getUsername(), DatabaseConnector.INSTANCE.getPassword());
		_finValid = new FinancialDataAccess(DatabaseConnector.INSTANCE.getUrlFinancialData(), DatabaseConnector.INSTANCE.getUsername(), DatabaseConnector.INSTANCE.getPassword());
		DatabaseConnector.INSTANCE.flushDatabaseConnectionEssentials();
	}
	
	@Test
	void isOperationalConfigReaderTrue() {
		_healthChecker.add(DatabaseConnector.INSTANCE);
		_healthChecker.runHealthCheck();
		assertTrue(_healthChecker.isTypeOperational(DatabaseConnector.INSTANCE));
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
	
	@AfterAll
	public static void tearDown() {
		_apiValid.close();
		_finValid.close();
	}
}
