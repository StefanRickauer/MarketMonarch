package com.rickauer.marketmonarch.api.data.processing.trade;

import java.io.IOException;
import java.time.Duration;
import java.time.ZonedDateTime;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.ta4j.core.Bar;
import org.ta4j.core.BarSeries;
import org.ta4j.core.BaseBar;
import org.ta4j.core.BaseBarSeriesBuilder;
import org.ta4j.core.num.DecimalNum;

import com.ib.client.Contract;
import com.ib.client.Decimal;
import com.ib.client.Order;
import com.ib.client.OrderState;
import com.rickauer.marketmonarch.MarketMonarch;
import com.rickauer.marketmonarch.constants.TradingConstants;
import com.rickauer.marketmonarch.db.data.TradeDto;
import com.rickauer.marketmonarch.db.data.TradeReportDto;
import com.rickauer.marketmonarch.reporting.LineChartCreator;
import com.rickauer.marketmonarch.reporting.ReportPdfCreator;

public class SessionNotificationState extends TradeState{
	
	private static Logger _sessionNotificationLogger = LogManager.getLogger(SessionNotificationState.class.getName());
	
	TradeDto _sessionData;
	TradeReportDto _tradeReportData;
	BarSeries _series;
	Object _lock; 

	SessionNotificationState(TradeContext context, TradeDto sessionData) {
		super(context);
		_sessionData = sessionData;
		_lock = new Object();
		_series = new BaseBarSeriesBuilder()
				.withNumTypeOf(DecimalNum::valueOf)
				.build();
		_tradeReportData = null;
	}

	@Override
	public void onEnter() {
		_sessionNotificationLogger.info("Entered session notification state.");
		_sessionNotificationLogger.info("Requesting historical data in order to create line chart.");
		
		_tradeReportData = new TradeReportDto(MarketMonarch._finAccess, _sessionData);
		
		_context.getController().getSocket().reqHistoricalData(
				 _context.getController().getNextRequestId(), 
				_context.getContract(),
				TradingConstants.END_DATE_TIME_UNTIL_NOW, 
				TradingConstants.LOOKBACK_PERIOD_SIX_HOURS,
				TradingConstants.BARSIZE_SETTING_FIVE_SECONDS, 
				TradingConstants.SHOW_TRADES,
				TradingConstants.USE_REGULAR_TRADING_HOUR_DATA_INTEGER, 
				TradingConstants.FORMAT_DATE,
				TradingConstants.KEEP_UP_TO_DATE, 
				null
				);
		try {
			_lock.wait(TradingConstants.FIVE_MINUTES_TIMEOUT_MS);
		} catch (InterruptedException e) {
			_sessionNotificationLogger.error("Error waiting for lock to be released.");
		}
		if (!_hasReceivedApiResponse) {
			_sessionNotificationLogger.warn("Did not fetch historical Data. Line chart won't be created.");
		} else {
			LineChartCreator.createLineGraphAndSaveFile(_series, _context.getStopLossAuxPrice());
		}
		
		try {
			ReportPdfCreator.createSessionReport(_tradeReportData, LineChartCreator.LINECHART);
		} catch (IOException e) {
			_sessionNotificationLogger.error("Could not create PDF report.");
		}
		
		;; // send mail
		
		_context.setState(new TradeInactiveState(_context));
	}

	@Override
	public void processOrderStatus(String msg, int orderId, String status, Decimal filled, Decimal remaining,
			double avgFillPrice) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void processOpenOrder(String msg, int orderId, Contract contract, Order order, OrderState orderState) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void processHistoricalData(int reqId, ZonedDateTime time, double open, double high, double low, double close, double volume) {
		
		Bar baseBar = new BaseBar(
				Duration.ofMillis(5), 
				time, 
				DecimalNum.valueOf(open), 
				DecimalNum.valueOf(high),
				DecimalNum.valueOf(low), 
				DecimalNum.valueOf(close), 
				DecimalNum.valueOf(volume),
				DecimalNum.valueOf(0));
		
		_series.addBar(baseBar);
	}

	@Override
	public void processHistoricalDataEnd(int reqId, String startDateStr, String endDateStr) {
		synchronized (_lock) {
			_hasReceivedApiResponse = true;
			_lock.notify();
		}
	}

	@Override
	public void processRealtimeBar(int reqId, ZonedDateTime time, double open, double high, double low, double close,
			Decimal volume, Decimal wap, int count) {
		// TODO Auto-generated method stub
		
	}

}
