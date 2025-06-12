package com.rickauer.marketmonarch.api.data.processing.trade;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.ta4j.core.Bar;
import org.ta4j.core.BarSeries;

import com.ib.client.Contract;
import com.ib.client.Decimal;
import com.ib.client.Order;
import com.ib.client.OrderState;
import com.rickauer.marketmonarch.MarketMonarch;
import com.rickauer.marketmonarch.utils.FileSupplier;

public class SessionStoringState extends TradeState {

	private static Logger _sessionStoringLogger = LogManager.getLogger(SessionStoringState.class.getName());
	
	SessionStoringState(TradeContext context) {
		super(context);
	}

	@Override
	public void onEnter() {
		
		_sessionStoringLogger.info("Entered session storing state.");
		_sessionStoringLogger.info("Saving session metrics.");
		_sessionStoringLogger.info("Notice: Session metrics only contain bars up to the moment an entry was detected!");
		
		try {
			BarSeries history = _context.getStockAnalysisManager().getExecutorBySymbol(_context.getContract().symbol()).getSeries();
			
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
			String fileContent = convertToString(history);
			String sessionFileName = ZonedDateTime.now().format(formatter) + ".csv";
			String sessionFolder = MarketMonarch.SESSION_STORAGE_FOLDER + "\\" +sessionFileName;
			
			FileSupplier.writeFile(sessionFolder, fileContent);
		
			_sessionStoringLogger.info("Saved session metrics to: '" + sessionFolder + "'.");			
		} catch (Exception e) {
			_sessionStoringLogger.error("Failed to save session metrics.");
		}
		
		_context.setState(new TradeInactiveState(_context));
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
	
	private String convertToString(BarSeries series) {
		StringBuilder sb = new StringBuilder();
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
		for (Bar bar : series.getBarData()) {
			sb.append(String.format("%s,%.2f,%.2f,%.2f,%.2f,%.2f\n", 
					bar.getEndTime().format(formatter),
					bar.getOpenPrice().doubleValue(),
					bar.getHighPrice().doubleValue(), 
					bar.getLowPrice().doubleValue(),
					bar.getClosePrice().doubleValue(),
					bar.getVolume().doubleValue()
					));
		}
		return sb.toString();
	}
}
