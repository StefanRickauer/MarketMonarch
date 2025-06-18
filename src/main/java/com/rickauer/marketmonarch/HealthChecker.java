package com.rickauer.marketmonarch;

import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


import com.rickauer.marketmonarch.utils.IsCoreTypeCheckVisitor;
import com.rickauer.marketmonarch.utils.OperationalCheckVisitor;
import com.rickauer.marketmonarch.utils.Verifyable;

public final class HealthChecker {
	
	private static Logger _healthCheckerLogger = LogManager.getLogger(HealthChecker.class.getName()); 
	private List<Verifyable> _typesToCheck;
	private List<Result> _results;
	
	public HealthChecker() {
		_typesToCheck = new ArrayList<>();
		_results = new ArrayList<>();
	}
	
	public void add(Verifyable verifyable) {
		_typesToCheck.add(verifyable);
	}
	
	public void clear() {
		_typesToCheck.clear();
	}
	
	public void runHealthCheck() {
		
		OperationalCheckVisitor opCheckVisitor = new OperationalCheckVisitor();
		IsCoreTypeCheckVisitor coreCheckVisitor = new IsCoreTypeCheckVisitor();
		
		for (Verifyable element : _typesToCheck) {
			_healthCheckerLogger.info("Running health check for: " + element.getClass().getCanonicalName());

			element.accept(opCheckVisitor);
			element.accept(coreCheckVisitor);
			
			_results.add(new Result(element, opCheckVisitor.getOperational(), coreCheckVisitor.getCoreType()));
		}
	}
	
	public void analyseCheckResults() {
		
		boolean isOperational = true;
		
		for (Result result : _results) {
			if ( (!result._isOperational) && result._isCoreType ) {
				_healthCheckerLogger.error("Discovered fatal error in core type: " + result._testSubject.getClass().getCanonicalName());
				isOperational = false;
			}
			
			if (!result._isOperational) {
				_healthCheckerLogger.error("Discovered error in: " + result._testSubject.getClass().getCanonicalName());
			}
		}
		
		if (!isOperational) {
			throw new RuntimeException("Discovered fatal error in core type(s). Check log for more information.");
		}
		_healthCheckerLogger.info("All checked types are operational.");
	}
	
	public boolean isTypeOperational(Verifyable verifyable) {
		for (Result result : _results) {
			if (result._testSubject.equals(verifyable))
				return result._isOperational;
		}
		return false;
	}
	
	private class Result {
		Verifyable _testSubject;
		boolean _isOperational, _isCoreType;
		
		public Result(Verifyable testSubject, boolean isOperational, boolean isCoreType) {
			_testSubject = testSubject;
			_isOperational = isOperational;
			_isCoreType = isCoreType;
		}		
	}
}
