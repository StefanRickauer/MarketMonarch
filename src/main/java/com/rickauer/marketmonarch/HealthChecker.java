package com.rickauer.marketmonarch;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


import com.rickauer.marketmonarch.configuration.ConfigReader;
import com.rickauer.marketmonarch.db.ApiKeyAccess;
import com.rickauer.marketmonarch.db.FinancialDataAccess;
import com.rickauer.marketmonarch.utils.IsCoreTypeCheckVisitor;
import com.rickauer.marketmonarch.utils.OperationalCheckVisitor;
import com.rickauer.marketmonarch.utils.Verifyable;
import com.rickauer.marketmonarch.utils.Visitor;

public class HealthChecker {
	
	private static Logger healthCheckerLogger = LogManager.getLogger(HealthChecker.class.getName()); 
	private List<Verifyable> typesToCheck;
	
	public HealthChecker() {
		typesToCheck = new ArrayList<>();
	}
	
	public void add(Verifyable verifyable) {
		typesToCheck.add(verifyable);
	}
	
	public void runHealthCheck() {
		
		OperationalCheckVisitor opCheckVisitor = new OperationalCheckVisitor();
		IsCoreTypeCheckVisitor coreCheckVisitor = new IsCoreTypeCheckVisitor();
		
		for (Verifyable element : typesToCheck) {
			element.accept(opCheckVisitor);
			element.accept(coreCheckVisitor);
			
			System.out.println("[DEBUG] is operational: " + opCheckVisitor.getOperational());
			System.out.println("[DEBUG] is core type:   " + coreCheckVisitor.getCoreType());
		}
	}
}
