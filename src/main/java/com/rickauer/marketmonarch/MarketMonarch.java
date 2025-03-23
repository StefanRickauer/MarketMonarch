package com.rickauer.marketmonarch;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.ib.client.Contract;
import com.ib.client.ScannerSubscription;
import com.ib.client.TagValue;
import com.rickauer.marketmonarch.api.connect.AlphaVantageConnector;
import com.rickauer.marketmonarch.api.connect.FmpConnector;
import com.rickauer.marketmonarch.api.connect.MailtrapServiceConnector;
import com.rickauer.marketmonarch.api.connect.StockNewsConnector;
import com.rickauer.marketmonarch.api.controller.FmpRequestController;
import com.rickauer.marketmonarch.api.controller.InteractiveBrokersApiController;
import com.rickauer.marketmonarch.api.enums.FmpServiceRequest;
import com.rickauer.marketmonarch.api.response.ScannerResponse;
import com.rickauer.marketmonarch.configuration.ConfigReader;
import com.rickauer.marketmonarch.configuration.FileSupplier;
import com.rickauer.marketmonarch.data.CandleStick;
import com.rickauer.marketmonarch.data.StockMetrics;
import com.rickauer.marketmonarch.db.ApiKeyAccess;
import com.rickauer.marketmonarch.db.FinancialDataAccess;
import com.rickauer.marketmonarch.reporting.LineChartCreator;
import com.rickauer.marketmonarch.utils.StockUtils;

import java.awt.Desktop;
import java.io.File;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.exception.*;

public final class MarketMonarch {

	public static final String PROGRAM = "MarketMonarch";
	private static final String VERSION = "0.28";

	private static Logger _marketMonarchLogger = LogManager.getLogger(MarketMonarch.class.getName());

	private static int MAX_NUMBER_OF_SHARES = 20_000_000;
	private static int MIN_NUMBER_OF_SHARES = 5_000_000;
	
	private static HealthChecker _healthChecker = new HealthChecker();
	public static ApiKeyAccess _apiAccess;
	private static FinancialDataAccess _finAccess;
	private static FmpConnector _fmpConnector;
	private static FmpRequestController _fmpController;
	private static AlphaVantageConnector _alphaVantage;
	private static MailtrapServiceConnector _mailtrapService;
	private static InteractiveBrokersApiController _ibController;
	private static Object _sharedLock;
	public static ScannerResponse _responses;
	public static Map<Integer, StockMetrics> _stocks;
	private static Map<String, Long> _sharedFloatBySymbol;
	private static String _allCompanyFloats;

	static {
		_sharedLock = new Object();
		_responses = new ScannerResponse(_sharedLock);
		_stocks = new HashMap<>();
		_sharedFloatBySymbol = new HashMap<>();
		_allCompanyFloats = "";

		_ibController = new InteractiveBrokersApiController(_responses);

		ConfigReader.INSTANCE.initializeConfigReader();

		_apiAccess = new ApiKeyAccess(ConfigReader.INSTANCE.getUrlAPIKey(), ConfigReader.INSTANCE.getUsername(), ConfigReader.INSTANCE.getPassword());
		_finAccess = new FinancialDataAccess(ConfigReader.INSTANCE.getUrlAPIKey(), ConfigReader.INSTANCE.getUsername(), ConfigReader.INSTANCE.getPassword());
		_mailtrapService = new MailtrapServiceConnector("mailtrap", _apiAccess.executeSqlQueryAndGetFirstResultAsString("SELECT token FROM credentials where provider = 'mailtrap'", "token"));
		_fmpConnector = new FmpConnector("fmp", _apiAccess.executeSqlQueryAndGetFirstResultAsString("SELECT token FROM credentials where provider = 'FMP'", "token"));
		_fmpController = new FmpRequestController(_fmpConnector.getToken(), FmpServiceRequest.ALL_SHARES_FLOAT);
		_alphaVantage = new AlphaVantageConnector("alphavantageapi", _apiAccess.executeSqlQueryAndGetFirstResultAsString("SELECT token FROM credentials where provider = 'alphavantage'", "token"));
		ConfigReader.INSTANCE.flushDatabaseConnectionEssentials();

	}

	public static void main(String[] args) {
		try {
			Thread.currentThread().setName(PROGRAM + " -> Main Thread");
			_marketMonarchLogger.info("Starting " + PROGRAM + " (version " + VERSION + ").");
			ensureOperationalReadiness();
			
			// TODO: Request account summary and quit execution if account balance is below certain amount!
			// TODO: Add functionality that saves all received float values in separate file an in case there is no connection to load saved values instead or in case there was already a request today! 
			
			getAllCompanyFloats();
			scanMarketAndSaveResult();
			filterByFloat();
			filterByProfitLossAndRVOL();
			addFloatToStock();
			
			// DEBUG ONLY: Remove before going live =======================================
			for (StockMetrics metric : _stocks.values()) {
				System.out.println("[DEBUG] -> Symbol: " + metric.getSymbol() + ", Relative volume: " + metric.getRelativeVolume() + ", Profit loss: " + metric.getProfitLossChange() 
				+ ", Company Share Float: " + _sharedFloatBySymbol.get(metric.getSymbol()));
			}
			// DEBUG ONLY END =============================================================
			
			// Make money
			// Docs: Orders are submitted via the EClient.placeOrder method. From the
			// snippet below, note how a variable holding the nextValidId is incremented
			// automatically.
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

		_healthChecker.add(ConfigReader.INSTANCE);
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

	private static void getAllCompanyFloats() {
		_marketMonarchLogger.info("Requesting all company floats...");
		
		_allCompanyFloats = _fmpController.requestAllShareFloat();
		
		if (_allCompanyFloats.equals("")) {
			_marketMonarchLogger.warn("Received empty response from FMP. Trying to fall back on latest save point...");
		}
		
		_marketMonarchLogger.info("Received all company floats.");
	}
	
	private static void scanMarketAndSaveResult() {
	
		_marketMonarchLogger.info("Setting up market scanner subscription and requesting scan results...");
		
		int requestId = _ibController.getRequestId();
		
		ScannerSubscription subscription = new ScannerSubscription();
		subscription.instrument("STK");
		subscription.locationCode("STK.US.MAJOR");
		subscription.scanCode("TOP_PERC_GAIN");
		List<TagValue> filterTagValues = new LinkedList<>();
		filterTagValues.add(new TagValue("priceAbove", "2"));
		filterTagValues.add(new TagValue("priceBelow", "20"));
		_ibController.getSocket().reqScannerSubscription(requestId, subscription, null, filterTagValues);

		synchronized (_sharedLock) {
			try {
				_sharedLock.wait();
			} catch (InterruptedException e) {
				throw new RuntimeException("Error scanning market.", e);
			}
		}
		_ibController.getSocket().cancelScannerSubscription(requestId);
		_marketMonarchLogger.info("Received scan results.");
	}
	
	private static void filterByFloat() {
		
		_marketMonarchLogger.info("Filtering scan results by company share float...");
		long floatShares = 0L;
		int numberOfStocksBeforeFiltering = _responses.getRankings().size();
		
		for (Map.Entry<Integer, Contract> entry : _responses.getRankings().entrySet()) {
			
			String currentSymbol = entry.getValue().symbol();
			
			if (currentSymbol.contains(" ")) {
				floatShares = StockUtils.filterAllFloatsForSymbol(_allCompanyFloats, currentSymbol.replace(" ", "-"));	// The symbol "GTN A" wasn't found but list contains "GTN-A".
			} else {
				floatShares = StockUtils.filterAllFloatsForSymbol(_allCompanyFloats, currentSymbol);
			}
			
			_sharedFloatBySymbol.put(currentSymbol, floatShares); 
		}
		
		_responses.getRankings().entrySet()
			.removeIf(entry -> _sharedFloatBySymbol.get(entry.getValue().symbol()) > MAX_NUMBER_OF_SHARES || _sharedFloatBySymbol.get(entry.getValue().symbol()) < MIN_NUMBER_OF_SHARES);
		
		_marketMonarchLogger.info("Done filtering scan results by company share float. Removed " + (numberOfStocksBeforeFiltering - _responses.getRankings().size()) + " entries.");
	}
	
	private static void filterByProfitLossAndRVOL() {
		
		_marketMonarchLogger.info("Filtering stocks by profit and loss (P&L) and relative trading volume...");
		int requestId = 0;
		int numberOfStocksBeforeFiltering = _responses.getRankings().size();
		
		for (Map.Entry<Integer, Contract> entry : _responses.getRankings().entrySet()) {
			
			synchronized (_stocks) {
				try {
					requestId = _ibController.getRequestId();
					_stocks.put(requestId, new StockMetrics(entry.getValue()));
					_ibController.getSocket().reqHistoricalData(requestId, entry.getValue(), "", "12 D", "5 mins", "TRADES", 1, 1, false, null);
					_stocks.wait();
				} catch (InterruptedException e) {
					throw new RuntimeException("Error scanning market.", e);
				}
			}
		}
		
		_stocks.entrySet().removeIf(entry -> Math.floor(entry.getValue().getProfitLossChange()) < 10 || Math.floor(entry.getValue().getRelativeVolume()) < 5);
		_marketMonarchLogger.info("Done filtering stocks by profit and loss (P&L) and relative trading volume. Removed " + (numberOfStocksBeforeFiltering - _stocks.size()) + " entries.");
	}
	
	private static void addFloatToStock() {
		for (Map.Entry<Integer, StockMetrics> entry : _stocks.entrySet()) {
			entry.getValue().setCompanyShareFloat(_sharedFloatBySymbol.get(entry.getValue().getSymbol()));
		}
	}
}
