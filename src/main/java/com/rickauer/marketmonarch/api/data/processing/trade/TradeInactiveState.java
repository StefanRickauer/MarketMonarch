package com.rickauer.marketmonarch.api.data.processing.trade;

import java.time.ZonedDateTime;

import com.ib.client.Contract;
import com.ib.client.Decimal;
import com.ib.client.Order;
import com.ib.client.OrderState;

public class TradeInactiveState extends TradeState {

	public TradeInactiveState(TradeContext context) {
		super(context);
	}

	@Override
	public void onEnter() 				{	/* intentionally left blank */ }

	@Override
	public void processOrderStatus(String msg, int orderId, String status, Decimal filled, Decimal remaining, double avgFillPrice) 		{	/* intentionally left blank */ }

	@Override
	public void processHistoricalDataEnd(int reqId, String startDateStr, String endDateStr) {	/* intentionally left blank */ }

	@Override
	public void processHistoricalData(int reqId, ZonedDateTime time, double open, double high, double low, double close,
			double volume) {	/* intentionally left blank */ }

	@Override
	public void processRealtimeBar(int reqId, ZonedDateTime time, double open, double high, double low, double close,
			Decimal volume, Decimal wap, int count) {
		// intentionally left blank 
	}

	@Override
	public void processOpenOrder(String msg, int orderId, Contract contract, Order order, OrderState orderState) {
		// intentionally left blank 
	}

}
