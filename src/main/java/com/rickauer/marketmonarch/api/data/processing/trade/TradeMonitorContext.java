package com.rickauer.marketmonarch.api.data.processing.trade;

import java.util.HashMap;
import java.util.Map;

import org.ta4j.core.BarSeries;

import com.ib.client.Contract;
import com.ib.client.Decimal;
import com.rickauer.marketmonarch.api.controller.InteractiveBrokersApiController;
import com.rickauer.marketmonarch.api.data.StockMetrics;
import com.rickauer.marketmonarch.api.data.processing.StockAnalysisManager;
import com.rickauer.marketmonarch.api.data.processing.StrategyExecutor;

public class TradeMonitorContext {
	
	TradeMonitorState _state;
	private final InteractiveBrokersApiController _controller;
	private StockAnalysisManager _analysisManager;
	Contract _tradedContract;
	boolean _restartSession;
	double _stopLossLimit;
	double _stopLossAuxPrice;
	double _takeProfitLimit;
	long _quantity;
	
	public TradeMonitorContext(InteractiveBrokersApiController controller) {
		_controller = controller;
		_analysisManager = new StockAnalysisManager();
		_restartSession = true;
	}
	
	public InteractiveBrokersApiController getController() {
		return _controller;
	}
	
	public TradeMonitorState getState() {
		return _state;
	}
	
	public void setState(TradeMonitorState newState) {
		_state = newState;
		newState.onEnter();
	}
	
	public StockAnalysisManager getStockAnalysisManager() {
		return _analysisManager;
	}
	
	; // rename?
	public Map<String, StrategyExecutor> getHistoricalData() {
		return _analysisManager.getExecutors();
	}
	
	public Contract getContract() {
		return _tradedContract;
	}
	
	public void setContract(Contract contract) {
		_tradedContract = contract;
	}
	
	public double getStopLossLimit() {
		return _stopLossLimit;
	}
	
	public void setStopLossLimit(double lmt) {
		_stopLossLimit = lmt;
	}
	
	public double getStopLossAuxPrice() {
		return _stopLossAuxPrice;
	}
	
	public void setStopLossAuxPrice(double auxPrice) {
		_stopLossAuxPrice = auxPrice;
	}
	
	public double getTakeProfitLimit() {
		return _takeProfitLimit;
	}
	
	public void setTakeProfitLimit(double lmt) {
		_takeProfitLimit = lmt;
	}
	
	public Decimal getQuantity() {
		return Decimal.get(_quantity);
	}
	
	public void setQuantity(long quan) {
		_quantity = quan;
	}
	
	public void setRestartSession(boolean restartSession) {
		_restartSession = restartSession;
	}
	
	public boolean getRestartSession() {
		return _restartSession;
	}
}
