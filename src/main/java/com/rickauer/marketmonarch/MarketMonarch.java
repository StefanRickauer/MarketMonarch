package com.rickauer.marketmonarch;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import com.ib.client.Contract;
import com.ib.client.ScannerSubscription;
import com.ib.client.TagValue;
import com.ib.client.Types.TimeInForce;
import com.ib.controller.AccountSummaryTag;
import com.rickauer.marketmonarch.api.connect.AlphaVantageConnector;
import com.rickauer.marketmonarch.api.connect.FmpConnector;
import com.rickauer.marketmonarch.api.connect.MailtrapServiceConnector;
import com.rickauer.marketmonarch.api.connect.StockNewsConnector;
import com.rickauer.marketmonarch.api.controller.FmpRequestController;
import com.rickauer.marketmonarch.api.controller.InteractiveBrokersApiController;
import com.rickauer.marketmonarch.api.data.AccountSummaryItem;
import com.rickauer.marketmonarch.api.data.CandleSeries;
import com.rickauer.marketmonarch.api.data.CandleStick;
import com.rickauer.marketmonarch.api.data.StockMetrics;
import com.rickauer.marketmonarch.api.data.processing.TradeMonitorContext;
import com.rickauer.marketmonarch.api.data.processing.pretrade.PreTradeAccountValidationState;
import com.rickauer.marketmonarch.api.data.processing.pretrade.PreTradeContext;
import com.rickauer.marketmonarch.api.data.processing.pretrade.PreTradeRequestHistoricalDataState;
import com.rickauer.marketmonarch.api.enums.FmpServiceRequest;
import com.rickauer.marketmonarch.api.response.ScannerResponse;
import com.rickauer.marketmonarch.configuration.DatabaseConnector;
import com.rickauer.marketmonarch.constants.TradingConstants;
import com.rickauer.marketmonarch.db.ApiKeyDao;
import com.rickauer.marketmonarch.db.FinancialDataDao;
import com.rickauer.marketmonarch.reporting.LineChartCreator;
import com.rickauer.marketmonarch.utils.FileSupplier;
import com.rickauer.marketmonarch.utils.StockUtils;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.exception.*;

public final class MarketMonarch {

	public static final String PROGRAM = "MarketMonarch";
	private static final String VERSION = "0.7";

	private static Logger _marketMonarchLogger = LogManager.getLogger(MarketMonarch.class.getName());

	public static final String COMPANY_FLOATS_BACKUP_FOLDER= FileSupplier.getBackupFolder() + "\\Company Floats\\";
	
	private static HealthChecker _healthChecker = new HealthChecker();
	public static ApiKeyDao _apiAccess;
	private static FinancialDataDao _finAccess;
	private static FmpConnector _fmpConnector;
	private static FmpRequestController _financialModellingPrepController;
	private static AlphaVantageConnector _alphaVantage;
	private static MailtrapServiceConnector _mailtrapService;
	private static InteractiveBrokersApiController _interactiveBrokersController;

	public static Map<Integer, CandleSeries> _stocksToTradeWith;		// stocks that are being observed 
	public static PreTradeContext _preTradeContext;
	public static TradeMonitorContext _tradingContext;

	static {
		_stocksToTradeWith = new HashMap<>();

		_interactiveBrokersController = new InteractiveBrokersApiController();


		DatabaseConnector.INSTANCE.initializeDatabaseConnector();

		_apiAccess = new ApiKeyDao(DatabaseConnector.INSTANCE.getUrlAPIKey(), DatabaseConnector.INSTANCE.getUsername(), DatabaseConnector.INSTANCE.getPassword());
		_finAccess = new FinancialDataDao(DatabaseConnector.INSTANCE.getUrlAPIKey(), DatabaseConnector.INSTANCE.getUsername(), DatabaseConnector.INSTANCE.getPassword());
		_mailtrapService = new MailtrapServiceConnector("mailtrap", _apiAccess.executeSqlQueryAndGetFirstResultAsString("SELECT token FROM credentials where provider = 'mailtrap'", "token"));
		_fmpConnector = new FmpConnector("fmp", _apiAccess.executeSqlQueryAndGetFirstResultAsString("SELECT token FROM credentials where provider = 'FMP'", "token"));
		_financialModellingPrepController = new FmpRequestController(_fmpConnector.getToken(), FmpServiceRequest.ALL_SHARES_FLOAT);
		_alphaVantage = new AlphaVantageConnector("alphavantageapi", _apiAccess.executeSqlQueryAndGetFirstResultAsString("SELECT token FROM credentials where provider = 'alphavantage'", "token"));
		DatabaseConnector.INSTANCE.flushDatabaseConnectionEssentials();

		_preTradeContext = new PreTradeContext(_interactiveBrokersController, _financialModellingPrepController);
		_tradingContext = new TradeMonitorContext(_interactiveBrokersController);
	}

	public static void main(String[] args) {
		try {
			Thread.currentThread().setName(PROGRAM + " -> Main Thread");
			_marketMonarchLogger.info("Starting " + PROGRAM + " (version " + VERSION + ").");
			ensureOperationalReadiness();
			setUpWorkingEnvironment();
			
			_preTradeContext.setState(new PreTradeAccountValidationState(_preTradeContext));				
			
			addFloatToStock();
			
			// DEBUG ONLY: Remove before going live =======================================
			for (StockMetrics metric : _preTradeContext.getHistoricalData().values()) {
				_marketMonarchLogger.debug("Symbol: " + metric.getSymbol() + ", Relative volume: " + metric.getRelativeVolume() + ", Profit loss: " + metric.getProfitLossChange() 
				+ ", Company Share Float: " + _preTradeContext.getAllCompanyFloats().get(metric.getSymbol()));
			}
			// DEBUG ONLY END =============================================================
			
			// Make money
			// Docs: Orders are submitted via the EClient.placeOrder method. From the
			// snippet below, note how a variable holding the nextValidId is incremented
			// automatically.
			
			// for each remaining search result
			//		- request 5 sec candles of the past 3 days
			
			requestHistoricalDataForPotentialBuy();
			
			//		- convert these candles to barseries
			//		- request live data for symbol
			//			-- add live candle to bar series
			//			-- analyze bar series
			//			-- if should enter is true
			//				--- stop monitoring other stocks
			//				--- calculate entry price from last close + puffer
			//				--- calculate exit price
			//					---- all prices in orders are of type double
			//				--- calculate total quantity											-> (available money - buffer) / entry price -> floor the result 
			//				--- create order object for BUY 										-> orderType = "LMT" for limit order
			//				--- place order
			//				--- if order is filled													-> 	EWrapper.orderStatus()	will be called every time the status changes:	order.status == "Filled" means order has been placed 
			// 					---- get price														->																			order.avgFillPrice is the average price for each stock
			//					---- calculate desired win and maximum loss for the stock based on the average fill price
			//					---- create new order object for SELL								-> orderType = "STPLMT" for stop limit order
			//					---- keep monitoring live data
			//							----- if actual price > average fill price
			//										------ set stop loss to desired win				-> set auxPrice() and lmtPrice() of the order and place it again
			//							----- if actual price < average fill price	
			//										------ set stop loss to maximum loss			-> set auxPrice() and lmtPrice() of the order and place it again
			// Order example
			//			order.action("BUY");		or "SELL"
			//			order.orderType("LMT");		or "STPLMT"
			//			order.totalQuantity(quantity);
			//			order.lmtPrice(Double);				+ order.auxPrice(Double) if "STPLMT" is used
			//			order.tif(TimeInForce.DAY);			// default
			
			
			// DEBUG ONLY: Remove before going live =======================================
			System.out.println("IMPORTATN NOTICE: Order is not based on greatest profit loss first, which must be changed.");
			for (CandleSeries entry : _stocksToTradeWith.values()) {
				System.out.println("======>  " + entry.getSymbol());
			}
			// DEBUG ONLY END =============================================================
			
		} catch (Throwable t) {
			// Workaround because usage of t will throw exception.
			String stackTrace = ExceptionUtils.getStackTrace(t);
			_marketMonarchLogger.error(stackTrace);
		} finally {
			_apiAccess.close();
			_finAccess.close();
		}
	}

	
	private static void ensureOperationalReadiness() {
		_marketMonarchLogger.info("Preparing for operational readiness check...");

		_healthChecker.add(DatabaseConnector.INSTANCE);
		_healthChecker.add(_apiAccess);
		_healthChecker.add(_finAccess);
		_healthChecker.add(_mailtrapService);
		_healthChecker.add(_fmpConnector);
		_healthChecker.add(_alphaVantage);
		_healthChecker.add(_interactiveBrokersController);

		_marketMonarchLogger.info("Checking operational readiness...");
		_healthChecker.runHealthCheck();
		_marketMonarchLogger.info("Checked operational readiness.");

		_marketMonarchLogger.info("Evaluating check results...");
		_healthChecker.analyseCheckResults();
		_marketMonarchLogger.info("Evaluated check results.");
	}
	
	private static void setUpWorkingEnvironment() {
		_marketMonarchLogger.info("Setting up working environment...");
		
		File companyFloatBackupFolder = new File(COMPANY_FLOATS_BACKUP_FOLDER);
		if (!companyFloatBackupFolder.exists()) {
			companyFloatBackupFolder.mkdir();
			_marketMonarchLogger.info("Created company floats backup folder.");
		}
		
		_marketMonarchLogger.info("Set up environment.");
	}
	
	private static void addFloatToStock() {
		for (Map.Entry<Integer, StockMetrics> entry : _preTradeContext.getHistoricalData().entrySet()) {
			
			String symbol = entry.getValue().getSymbol().replace(" ", "-");			// <====== NICHT VERGESSEN!!!
			Long floatForSymbol = _preTradeContext.getAllCompanyFloats().get(symbol);
			entry.getValue().setCompanyShareFloat(floatForSymbol);
			
			// replace before getting value
//			entry.getValue().setCompanyShareFloat(_allCompanyFloats.get(entry.getValue().getSymbol()));
		}
	}
	
	public static void requestHistoricalDataForPotentialBuy() {
		
		_marketMonarchLogger.info("Requesting historical chart data to prepare for live entry signal detection...");
		
		_stocksToTradeWith.clear(); 			// this method will iterate over all contracts that need to be observed. Hence, delete before use.
		
		for (Map.Entry<Integer, StockMetrics> filteredResult : _preTradeContext.getHistoricalData().entrySet()) {
			_interactiveBrokersController.requestHistoricalDataForAnalysis(
					filteredResult.getValue().getContract(), 
					TradingConstants.LOOKBACK_PERIOD_FOUR_HOURS_TEN_MINUTES_IN_SECONDS, 
					TradingConstants.BARSIZE_SETTING_FIVE_SECONDS);
		}
		
		_marketMonarchLogger.info("Done requesting historical chart data.");
	}
}
