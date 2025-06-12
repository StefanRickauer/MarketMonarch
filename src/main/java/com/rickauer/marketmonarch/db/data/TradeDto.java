package com.rickauer.marketmonarch.db.data;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import com.rickauer.marketmonarch.utils.StockUtils;

public class TradeDto {
	private int _id;
	private String _symbol;
	private double _entryPrice;
	private double _exitPrice;
	private int _quantity;
	private LocalDateTime _entryTime;
	private LocalDateTime _exitTime;
	private double _stopLoss;
	private double _takeProfit;
	private double _orderEfficiencyRatio;


	public TradeDto() {
		// default constructor
	}
	
	public TradeDto(String symbol, double entryPrice, double exitPrice, int quantity, LocalDateTime entryTime, LocalDateTime exitTime, double stopLossLimit, double takeProfit) { 
		_symbol = symbol;
		_entryPrice = entryPrice;
		_exitPrice = exitPrice;
		_quantity = quantity;
		_entryTime = entryTime;
		_exitTime = exitTime;
		_stopLoss = stopLossLimit;
		_takeProfit = takeProfit;
	}

	public int getId() {
		return _id;
	}
	
	public void setId(int id) {
		_id = id;
	}
	
	public String getSymbol() {
		return _symbol;
	}
	
	public void setSymbol(String symbol) {
		_symbol = symbol;
	}
	
	public double getEntryPrice() {
		return _entryPrice;
	}
	
	public void setEntryPrice(double price) {
		_entryPrice = price;
	}
	
	public double getExitPrice() {
		return _exitPrice;
	}
	
	public void setExitPrice(double price) {
		_exitPrice = price;
	}
	
	public int getQuantity() {
		return _quantity;
	}
	
	public void setQuantity(int quantity) {
		_quantity = quantity;
	}
	
	public LocalDateTime getEntryTime() {
		return _entryTime;
	}
	
	public void setEntryTime(LocalDateTime time) {
		_entryTime = time;
	}
	
	public void setEntryTime(String time) {
		_entryTime = StockUtils.stringToLocalDateTime(time);
	}
	
	public LocalDateTime getExitTime() {
		return _exitTime;
	}
	
	public void setExitTime(LocalDateTime time) {
		_exitTime = time;
	}
	
	public void setExitTime(String time) {
		_exitTime = StockUtils.stringToLocalDateTime(time);
	}
	
	public double getStopLoss() {
		return _stopLoss;
	}
	
	public void setStopLoss(double stopLoss) {
		_stopLoss = stopLoss;
	}
	
	public double getTakeProfit() {
		return _takeProfit;
	}
	
	public void setTakeProfit(double takeProfit) {
		_takeProfit = takeProfit;
	}
	
	public double getOrderEfficiencyRatio() {
		return _orderEfficiencyRatio;
	}
	
	public void setOrderEfficiencyRatio(double orderEfficiencyRatio) {
		_orderEfficiencyRatio = orderEfficiencyRatio;
	}
	
	@Override
	public String toString() {
		return String.format("id=%d, symbol=%s, buyOrderID=%d, sellOrderId=%d, entryPrice=%f, exitPrice=%f, entryTime=%s, exitTime=%s, stopLoss=%f, takeProfit=%f, orderEfficiencyRatio=%f", 
				_id, _symbol, _entryPrice, _exitPrice, _entryTime, _exitTime, _stopLoss, _takeProfit, _orderEfficiencyRatio);
	}
}
