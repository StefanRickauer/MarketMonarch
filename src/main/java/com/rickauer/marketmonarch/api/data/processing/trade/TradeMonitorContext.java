package com.rickauer.marketmonarch.api.data.processing.trade;

import java.util.HashMap;
import java.util.Map;

import org.ta4j.core.BarSeries;

import com.ib.client.Contract;
import com.ib.client.Decimal;
import com.rickauer.marketmonarch.api.controller.InteractiveBrokersApiController;
import com.rickauer.marketmonarch.api.data.StockMetrics;

public class TradeMonitorContext {
	
	TradeMonitorState _state;
	private final InteractiveBrokersApiController _controller;
	private Map<Integer, BarSeries> _historicalData;	
	Contract _tradedContract;
	double _stopLossLimit;
	double _stopLossAuxPrice;
	double _takeProfitLimit;
	long _quantity;
	
	public TradeMonitorContext(InteractiveBrokersApiController controller) {
		_controller = controller;
		_historicalData = new HashMap<>();
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
	
	public Map<Integer, BarSeries> getHistoricalData() {
		return _historicalData;
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
}
