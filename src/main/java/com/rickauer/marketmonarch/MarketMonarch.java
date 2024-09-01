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
			DatabaseConnectionEssentials.INSTANCE.getUsername();
			DatabaseConnectionEssentials.INSTANCE.getPassword();
		} catch (Throwable t) {
			// Workaround because usage of e will throw exception.
			String stackTrace = ExceptionUtils.getStackTrace(t);
			marketMonarchLogger.error(stackTrace);
		}
	}
}
