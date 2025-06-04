package com.rickauer.marketmonarch.api.data.processing.trade;

import java.time.ZonedDateTime;

import com.ib.client.Bar;
import com.ib.client.Decimal;

public class TradeInactiveState extends TradeMonitorState {

	public TradeInactiveState(TradeMonitorContext context) {
		super(context);
	}

	@Override
	public void onEnter() 				{	/* intentionally left blank */ }

	@Override
	public void processOrderData(String msg, String status, Decimal filled, Decimal remaining, double avgFillPrice) 		{	/* intentionally left blank */ }

	@Override
	public void processHistoricalDataEnd(int reqId, String startDateStr, String endDateStr) {	/* intentionally left blank */ }

	@Override
	public void processHistoricalData(int reqId, ZonedDateTime time, double open, double high, double low, double close,
			double volume) {	/* intentionally left blank */ }

}
