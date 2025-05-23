package com.rickauer.marketmonarch.api.data.processing;

import com.ib.client.Contract;
import com.ib.client.Decimal;
import com.rickauer.marketmonarch.api.controller.InteractiveBrokersApiController;

public class TradeMonitor {
	; // context class: https://www.youtube.com/watch?v=abX4xzaAsoc , 2:52
	TradeMonitorState _state;
	private final InteractiveBrokersApiController _controller;
	Contract _tradedContract;
	double _stopLossLimit;
	double _stopLossAuxPrice;
	double _takeProfitLimit;
	long _quantity;
	
	; // Must be MarketMonarchs Controller!
	public TradeMonitor(InteractiveBrokersApiController controller) {
		_state = new TradeInactiveState(this);
		_controller = controller;
	}
	
	public InteractiveBrokersApiController getController() {
		return _controller;
	}
	
	public TradeMonitorState getState() {
		return _state;
	}
	
	public void setState(TradeMonitorState state) {
		_state = state;
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
