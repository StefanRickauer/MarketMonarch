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
import com.rickauer.marketmonarch.api.response.ScannerResponse;
import com.rickauer.marketmonarch.configuration.ConfigReader;
import com.rickauer.marketmonarch.configuration.FileSupplier;
import com.rickauer.marketmonarch.data.CandleStick;
import com.rickauer.marketmonarch.data.StockMetrics;
import com.rickauer.marketmonarch.db.ApiKeyAccess;
import com.rickauer.marketmonarch.db.FinancialDataAccess;
import com.rickauer.marketmonarch.reporting.LineChartCreator;

import java.awt.Desktop;
import java.io.File;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.exception.*;

public final class MarketMonarch {

	private static final String PROGRAM = "MarketMonarch";
	private static final String VERSION = "0.1";

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

	static {
		_sharedLock = new Object();
		_responses = new ScannerResponse(_sharedLock);
		_stocks = new HashMap<>();
		_sharedFloatBySymbol = new HashMap<>();

		_ibController = new InteractiveBrokersApiController(_responses);

		ConfigReader.INSTANCE.initializeConfigReader();

		_apiAccess = new ApiKeyAccess(ConfigReader.INSTANCE.getUrlAPIKey(), ConfigReader.INSTANCE.getUsername(), ConfigReader.INSTANCE.getPassword());
		_finAccess = new FinancialDataAccess(ConfigReader.INSTANCE.getUrlAPIKey(), ConfigReader.INSTANCE.getUsername(), ConfigReader.INSTANCE.getPassword());
		_mailtrapService = new MailtrapServiceConnector("mailtrap", _apiAccess.executeSqlQueryAndGetFirstResultAsString("SELECT token FROM credentials where provider = 'mailtrap'", "token"));
		_fmpConnector = new FmpConnector("fmp", _apiAccess.executeSqlQueryAndGetFirstResultAsString("SELECT token FROM credentials where provider = 'FMP'", "token"));
		_fmpController = new FmpRequestController(_fmpConnector.getToken());
		_alphaVantage = new AlphaVantageConnector("alphavantageapi", _apiAccess.executeSqlQueryAndGetFirstResultAsString("SELECT token FROM credentials where provider = 'alphavantage'", "token"));
		ConfigReader.INSTANCE.flushDatabaseConnectionEssentials();

	}

	public static void main(String[] args) {
		try {
			_marketMonarchLogger.info("Starting " + PROGRAM + " (version " + VERSION + ").");
			ensureOperationalReadiness();
			
			scanMarketAndSaveResult();
			filterByFloat();
			analyseScanResults();
			
			// request company share float; filter out all stocks > 20.000.000 
			// request historical data for all results (5 minute candles) -> iterate over _responses
			// calculate percentage gain; filter out all stocks < 10% gain
			// calculate RVOL for 30 minute intervals
			
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

	
	private static void scanMarketAndSaveResult() {
	
		_marketMonarchLogger.info("Setting up market scanner and requesting scan results...");
		
		ScannerSubscription subscription = new ScannerSubscription();
		subscription.instrument("STK");
		subscription.locationCode("STK.US.MAJOR");
		subscription.scanCode("TOP_PERC_GAIN");
		List<TagValue> filterTagValues = new LinkedList<>();
		filterTagValues.add(new TagValue("priceAbove", "2"));
		filterTagValues.add(new TagValue("priceBelow", "20"));
		_ibController.getSocket().reqScannerSubscription(_ibController.getRequestId(), subscription, null, filterTagValues);

		synchronized (_sharedLock) {
			try {
				_sharedLock.wait();
			} catch (InterruptedException e) {
				throw new RuntimeException("Error scanning market.", e);
			}
		}
		
		_marketMonarchLogger.info("Set up market scanner and received scan results.");

		// DEBUG only!
		for (Map.Entry<Integer, Contract> entry : _responses.getRankings().entrySet()) {
			System.out.println("Rank: " + entry.getKey() + ", Symbol: " + entry.getValue().symbol());
		}
	}
	
	private static void filterByFloat() {
		// iterate over scan results and filter out all stocks whose company share float is above 20.000.000 and below 5.000.000
		
		_marketMonarchLogger.info("Filtering scan results by company share float...");
		
		long floatShares = 0L;
		
		for (Map.Entry<Integer, Contract> entry : _responses.getRankings().entrySet()) {
			floatShares = _fmpController.requestCompanyShareFloat(entry.getValue().symbol());
			
			_sharedFloatBySymbol.put(entry.getValue().symbol(), floatShares); 
			
		}
		
		_responses.getRankings().entrySet()
			.removeIf(entry -> _sharedFloatBySymbol.get(entry.getValue().symbol()) > MAX_NUMBER_OF_SHARES || _sharedFloatBySymbol.get(entry.getValue().symbol()) < MIN_NUMBER_OF_SHARES);
		
		_marketMonarchLogger.info("Done filtering scan results by company share float.");
	}
	
	private static void analyseScanResults() {
		int requestId = 0;
		
		for (Map.Entry<Integer, Contract> entry : _responses.getRankings().entrySet()) {
			
			_marketMonarchLogger.info("Analysing scan results for " + entry.getValue().symbol());

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
		
		// TODO: Revise StockMetrics such that there is no need to call the calculate...-Functions in order to get valid results.
		// TODO: Filer out negative results
		
		for (StockMetrics metric : _stocks.values()) {
			metric.calculateRelativeTradingVolume();
			metric.calculateProfitLossChange();
			System.out.println("Symbol: " + metric.getSymbol() + ", Relative volume: " + metric.getRelativeVolume() + ", Profit loss: " + metric.getProfitLossChange());
		}
	}
}
