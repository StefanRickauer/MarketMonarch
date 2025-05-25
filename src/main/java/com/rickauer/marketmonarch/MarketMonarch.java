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
import com.rickauer.marketmonarch.api.data.processing.PreTradeAccountValidationState;
import com.rickauer.marketmonarch.api.data.processing.PreTradeContext;
import com.rickauer.marketmonarch.api.data.processing.TradeMonitorContext;
import com.rickauer.marketmonarch.api.enums.FmpServiceRequest;
import com.rickauer.marketmonarch.api.response.ScannerResponse;
import com.rickauer.marketmonarch.configuration.DatabaseConnector;
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
	private static final String VERSION = "0.5";

	private static Logger _marketMonarchLogger = LogManager.getLogger(MarketMonarch.class.getName());

	private static final String COMPANY_FLOATS_BACKUP_FOLDER= FileSupplier.getBackupFolder() + "\\Company Floats\\";
	
	private static final int MAX_NUMBER_OF_SHARES = 20_000_000;
	private static final int MIN_NUMBER_OF_SHARES = 5_000_000;
	private static final int MINIMUM_ACCOUNT_BALANCE = 500;
	public static final double TAKE_PROFIT_FACTOR = 1.05;				// number * TAKE_PROFIT = 5%
	
	private static HealthChecker _healthChecker = new HealthChecker();
	public static ApiKeyDao _apiAccess;
	private static FinancialDataDao _finAccess;
	private static FmpConnector _fmpConnector;
	private static FmpRequestController _fmpController;
	private static AlphaVantageConnector _alphaVantage;
	private static MailtrapServiceConnector _mailtrapService;
	private static InteractiveBrokersApiController _ibController;
	private static Object _sharedLock;
	public static ScannerResponse _responses;
	public static Map<Integer, StockMetrics> _stocks;					// all Stocks
	private static List<Contract> _contractsToObserve;					// contracts to observe with live data
	public static Map<Integer, CandleSeries> _stocksToTradeWith;		// stocks that are being observed 
	private static Map<String, Long> _allCompanyFloats;
	public static PreTradeContext _preTradeContext;
	public static TradeMonitorContext _tradingContext;

	static {
		_sharedLock = new Object();
		_responses = new ScannerResponse(_sharedLock);
		_stocks = new HashMap<>();
		_contractsToObserve = new ArrayList<>();
		_stocksToTradeWith = new HashMap<>();
		_allCompanyFloats = new HashMap<>();

		_ibController = new InteractiveBrokersApiController(_responses);

		_preTradeContext = new PreTradeContext(_ibController);
		_tradingContext = new TradeMonitorContext(_ibController);

		DatabaseConnector.INSTANCE.initializeDatabaseConnector();

		_apiAccess = new ApiKeyDao(DatabaseConnector.INSTANCE.getUrlAPIKey(), DatabaseConnector.INSTANCE.getUsername(), DatabaseConnector.INSTANCE.getPassword());
		_finAccess = new FinancialDataDao(DatabaseConnector.INSTANCE.getUrlAPIKey(), DatabaseConnector.INSTANCE.getUsername(), DatabaseConnector.INSTANCE.getPassword());
		_mailtrapService = new MailtrapServiceConnector("mailtrap", _apiAccess.executeSqlQueryAndGetFirstResultAsString("SELECT token FROM credentials where provider = 'mailtrap'", "token"));
		_fmpConnector = new FmpConnector("fmp", _apiAccess.executeSqlQueryAndGetFirstResultAsString("SELECT token FROM credentials where provider = 'FMP'", "token"));
		_fmpController = new FmpRequestController(_fmpConnector.getToken(), FmpServiceRequest.ALL_SHARES_FLOAT);
		_alphaVantage = new AlphaVantageConnector("alphavantageapi", _apiAccess.executeSqlQueryAndGetFirstResultAsString("SELECT token FROM credentials where provider = 'alphavantage'", "token"));
		DatabaseConnector.INSTANCE.flushDatabaseConnectionEssentials();

	}

	public static void main(String[] args) {
		try {
			Thread.currentThread().setName(PROGRAM + " -> Main Thread");
			_marketMonarchLogger.info("Starting " + PROGRAM + " (version " + VERSION + ").");
			ensureOperationalReadiness();
			setUpWorkingEnvironment();
			
			_preTradeContext.setState(new PreTradeAccountValidationState(_preTradeContext));
			double balance = 0.0;
			// DEBUG ONLY: Remove before going live =======================================
			for (AccountSummaryItem summary : _preTradeContext.getAccountDetails()) {

				if (summary.getTag().equals("TotalCashValue")) {
					balance = summary.getValueAsDouble();
					_marketMonarchLogger.debug("Total cash: " + balance); 
				}
				
				if (summary.getTag().equals("GrossPositionValue")) {
					_marketMonarchLogger.debug("Total in stocks: " + summary.getValueAsDouble()); 
				}
			}
			// DEBUG ONLY END =============================================================
			
			if ( ( (long)Math.floor(balance) ) < MINIMUM_ACCOUNT_BALANCE) {
				_marketMonarchLogger.fatal("Less than 500 Euros in cash available. Exiting.");
				System.exit(0);
			}
			
			getAllCompanyFreeFloats();
			scanMarket();
			filterScanResultsByFloat();
			requestHistoricalDataAndfilterScanResultsByProfitLoss();
			addFloatToStock();
			
			// DEBUG ONLY: Remove before going live =======================================
			for (StockMetrics metric : _stocks.values()) {
				_marketMonarchLogger.debug("Symbol: " + metric.getSymbol() + ", Relative volume: " + metric.getRelativeVolume() + ", Profit loss: " + metric.getProfitLossChange() 
				+ ", Company Share Float: " + _allCompanyFloats.get(metric.getSymbol()));
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
		_healthChecker.add(_ibController);

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
	
	private static void getAllCompanyFreeFloats() {
		_marketMonarchLogger.info("Requesting all company free floats...");
		
		String companyFloats = "";
		
		DateTime today = DateTime.now();
		DateTimeFormatter formatter = DateTimeFormat.forPattern("yyyyMMdd");
		
		String todaysBackupFileName = COMPANY_FLOATS_BACKUP_FOLDER + today.toString(formatter);
		File todaysBackupFile = new File(todaysBackupFileName);

		if (todaysBackupFile.exists()) {
			companyFloats = FileSupplier.readFile(todaysBackupFileName);
		} else {
			// Latest test returned significantly fewer shares which is why the length of the response will be checked and if the response is incomplete, backup will be used instead.
			try {
				companyFloats = _fmpController.requestAllShareFloat();
				if (companyFloats.length() < 1000000) {
					_marketMonarchLogger.error("Received incomplete response.");
					companyFloats = "";
					throw new RuntimeException("Incomplete response won't be saved.");
				}
				FileSupplier.writeFile(todaysBackupFileName,companyFloats);
				_marketMonarchLogger.info("Saved company floats to '" + todaysBackupFileName + "'.");		
			} catch (Exception e) {
				_marketMonarchLogger.error("Could not fetch data.");
			}
		}
		
		if (companyFloats.equals("")) {
			_marketMonarchLogger.warn("No backups today and received an empty or incomplete response from FMP. Attempting to restore latest save point...");
			
			File backupFolder = new File(COMPANY_FLOATS_BACKUP_FOLDER);
			File[] backupFiles = backupFolder.listFiles(); 
			
			if (backupFiles.length > 0 ) {
				String latestBackup = backupFiles[backupFiles.length - 1].getAbsolutePath();
				companyFloats = FileSupplier.readFile(latestBackup);
				_marketMonarchLogger.info("Loaded latest save file: '" + latestBackup + "'.");
			} else {
				_marketMonarchLogger.fatal("Could not fetch company free floats.");
			}
		}
		 _allCompanyFloats = FmpRequestController.convertResponseToMap(companyFloats);
		_marketMonarchLogger.info("Received all company free floats.");
	}
	
	private static void scanMarket() {
	
		_marketMonarchLogger.info("Setting up market scanner subscription and requesting scan results...");
		
		_ibController.requestScannerSubscription("2", "20");

		synchronized (_sharedLock) {
			try {
				_sharedLock.wait();
			} catch (InterruptedException e) {
				throw new RuntimeException("Error scanning market.", e);
			}
		}
		_ibController.cancelScannerSubscription(_ibController.getRequestId());
		_marketMonarchLogger.info("Received scan results.");
	}
	
	private static void filterScanResultsByFloat() {
		
		_marketMonarchLogger.info("Filtering scan results by company share float...");
		
		Map<String, Long> scanResultCompanyFloat = new HashMap<>();
		
		long floatShares = 0L;
		int numberOfStocksBeforeFiltering = _responses.getRankings().size();
		int failedSearchesCount = 0;
		
		for (Map.Entry<Integer, Contract> entry : _responses.getRankings().entrySet()) {
			
			String currentSymbol = entry.getValue().symbol();
			
			try {
				floatShares = _allCompanyFloats.get(currentSymbol.replace(" ", "-"));		// The symbol "GTN A" wasn't found but list contains "GTN-A".
			} catch (NullPointerException e) {
				floatShares = -1L;
				_marketMonarchLogger.warn("Did not find company share float for symbol: '" + currentSymbol + "'.");
				failedSearchesCount++;
			}
			
			scanResultCompanyFloat.put(currentSymbol, floatShares); 
		}
		
		_responses.getRankings().entrySet()
			.removeIf(entry -> scanResultCompanyFloat.get(entry.getValue().symbol()) > MAX_NUMBER_OF_SHARES || scanResultCompanyFloat.get(entry.getValue().symbol()) < MIN_NUMBER_OF_SHARES);
		
		_marketMonarchLogger.info("Done filtering scan results by company share float. Removed " + (numberOfStocksBeforeFiltering - _responses.getRankings().size()) + " out of " + numberOfStocksBeforeFiltering + " entries. Failed searches in totoal: " + failedSearchesCount);
	}
	
	private static void requestHistoricalDataAndfilterScanResultsByProfitLoss() {
		
		_marketMonarchLogger.info("Filtering stocks by profit and loss (P&L) and relative trading volume...");
		
		int numberOfStocksBeforeFiltering = _responses.getRankings().size();
		
		for (Map.Entry<Integer, Contract> entry : _responses.getRankings().entrySet()) {
			_ibController.requestHistoricalDataUntilToday(entry.getValue(), "4 D", "5 mins");
		}
		
		_stocks.entrySet().removeIf(entry -> Math.floor(entry.getValue().getProfitLossChange()) < 10);
		
		for (Map.Entry<Integer, StockMetrics> filteredResult : _stocks.entrySet()) {
			_contractsToObserve.add(filteredResult.getValue().getContract());
		}
		
		_marketMonarchLogger.info("Done filtering stocks by profit and loss (P&L) and relative trading volume. Removed " + (numberOfStocksBeforeFiltering - _stocks.size()) + " entries.");
	}
	
	private static void addFloatToStock() {
		for (Map.Entry<Integer, StockMetrics> entry : _stocks.entrySet()) {
			
			String symbol = entry.getValue().getSymbol().replace(" ", "-");
			Long floatForSymbol = _allCompanyFloats.get(symbol);
			entry.getValue().setCompanyShareFloat(floatForSymbol);
			
			// replace before getting value
//			entry.getValue().setCompanyShareFloat(_allCompanyFloats.get(entry.getValue().getSymbol()));
		}
	}
	
	public static void requestHistoricalDataForPotentialBuy() {
		
		_marketMonarchLogger.info("Requesting historical chart data to prepare for live entry signal detection...");
		
		_stocksToTradeWith.clear(); 			// this method will iterate over all contracts that need to be observed. Hence, delete before use.
		
		for (Contract contract : _contractsToObserve) {
			_ibController.requestHistoricalDataForAnalysis(contract, "15000 S", "5 secs");		
		}
		
		_marketMonarchLogger.info("Done requesting historical chart data.");
	}
}
