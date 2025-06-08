package com.rickauer.marketmonarch.api.data.processing.trade;

import java.time.ZonedDateTime;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.ib.client.Bar;
import com.ib.client.Decimal;
import com.ib.client.Order;
import com.ib.client.Types.TimeInForce;
import com.rickauer.marketmonarch.MarketMonarch;
import com.rickauer.marketmonarch.api.enums.OrderStatus;
import com.rickauer.marketmonarch.api.enums.OrderTransactionType;
import com.rickauer.marketmonarch.api.enums.TradingOrderType;
import com.rickauer.marketmonarch.utils.StockUtils;

public class TradeBuyProcessingState extends TradeMonitorState {

	private static Logger _buyOrderLogger = LogManager.getLogger(TradeBuyProcessingState.class.getName());
	
	Object _lock;
	
	TradeBuyProcessingState(TradeMonitorContext context) {
		super(context);
		_lock = new Object();
	}

	@Override
	public void onEnter() {
		_buyOrderLogger.info("Entered trade buy order processing state.");
		_buyOrderLogger.info("Placing order...");
		
		Order order = new Order();
		
		String action = OrderTransactionType.BUY.getAction();
		String orderType = TradingOrderType.LMT.getCode();
		
		_context.setQuantity(StockUtils.calculateQuantity(MarketMonarch._preTradeContext.getTotalCash(), MarketMonarch._tradingContext.getEntryPrice()));
		Decimal quantity = _context.getQuantity(); 
		double limitPrice = _context.getEntryPrice();
		
		order.action(action);
		order.orderType(orderType);
		order.totalQuantity(quantity);
		order.lmtPrice(limitPrice);
		order.tif(TimeInForce.GTC);
		
		int orderId = _context.getController().getOrderId();
		
		_context.getController().getSocket().placeOrder(orderId, _context.getContract(), order);
		
		synchronized(_lock) {
			try {
				_lock.wait();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		_buyOrderLogger.info(String.format("Placed order: BUY   @ %s | Durchschn. Ausführungspreis: %.2f "), _context.getContract().symbol(), _context.getAverageFillPrice());
		_context.setState(new TradeSellProcessingState(_context));
	}

	@Override
	public void processOrderStatus(String msg, String status, Decimal filled, Decimal remaining, double avgFillPrice) {
		
		_buyOrderLogger.info(msg);
		
		if (status.equals(OrderStatus.FILLED.getOrderStatus())) {
			_context.setAverageFillPrice(avgFillPrice);
			synchronized (_lock) {
				_lock.notify();
			}
		}
	}

	@Override
	public void processHistoricalData(int reqId, ZonedDateTime time, double open, double high, double low, double close,
			double volume) {
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
