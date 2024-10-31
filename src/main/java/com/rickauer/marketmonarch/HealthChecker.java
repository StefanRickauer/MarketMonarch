package com.rickauer.marketmonarch;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


import com.rickauer.marketmonarch.configuration.ConfigReader;
import com.rickauer.marketmonarch.db.ApiKeyAccess;
import com.rickauer.marketmonarch.db.FinancialDataAccess;

public class HealthChecker {
	
	private static Logger healthCheckerLogger = LogManager.getLogger(HealthChecker.class.getName()); 
	
	private HealthChecker() {
		throw new UnsupportedOperationException("The class '" + HealthChecker.class.getCanonicalName() + "' is not meant to be instanciated.");
	}
	
	public static void runHealthCheck() {
		
		if (!ConfigReader.INSTANCE.isSourceFilePresent()) {
			healthCheckerLogger.error("Check for operational readiness failed. Could not load operating environment. Missing configuration file.");
			throw new RuntimeException("Could not find '" + ConfigReader.INSTANCE.getSourceFile() + "'.");
		}
		ConfigReader.INSTANCE.initializeConfigReader();
		
		ApiKeyAccess apiData = new ApiKeyAccess(true, ConfigReader.INSTANCE.getUrlAPIKey(), ConfigReader.INSTANCE.getUsername(), ConfigReader.INSTANCE.getPassword());
		if (!apiData.isReadyForOperation(5))
			throw new RuntimeException("Could not access API keys.");
		
		FinancialDataAccess financeData = new FinancialDataAccess(false, ConfigReader.INSTANCE.getUrlAPIKey(), ConfigReader.INSTANCE.getUsername(), ConfigReader.INSTANCE.getPassword());
		if (!financeData.isReadyForOperation(5))
			System.err.println("Could not access financial data.");
	}
}
