package com.rickauer.marketmonarch.api.data.processing.trade;

import java.time.ZonedDateTime;

import com.ib.client.Bar;
import com.ib.client.Contract;
import com.ib.client.Decimal;
import com.ib.client.Order;
import com.ib.client.OrderState;

public abstract class TradeState {
	
	TradeContext _context;
	boolean _hasReceivedApiResponse;
	
	TradeState(TradeContext context) {
		_context = context;
		_hasReceivedApiResponse = false;
	}
	
	public abstract void onEnter();
	public abstract void processOrderStatus(String msg, String status, Decimal filled, Decimal remaining, double avgFillPrice);
	public abstract void processOpenOrder(String msg, int orderId, Contract contract, Order order, OrderState orderState);
	public abstract void processHistoricalData(int reqId, ZonedDateTime time, double open, double high, double low, double close, double volume); 
	public abstract void processHistoricalDataEnd(int reqId, String startDateStr, String endDateStr);
	public abstract void processRealtimeBar(int reqId, ZonedDateTime time, double open, double high, double low, double close, Decimal volume, Decimal wap, int count);
}
