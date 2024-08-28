package com.rickauer.marketmonarch;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.commons.lang3.exception.*;

public class MarketMonarch {

	private static final String PROGRAM	= "MarketMonarch";
	private static final String VERSION	= "0.01";
	
	private static Logger marketMonarchLogger = LogManager.getLogger(MarketMonarch.class);
	
	public static void main(String[] args) {
		try {
			// TODO: Reviese log4j2.xml
			marketMonarchLogger.info("Starting " + PROGRAM + " (version " + VERSION + ")...");
			test();
		} catch (Exception e) {
			// Workaround because usage of e will throw exception.
			String stackTrace = ExceptionUtils.getStackTrace(e);
			marketMonarchLogger.error(stackTrace);
		}
	}
	
	private static void test() {
		int a = 5 / 0;
	}
}
