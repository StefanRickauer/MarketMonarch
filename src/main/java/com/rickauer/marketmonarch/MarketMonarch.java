package com.rickauer.marketmonarch;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.rickauer.marketmonarch.database.DatabaseConnectionEssentials;

import org.apache.commons.lang3.exception.*;

public class MarketMonarch {

	private static final String PROGRAM	= "MarketMonarch";
	private static final String VERSION	= "0.01";
	
	private static Logger marketMonarchLogger = LogManager.getLogger(MarketMonarch.class.getName());
	
	public static void main(String[] args) {
		try {
			marketMonarchLogger.info("Starting " + PROGRAM + " (version " + VERSION + ").");
			// Get DB credentials 
			System.out.println("[DEBUG] Displaying contents of credentials file.");
			System.out.println(DatabaseConnectionEssentials.INSTANCE.getUsername());
			System.out.println(DatabaseConnectionEssentials.INSTANCE.getPassword());
			System.out.println(DatabaseConnectionEssentials.INSTANCE.getUrlTestDB());
			System.out.println(DatabaseConnectionEssentials.INSTANCE.getUrlAPIKey());
			System.out.println(DatabaseConnectionEssentials.INSTANCE.getFinancialData());
			DatabaseConnectionEssentials.INSTANCE.flushDatabaseConnectionEssentials();
			// Query other credentials
			// Make money
		} catch (Throwable t) {
			// Workaround because usage of e will throw exception.
			String stackTrace = ExceptionUtils.getStackTrace(t);
			marketMonarchLogger.error(stackTrace);
		}
	}
}
