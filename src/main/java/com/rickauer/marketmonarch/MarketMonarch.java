package com.rickauer.marketmonarch;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.rickauer.marketmonarch.configuration.ConfigReader;
import com.rickauer.marketmonarch.configuration.FileSupplier;
import com.rickauer.marketmonarch.reporting.LineChartCreator;

import java.awt.Desktop;
import java.io.File;

import org.apache.commons.lang3.exception.*;

public class MarketMonarch {

	private static final String PROGRAM	= "MarketMonarch";
	private static final String VERSION	= "0.01";
	
	private static Logger marketMonarchLogger = LogManager.getLogger(MarketMonarch.class.getName());
	
	public static void main(String[] args) {
		try {
			marketMonarchLogger.info("Starting " + PROGRAM + " (version " + VERSION + ").");
			ensureOperationalReadiness();
			// Get DB credentials 
			System.out.println("[DEBUG] Displaying contents of credentials file.");
			ConfigReader.INSTANCE.initializeConfigReader();
			System.out.println(ConfigReader.INSTANCE.getUsername());
			System.out.println(ConfigReader.INSTANCE.getPassword());
			System.out.println(ConfigReader.INSTANCE.getUrlTestDB());
			System.out.println(ConfigReader.INSTANCE.getUrlAPIKey());
			System.out.println(ConfigReader.INSTANCE.getFinancialData());
			ConfigReader.INSTANCE.flushDatabaseConnectionEssentials();
			
			// Query other credentials
			// Make money
		} catch (Throwable t) {
			// Workaround because usage of e will throw exception.
			String stackTrace = ExceptionUtils.getStackTrace(t);
			marketMonarchLogger.error(stackTrace);
		}
	}
	
	private static void ensureOperationalReadiness() {
		marketMonarchLogger.info("Checking operational readiness...");
		if (!ConfigReader.INSTANCE.isSourceFilePresent()) {
			marketMonarchLogger.error("Check for operational readiness failed. Could not load operating environment. Missing configuration file.");
			throw new RuntimeException("Could not find '" + ConfigReader.INSTANCE.getSourceFile() + "'.");
		}
		// do remaining checks
		marketMonarchLogger.info("Checked operational readiness.");
	}
}
