package com.rickauer.marketmonarch.api.data.processing.trade;

import java.time.ZonedDateTime;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.ib.client.Contract;
import com.ib.client.Decimal;
import com.ib.client.Order;
import com.ib.client.OrderState;
import com.ib.client.Types.TimeInForce;
import com.rickauer.marketmonarch.MarketMonarch;
import com.rickauer.marketmonarch.api.enums.OrderStatus;
import com.rickauer.marketmonarch.api.enums.OrderTransactionType;
import com.rickauer.marketmonarch.api.enums.TradingOrderType;
import com.rickauer.marketmonarch.constants.TradingConstants;
import com.rickauer.marketmonarch.utils.StockUtils;

public class BuyProcessingState extends TradeState {

	private static Logger _buyOrderLogger = LogManager.getLogger(BuyProcessingState.class.getName());
	
	Object _lock;
	
	BuyProcessingState(TradeContext context) {
		super(context);
		_lock = new Object();
	}

	@Override
	public void onEnter() {
		_buyOrderLogger.info("Entered trade buy order processing state.");
		
		Order order = new Order();
		
		String action = OrderTransactionType.BUY.getAction();
		String orderType = TradingOrderType.LMT.getCode();
		
		if (_context.getController().getPortNumber() == TradingConstants.REAL_MONEY_TRADING_PORT_NUMBER) {
			_context.setQuantity(StockUtils.calculateQuantity(MarketMonarch._preTradeContext.getTotalCash(), MarketMonarch._tradingContext.getEntryPrice()));			
		} else {
			_context.setQuantity(StockUtils.calculateQuantity(1184.92, MarketMonarch._tradingContext.getEntryPrice()));						
		}
		
		Decimal quantity = _context.getQuantity(); 
		double limitPrice = _context.getEntryPrice();
		
		order.action(action);
		order.orderType(orderType);
		order.totalQuantity(quantity);
		order.lmtPrice(limitPrice);
		order.tif(TimeInForce.GTC);
		
		_buyOrderLogger.info("Trying to place order. Symbol: " + _context.getContract().symbol() + "$, Volume: " + quantity + " Shares, Limit Price (= Detected Entry + Buffer): " + limitPrice + "$");
		
		int orderId = _context.getController().getOrderId();
		_context.getController().getSocket().placeOrder(orderId, _context.getContract(), order);
		
		synchronized(_lock) {
			try {
				_lock.wait();
			} catch (InterruptedException e) {
				_buyOrderLogger.error("Error waiting for lock to be released.");
			}
		}
		_buyOrderLogger.info("Placed order: " + 
				"\n\t| BUY @ " + _context.getContract().symbol() + 
				"\n\t| Volume: " + _context.getQuantity().toString() + " shares" + 
				"\n\t| Average Fill Price: " + _context.getAverageBuyFillPrice() + "$" +
				"\n\t| Total Investment: " + (Double.parseDouble(quantity.toString()) * _context.getAverageBuyFillPrice()) + "$");
		
		_context.setState(new SellExitCalculationState(_context));
	}

	@Override
	public void processOrderStatus(String msg, int orderId, String status, Decimal filled, Decimal remaining, double avgFillPrice) {
		
		_buyOrderLogger.info(msg);
		
		if (status.equals(OrderStatus.FILLED.getOrderStatus())) {
			_context.setAverageBuyFillPrice(avgFillPrice);
			synchronized (_lock) {
				_lock.notify();
			}
		}
	}

	@Override
	public void processOpenOrder(String msg, int orderId, Contract contract, Order order, OrderState orderState) {
		_buyOrderLogger.info(msg);
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
