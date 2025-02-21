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
import com.rickauer.marketmonarch.api.controller.InteractiveBrokersApiController;
import com.rickauer.marketmonarch.api.response.ScannerResponse;
import com.rickauer.marketmonarch.configuration.ConfigReader;
import com.rickauer.marketmonarch.configuration.FileSupplier;
import com.rickauer.marketmonarch.db.ApiKeyAccess;
import com.rickauer.marketmonarch.db.FinancialDataAccess;
import com.rickauer.marketmonarch.reporting.LineChartCreator;

import java.awt.Desktop;
import java.io.File;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.exception.*;

public final class MarketMonarch {

	private static final String PROGRAM	= "MarketMonarch";
	private static final String VERSION	= "0.1";

	private static Logger _marketMonarchLogger = LogManager.getLogger(MarketMonarch.class.getName());
	
	private static HealthChecker _healthChecker = new HealthChecker();
	public static ApiKeyAccess _apiAccess;
	private static FinancialDataAccess _finAccess;
	private static FmpConnector _fmp;
	private static AlphaVantageConnector _alphaVantage;
	private static MailtrapServiceConnector _mailtrapService;
	private static InteractiveBrokersApiController _ibController;
	private static Object _sharedLock;
	private static ScannerResponse _responses;

	
	static {
		_sharedLock = new Object();
		_responses = new ScannerResponse(_sharedLock);
		
		_ibController = new InteractiveBrokersApiController(_responses);

		ConfigReader.INSTANCE.initializeConfigReader();
		
		_apiAccess = new ApiKeyAccess(ConfigReader.INSTANCE.getUrlAPIKey(), ConfigReader.INSTANCE.getUsername(), ConfigReader.INSTANCE.getPassword());
		_finAccess = new FinancialDataAccess(ConfigReader.INSTANCE.getUrlAPIKey(), ConfigReader.INSTANCE.getUsername(), ConfigReader.INSTANCE.getPassword());
		_mailtrapService = new MailtrapServiceConnector("mailtrap", _apiAccess.executeSqlQueryAndGetFirstResultAsString("SELECT token FROM credentials where provider = 'mailtrap'", "token"));
		_fmp = new FmpConnector("fmp", _apiAccess.executeSqlQueryAndGetFirstResultAsString("SELECT token FROM credentials where provider = 'FMP'", "token"));
		_alphaVantage = new AlphaVantageConnector("alphavantageapi", _apiAccess.executeSqlQueryAndGetFirstResultAsString("SELECT token FROM credentials where provider = 'alphavantage'", "token"));
		ConfigReader.INSTANCE.flushDatabaseConnectionEssentials();
		
	}
	
	public static void main(String[] args) {
		try {
			_marketMonarchLogger.info("Starting " + PROGRAM + " (version " + VERSION + ").");
			ensureOperationalReadiness();
			
			// Get market scanner and save response
			ScannerSubscription subscription = new ScannerSubscription();
			subscription.instrument("STK");
			subscription.locationCode("STK.US.MAJOR");
			subscription.scanCode("TOP_PERC_GAIN"); 
			List<TagValue> filterTagValues = new LinkedList<>();
			filterTagValues.add(new TagValue("priceAbove", "2"));
			filterTagValues.add(new TagValue("priceBelow", "20"));
			_ibController.getSocket().reqScannerSubscription(_ibController.getRequestId(), subscription, null, filterTagValues);
			
			synchronized(_sharedLock) {
				try {
					_sharedLock.wait();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			
			for (Map.Entry<Integer, Contract> entry : _responses.getRankings().entrySet()) {
				System.out.println("Rank: " + entry.getKey() + ", Symbol: " + entry.getValue().symbol());
			}
			// 
			
			// Query other credentials
			// Make money
			// Docs: Orders are submitted via the EClient.placeOrder method. From the snippet below, note how a variable holding the nextValidId is incremented automatically.
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
		_healthChecker.add(_fmp);
		_healthChecker.add(_alphaVantage);
		_healthChecker.add(_ibController);
		
		_marketMonarchLogger.info("Checking operational readiness...");
		_healthChecker.runHealthCheck();
		_marketMonarchLogger.info("Checked operational readiness.");
		
		_marketMonarchLogger.info("Evaluating check results...");
		_healthChecker.analyseCheckResults();
		_marketMonarchLogger.info("Evaluated check results.");
	}
}
