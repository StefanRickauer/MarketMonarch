package com.rickauer.marketmonarch.api.data.processing.trade;

import java.time.ZonedDateTime;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.ib.client.Bar;
import com.ib.client.Decimal;
import com.ib.client.Order;
import com.ib.client.OrderType;
import com.ib.client.Types.TimeInForce;
import com.rickauer.marketmonarch.api.enums.OrderStatus;
import com.rickauer.marketmonarch.api.enums.TradingOrderType;
import com.rickauer.marketmonarch.api.request.InteractiveBrokersApiRequestHandler;
import com.rickauer.marketmonarch.utils.StockUtils;

public class TradeSellProcessingState extends TradeMonitorState {

	private static Logger _tradeStateLogger = LogManager.getLogger(TradeSellProcessingState.class.getName());
	
	TradeSellProcessingState(TradeMonitorContext context) {
		super(context);
	}

	@Override
	public void onEnter() {
		_tradeStateLogger.info("Trading state 'sell processing' set.");
		
		; // berechne stoploss usw. basierend auf averageFillPrice! stoploss muss 10% unter averageFillPrice liegen. Methode schreiben.
		
		String timeStamp = StockUtils.getCurrentTimestampAsString();
		String ocaGroup = "oca_group_" + timeStamp;
		String action = "SELL";
		String orderTypeTakeProfit = TradingOrderType.LMT.getCode();
		String orderTypeStopLoss = TradingOrderType.STPLMT.getCode();
		Decimal quantity = _context.getQuantity();
		
		Order takeProfitOrder = new Order();
		Order stopLossOrder = new Order();
		
		// Take Profit
		takeProfitOrder.action(action);
		takeProfitOrder.orderType(orderTypeTakeProfit);
		takeProfitOrder.totalQuantity(quantity);
		takeProfitOrder.lmtPrice(_context.getTakeProfitLimit());
		takeProfitOrder.tif(TimeInForce.GTC);
		takeProfitOrder.ocaGroup(ocaGroup);
		takeProfitOrder.ocaType(1);
		
		// Stop Loss
		stopLossOrder.action(action);
		stopLossOrder.orderType(orderTypeStopLoss);
		stopLossOrder.totalQuantity(quantity);
		stopLossOrder.auxPrice(_context.getStopLossAuxPrice());
		stopLossOrder.lmtPrice(_context.getStopLossLimit());
		stopLossOrder.ocaGroup(ocaGroup);
		stopLossOrder.ocaType(1);
		
		int orderId = _context.getController().getOrderId();
		
		_context.getController().placeOrder(orderId++, _context.getContract(), takeProfitOrder);
		_context.getController().placeOrder(orderId, _context.getContract(), stopLossOrder);
		
		_tradeStateLogger.info("Placed OCA group order: groupId=" + ocaGroup + ", orders=2 [" + action + " " + quantity.toString() + " " + _context.getContract().symbol() + " @ limit=" + _context.getTakeProfitLimit() + ", "
				+  action + " " + quantity.toString() + " " + _context.getContract().symbol() + " @ " + "stop=" + _context.getStopLossAuxPrice() + " and limit=" + _context.getStopLossLimit());
	}

	@Override
	public void processOrderStatus(String msg, String status, Decimal filled, Decimal remaining, double avgFillPrice) {
		_tradeStateLogger.info(msg);
		
		if (status.equals(OrderStatus.FILLED.getOrderStatus())) {
			; // save data, 
			_context.setState(new TradeInactiveState(_context));
		}
	}

	@Override
	public void processHistoricalData(int reqId, ZonedDateTime time, double open, double high, double low, double close, double volume) {
		// intentionally left blank 		
	}

	@Override
	public void processHistoricalDataEnd(int reqId, String startDateStr, String endDateStr) {
		// intentionally left blank 
	}

	@Override
	public void processRealtimeBar(int reqId, ZonedDateTime time, double open, double high, double low, double close,
			Decimal volume, Decimal wap, int count) {
		// intentionally left blank 
	}
}
