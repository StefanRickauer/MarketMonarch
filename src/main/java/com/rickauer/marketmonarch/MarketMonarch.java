package com.rickauer.marketmonarch;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.rickauer.marketmonarch.api.connect.MailtrapServiceConnector;
import com.rickauer.marketmonarch.api.connect.StockNewsConnector;
import com.rickauer.marketmonarch.configuration.ConfigReader;
import com.rickauer.marketmonarch.configuration.FileSupplier;
import com.rickauer.marketmonarch.db.ApiKeyAccess;
import com.rickauer.marketmonarch.db.FinancialDataAccess;
import com.rickauer.marketmonarch.reporting.LineChartCreator;

import java.awt.Desktop;
import java.io.File;
import java.sql.SQLException;

import org.apache.commons.lang3.exception.*;

public class MarketMonarch {

	private static final String PROGRAM	= "MarketMonarch";
	private static final String VERSION	= "0.03";
	
	private static HealthChecker _healthChecker = new HealthChecker();
	public static ApiKeyAccess _apiAccess;
	private static FinancialDataAccess _finAccess;
	private static StockNewsConnector _stockNews;
	private static MailtrapServiceConnector _mailtrapService;

	private static Logger _marketMonarchLogger = LogManager.getLogger(MarketMonarch.class.getName());
	
	static {
		ConfigReader.INSTANCE.initializeConfigReader();
		
		_apiAccess = new ApiKeyAccess(ConfigReader.INSTANCE.getUrlAPIKey(), ConfigReader.INSTANCE.getUsername(), ConfigReader.INSTANCE.getPassword());
		_finAccess = new FinancialDataAccess(ConfigReader.INSTANCE.getUrlAPIKey(), ConfigReader.INSTANCE.getUsername(), ConfigReader.INSTANCE.getPassword());
		_mailtrapService = new MailtrapServiceConnector("mailtrap", _apiAccess.executeSqlQueryAndGetFirstResultAsString("SELECT token FROM credentials where provider = 'mailtrap'", "token"));
		_stockNews = new StockNewsConnector("stocknewsapi", _apiAccess.executeSqlQueryAndGetFirstResultAsString("SELECT token FROM credentials where provider = 'stocknewsapi'", "token"));

		ConfigReader.INSTANCE.flushDatabaseConnectionEssentials();
		
		; // add class that creates request-IDs
	}
	
	public static void main(String[] args) {
		try {
			_marketMonarchLogger.info("Starting " + PROGRAM + " (version " + VERSION + ").");
			ensureOperationalReadiness();
			
			// Query other credentials
			// Make money
		} catch (Throwable t) {
			// Workaround because usage of e will throw exception.
			String stackTrace = ExceptionUtils.getStackTrace(t);
			_marketMonarchLogger.error(stackTrace);
		} finally {
			_apiAccess.close();
			_finAccess.close();
		}
	}
	
	private static void ensureOperationalReadiness() {
		_marketMonarchLogger.info("Preparing for operational readiness check...");

		_healthChecker.add(ConfigReader.INSTANCE);
		_healthChecker.add(_apiAccess);
		_healthChecker.add(_finAccess);
		_healthChecker.add(_mailtrapService);
		_healthChecker.add(_stockNews);
		
		_marketMonarchLogger.info("Checking operational readiness...");
		_healthChecker.runHealthCheck();
		_marketMonarchLogger.info("Checked operational readiness.");
		
		_marketMonarchLogger.info("Evaluating check results...");
		_healthChecker.analyseCheckResults();
		_marketMonarchLogger.info("Evaluated check results.");
	}
}
