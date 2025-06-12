package com.rickauer.marketmonarch.api.data.processing.trade;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.Map;

import org.ta4j.core.BarSeries;

import com.ib.client.Contract;
import com.ib.client.Decimal;
import com.rickauer.marketmonarch.api.controller.InteractiveBrokersApiController;
import com.rickauer.marketmonarch.api.data.StockMetrics;
import com.rickauer.marketmonarch.api.data.processing.StockAnalysisManager;
import com.rickauer.marketmonarch.api.data.processing.StrategyExecutor;

public class TradeContext {
	
	TradeState _state;
	private final InteractiveBrokersApiController _controller;
	private StockAnalysisManager _analysisManager;
	Contract _tradedContract;
	boolean _restartSession;
	double _entryPrice;
	double _quantity;
	double _averageBuyFillPrice;
	double _averageSellFillPrice;
	double _stopLossLimit;
	double _stopLossAuxPrice;
	double _takeProfitLimit;
	ZonedDateTime _entryDetectedAt;
	ZonedDateTime _exitTriggeredAt;
	
	public TradeContext(InteractiveBrokersApiController controller) {
		_controller = controller;
		_analysisManager = new StockAnalysisManager();
		_restartSession = true;
	}
	
	public InteractiveBrokersApiController getController() {
		return _controller;
	}
	
	public TradeState getState() {
		return _state;
	}
	
	public void setState(TradeState newState) {
		_state = newState;
		newState.onEnter();
	}
	
	public StockAnalysisManager getStockAnalysisManager() {
		return _analysisManager;
	}
	
	public Contract getContract() {
		return _tradedContract;
	}
	
	public void setContract(Contract contract) {
		_tradedContract = contract;
	}
	
	public double getEntryPrice() {
		return _entryPrice;
	}
	
	public void setEntryPrice(double entryPrice) {
		_entryPrice = BigDecimal.valueOf(entryPrice).setScale(2, RoundingMode.HALF_DOWN).doubleValue();
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
	
	public int getQuantityAsInteger() {
		return (int)_quantity;
	}

	public double getQuantityAsDouble() {
		return _quantity;
	}
	
	public Decimal getQuantity() {
		return Decimal.get(_quantity);
	}
	
	public void setQuantity(double quan) {
		_quantity = quan;
	}
	
	public void setRestartSession(boolean restartSession) {
		_restartSession = restartSession;
	}
	
	public boolean getRestartSession() {
		return _restartSession;
	}
	
	public void setAverageBuyFillPrice(double fillPrice) {
		_averageBuyFillPrice = fillPrice;
	}
	
	public double getAverageBuyFillPrice() {
		return _averageBuyFillPrice;
	}

	public void setAverageSellFillPrice(double fillPrice) {
		_averageSellFillPrice = fillPrice;
	}
	
	public double getAverageSellFillPrice() {
		return _averageSellFillPrice;
	}
	
	public void setEntryTime(ZonedDateTime time) {
		_entryDetectedAt = time;
	}
	
	public ZonedDateTime getEntryDetected() {
		return _entryDetectedAt;
	}
	
	public void setExitTime(ZonedDateTime time) {
		_exitTriggeredAt = time;
	}
	
	public ZonedDateTime getExitTriggered() {
		return _exitTriggeredAt;
	}
	
}
