package com.rickauer.marketmonarch;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


public class MarketMonarch {

	private static final String PROGRAM	= "MarketMonarch";
	private static final String VERSION	= "0.01";
	
	private static Logger marketMonarchLogger = LogManager.getLogger(MarketMonarch.class);
	
	public static void main(String[] args) {
		try {
			// TODO: Logger is supposed to log to console as well as log file.
			// TODO: Reviese log4j2.xml
			marketMonarchLogger.info("Starting " + PROGRAM + " (version " + VERSION + ")...");
			System.out.println("Starting " + PROGRAM + " (version " + VERSION + ")...");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
}
