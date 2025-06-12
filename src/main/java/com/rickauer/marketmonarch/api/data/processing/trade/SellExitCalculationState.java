package com.rickauer.marketmonarch.api.data.processing.trade;

import java.time.ZonedDateTime;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.ib.client.Contract;
import com.ib.client.Decimal;
import com.ib.client.Order;
import com.ib.client.OrderState;
import com.rickauer.marketmonarch.constants.TradingConstants;
import com.rickauer.marketmonarch.utils.StockUtils;

public class SellExitCalculationState extends TradeState {

	private static Logger _sellExitCalculationLogger = LogManager.getLogger(SellExitCalculationState.class.getName());
	
	SellExitCalculationState(TradeContext context) {
		super(context);
	}

	@Override
	public void onEnter() {
		
		_sellExitCalculationLogger.info("Entered exit calculation state.");
		_sellExitCalculationLogger.info("Calculating exit prices required for next state.");
		
		double averageFillPrice = _context.getAverageBuyFillPrice();
		
		double takeProfit = StockUtils.calculateTargetPrice(averageFillPrice, TradingConstants.TAKE_PROFIT_FACTOR);
		_context.setTakeProfitLimit(takeProfit);
		
		double stopLossAuxPrice = StockUtils.calculateTargetPrice(averageFillPrice, TradingConstants.STOP_LIMIT_STOP_PRICE_FACTOR);
		_context.setStopLossAuxPrice(stopLossAuxPrice);
		
		double stopLossLmtPrice = StockUtils.calculateTargetPrice(averageFillPrice, TradingConstants.STOP_LIMIT_LIMIT_PRICE_FACTOR);
		_context.setStopLossLimit(stopLossLmtPrice);

		_sellExitCalculationLogger.info("Done calculating prices. Changing state.");
		_context.setState(new SellProcessingState(_context));
	}

	@Override
	public void processOrderStatus(String msg, int orderId, String status, Decimal filled, Decimal remaining, double avgFillPrice) {
		// intentionally left blank 
	}

	@Override
	public void processOpenOrder(String msg, int orderId, Contract contract, Order order, OrderState orderState) {
		// intentionally left blank 
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
