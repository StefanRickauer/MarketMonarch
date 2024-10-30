package com.rickauer.marketmonarch;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


import com.rickauer.marketmonarch.configuration.ConfigReader;

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
	}
}
