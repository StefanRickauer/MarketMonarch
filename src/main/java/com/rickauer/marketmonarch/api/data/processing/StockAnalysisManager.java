package com.rickauer.marketmonarch.api.data.processing;

import java.util.HashMap;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.ta4j.core.Bar;

import com.rickauer.marketmonarch.api.data.processing.trade.TradeEntryScanningState;

public class StockAnalysisManager {

	private static Logger _analysisLogger = LogManager.getLogger(StockAnalysisManager.class.getName());
	
	private Map<Integer, String> _symbolLookupTable;
	private Map<String, StrategyExecutor> _executors;
	
	public StockAnalysisManager() {
		_symbolLookupTable = new HashMap<>();
		_executors = new HashMap<>();
	}
	
	public Map<String, StrategyExecutor> getExecutors() {
		return _executors;
	}
	
	public StrategyExecutor getExecutorBySymbol(String symbol) {
		return _executors.get(symbol);
	}
	
	public Map<Integer, String> getSymbolLookupTable() {
		return _symbolLookupTable;
	}
	
	public String getSymbolById(int requestId) {
		return _symbolLookupTable.get(requestId);
	}
	
	public void handleHistoricalBar(int requestId, Bar bar) {
		String symbol = getSymbolById(requestId);
		StrategyExecutor executor = _executors.get(symbol);
		if (executor != null) {
			executor.onHistoricalBar(bar);
		} else {
			_analysisLogger.error("Could not get symbol for request id: " + requestId);
		}
	}
	
	public void handleNewBar(int requestId, Bar bar) {
		String symbol = getSymbolById(requestId);
		StrategyExecutor executor = _executors.get(symbol);
		if (executor != null) {
			executor.onNewBar(bar);
		} else {
			_analysisLogger.error("Could not get symbol for request id: " + requestId);
		}
	}
}
