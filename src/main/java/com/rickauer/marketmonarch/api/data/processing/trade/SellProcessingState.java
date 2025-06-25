package com.rickauer.marketmonarch.api.data.processing.trade;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.ib.client.Contract;
import com.ib.client.Decimal;
import com.ib.client.Order;
import com.ib.client.OrderState;
import com.ib.client.Types.TimeInForce;
import com.rickauer.marketmonarch.api.enums.OrderStatus;
import com.rickauer.marketmonarch.api.enums.OrderTransactionType;
import com.rickauer.marketmonarch.api.enums.TradingOrderType;
import com.rickauer.marketmonarch.utils.StockUtils;

public class SellProcessingState extends TradeState {

	private static Logger _sellProcessingLogger = LogManager.getLogger(SellProcessingState.class.getName());
	
	Object _lock = new Object();
	List<Integer> _orderIds;
	
	SellProcessingState(TradeContext context) {
		super(context);
		_orderIds = new ArrayList<>();
	}

	@Override
	public void onEnter() {
		_sellProcessingLogger.info("Entered sell processing state.");
		
		String timeStamp = StockUtils.getCurrentTimestampAsString();
		String ocaGroup = "oca_group_" + timeStamp;
		String action = OrderTransactionType.SELL.getAction(); 
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
		_orderIds.add(orderId);
		_context.getController().placeOrder(orderId++, _context.getContract(), takeProfitOrder);
		_orderIds.add(orderId);
		_context.getController().placeOrder(orderId, _context.getContract(), stopLossOrder);
		
		_sellProcessingLogger.info("Placed OCA Group Order: " + 
				"\n\t| Group ID: " + ocaGroup + 
				"\n\t| SELL (Take Profit) @ " + _context.getContract().symbol() + 
				"\n\t\t| Volume: " + quantity.toString() + " shares" + 
				"\n\t\t| Limit: " + _context.getTakeProfitLimit() + "$" + 
				"\n\t| SELL (Stop Loss) @ " + _context.getContract().symbol() +
				"\n\t\t| Volume: " + quantity.toString() + " shares" + 
				"\n\t\t| Stop Price: " + _context.getStopLossAuxPrice() + "$" + 
				"\n\t\t| Limit: " + _context.getStopLossLimit() + "$");
		
		synchronized (_lock) {
			try {
				_lock.wait();
			} catch (Exception e) {
				_sellProcessingLogger.error("Error while waiting for lock to be notified.");
			}
		}
		
		_context.setState(new SessionStoringState(_context));
	}

	@Override
	public void processOrderStatus(String msg, int orderId, String status, Decimal filled, Decimal remaining, double avgFillPrice) {
		
		if (_orderIds.contains(orderId)) {
			_sellProcessingLogger.info(msg);			

			if (status.equals(OrderStatus.FILLED.getOrderStatus())) {
				_context.setAverageSellFillPrice(avgFillPrice);

				double plPerShareRaw = (_context.getAverageSellFillPrice() - _context.getAverageBuyFillPrice());
				double plInTotalRaw = ((_context.getAverageSellFillPrice() - _context.getAverageBuyFillPrice()) * _context.getQuantityAsInteger());
				
				double plPerShareRounded = StockUtils.roundPrice(plPerShareRaw);
				double plInTotalRounded = StockUtils.roundPrice(plInTotalRaw);
				
				_sellProcessingLogger.info("Order Filled: " + 
						"\n\t| SELL @ " + _context.getContract().symbol() + 
						"\n\t| Volume: " + _context.getQuantity().toString() + " shares" +
						"\n\t| Average Fill Price (BUY): " + _context.getAverageBuyFillPrice() + "$" + 
						"\n\t| Average Fill Price (SELL): " + _context.getAverageSellFillPrice() + "$" + 
						"\n\t| P&L Per Share: " + plPerShareRounded + "$" +
						"\n\t| P&L In Total: " + plInTotalRounded + "$");
				
				_context.setExitTime(ZonedDateTime.now(ZoneId.of("US/Eastern")).withNano(0));
				
				synchronized (_lock) {
					_lock.notify();
				}
			}
		}
	}

	@Override
	public void processOpenOrder(String msg, int orderId, Contract contract, Order order, OrderState orderState) {
		if (_orderIds.contains(orderId)) {
			_sellProcessingLogger.info(msg);			
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
