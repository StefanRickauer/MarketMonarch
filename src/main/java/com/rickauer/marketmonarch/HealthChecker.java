package com.rickauer.marketmonarch;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


import com.rickauer.marketmonarch.configuration.ConfigReader;
import com.rickauer.marketmonarch.db.ApiKeyAccess;
import com.rickauer.marketmonarch.db.FinancialDataAccess;

public class HealthChecker {
	
	private static Logger healthCheckerLogger = LogManager.getLogger(HealthChecker.class.getName()); 
	private List<Verifyable> typesToCheck;
	
	public HealthChecker() {
		typesToCheck = new ArrayList<>();
	}
	
	public void runHealthCheck() {
		
		int operationalTypes = typesToCheck.size();
		int systemCriticalFailures = 0;
		boolean result = false;
		
		for (Verifyable system : typesToCheck) {
			
			result = system.runHealthCheck(); 
			
			if (!result) {
				operationalTypes--;
				healthCheckerLogger.info("Health check for '" + system.getClass().getCanonicalName() + "' failed.");
			}
			
			if (system.isCoreType() && (!result))
				systemCriticalFailures++;
		}
		
		;// hier weiter
		
	
		healthCheckerLogger.info("Check complete: " + operationalTypes + " out of "+ typesToCheck.size() + " operational.\n"
				+ systemCriticalFailures + "system critical failures detected.");
		
		
		if (systemCriticalFailures > 0) {
			throw new RuntimeException("Critical failures detected. Unable to proceed.");
		}
		ConfigReader.INSTANCE.initializeConfigReader();
		
		ApiKeyAccess apiData = new ApiKeyAccess(true, ConfigReader.INSTANCE.getUrlAPIKey(), ConfigReader.INSTANCE.getUsername(), ConfigReader.INSTANCE.getPassword());
		if (!apiData.isReadyForOperation(5))
			throw new RuntimeException("Could not access API keys.");
		
		FinancialDataAccess financeData = new FinancialDataAccess(false, ConfigReader.INSTANCE.getUrlAPIKey(), ConfigReader.INSTANCE.getUsername(), ConfigReader.INSTANCE.getPassword());
		if (!financeData.isReadyForOperation(5))
			System.err.println("Could not access financial data.");
		
		ConfigReader.INSTANCE.flushDatabaseConnectionEssentials();
	}
}
