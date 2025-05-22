package com.rickauer.marketmonarch.api.controller;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.ib.client.Contract;
import com.ib.client.EClientSocket;
import com.ib.client.EReader;
import com.ib.client.Order;
import com.ib.client.ScannerSubscription;
import com.ib.client.TagValue;
import com.rickauer.marketmonarch.MarketMonarch;
import com.rickauer.marketmonarch.api.data.CandleSeries;
import com.rickauer.marketmonarch.api.data.StockMetrics;
import com.rickauer.marketmonarch.api.request.InteractiveBrokersApiRequestHandler;
import com.rickauer.marketmonarch.api.response.ScannerResponse;
import com.rickauer.marketmonarch.utils.Verifyable;
import com.rickauer.marketmonarch.utils.Visitor;

import static com.rickauer.marketmonarch.MarketMonarch.PROGRAM;

import java.util.LinkedList;
import java.util.List;

public final class InteractiveBrokersApiController implements Verifyable {

	private static Logger _ibApiControllerLogger = LogManager
			.getLogger(InteractiveBrokersApiController.class.getName());

	private InteractiveBrokersApiRequestHandler _requestHandler;
	private String _host;
	private int _port;

	public InteractiveBrokersApiController(ScannerResponse result) {
		// Real money trading -> port 4001
		// Paper money trading (simulation) -> port 4002
		this("127.0.0.1", 4002, result);
	}

	public InteractiveBrokersApiController(int port, ScannerResponse result) {
		// Real money trading -> port 4001
		// Paper money trading (simulation) -> port 4002
		this("127.0.0.1", port, result);
	}

	public InteractiveBrokersApiController(boolean isSimulatedTrading, ScannerResponse result) {
		// Real money trading -> port 4001
		// Paper money trading (simulation) -> port 4002
		this("127.0.0.1", isSimulatedTrading ? 4002 : 4001, result);
	}

	private InteractiveBrokersApiController(String host, int port, ScannerResponse result) {
		_requestHandler = new InteractiveBrokersApiRequestHandler(result);
		_host = host;
		_port = port;

		_requestHandler.getClientSocket().eConnect(_host, _port, 2);

		_ibApiControllerLogger.info("Connection established. Host=" + _host + ", port=" + _port + ".");

		final EReader reader = new EReader(_requestHandler.getClientSocket(), _requestHandler.getReaderSignal());
		reader.start();
		new Thread(() -> {
			Thread.currentThread().setName(PROGRAM + " -> Request Handler Thread");
			while (_requestHandler.getClientSocket().isConnected()) {
				_requestHandler.getReaderSignal().waitForSignal();
				try {
					reader.processMsgs();
				} catch (Exception e) {
					_ibApiControllerLogger.error(e.getMessage());
				}
			}
		}).start();
	}

	public int getRequestId() {
		return _requestHandler.getRequestId();
	}
	
	public int getNextRequestId() {
		return _requestHandler.getNextRequestId();
	}
	
	public int getOrderId() {
		return _requestHandler.getCurrentOrderId();
	}

	public EClientSocket getSocket() {
		return _requestHandler.getClientSocket();
	}
	
	// Maximum number of requests is 3 -> Error occurred during test when 4 requests were sent. 
	public void requestAccountSummaryItem(String tag) {
		
		_ibApiControllerLogger.info("Account summary item '" + tag + "' is being requested...");
		
		String group = "All";		
		int requestId = 0;
		
		synchronized (MarketMonarch._accountSummary) {
			try {
				requestId = getNextRequestId();
				getSocket().reqAccountSummary(requestId, group, tag);
				MarketMonarch._accountSummary.wait();
				_ibApiControllerLogger.info("Retrieved account summary item '" + tag + "'.");
			} catch (InterruptedException e) {
				_ibApiControllerLogger.error("Account summary item '" + tag + "' could not be retrieved.");
				throw new RuntimeException("Account summary item '" + tag + "' could not be retrieved.");
			}
		}
		
	}

	public void requestScannerSubscription(String priceAbove, String priceBelow) {
		
		int requestId = getNextRequestId();

		ScannerSubscription subscription = new ScannerSubscription();
		subscription.instrument("STK");
		subscription.locationCode("STK.US.MAJOR");
		subscription.scanCode("TOP_PERC_GAIN");
		
		List<TagValue> filterTagValues = new LinkedList<>();
		filterTagValues.add(new TagValue("priceAbove", priceAbove));
		filterTagValues.add(new TagValue("priceBelow", priceBelow));
		
		_ibApiControllerLogger.info("Requesting market scanner subscription using request id: '" + requestId + "'...");
		getSocket().reqScannerSubscription(requestId, subscription, null, filterTagValues);
	}
	
	public void cancelScannerSubscription(int requestId) {
		getSocket().cancelScannerSubscription(requestId);
		_ibApiControllerLogger.info("Canceled market subscription for request id : " + requestId + "'.");
	}

	public void requestHistoricalDataUntilToday(Contract contract, String lookbackPeriod, String barSizeSetting) {

		int requestId = 0;

		synchronized (MarketMonarch._stocks) {
			try {
				requestId = getNextRequestId();
				MarketMonarch._stocks.put(requestId, new StockMetrics(contract));
				getSocket().reqHistoricalData(requestId, contract, "", lookbackPeriod, barSizeSetting, "TRADES", 1, 1,
						false, null);
				MarketMonarch._stocks.wait();
				_ibApiControllerLogger.info(MarketMonarch._stocks.get(requestId).getSymbol() + ": P&L = "
						+ MarketMonarch._stocks.get(requestId).getProfitLossChange() + ", RVOL = "
						+ MarketMonarch._stocks.get(requestId).getRelativeVolume());
			} catch (InterruptedException e) {
				throw new RuntimeException("Error fetching data.", e);
			}
		}
	}

	; // Refaktorisieren
	public void requestHistoricalDataForAnalysis(Contract contract, String lookbackPeriod, String barSizeSetting) {
		int requestId = 0;

		synchronized (MarketMonarch._stocksToTradeWith) {
			try {
				requestId = getNextRequestId();
				MarketMonarch._stocksToTradeWith.put(requestId, new CandleSeries(contract));
				getSocket().reqHistoricalData(requestId, contract, "", lookbackPeriod, barSizeSetting, "TRADES", 1, 1,
						false, null);
				MarketMonarch._stocksToTradeWith.wait();
				_ibApiControllerLogger.info("Received data for '" + MarketMonarch._stocksToTradeWith.get(requestId).getSymbol() + "' for analysis.");
			} catch (InterruptedException e) {
				throw new RuntimeException("Error fetching data.", e);
			}
		}
	}
	
	public void placeOrder(int id, Contract contract, Order order) {
		getSocket().placeOrder(id, contract, order);
	}
	
	@Override
	public void accept(Visitor visitor) {
		visitor.visit(this);
	}

	public boolean isOperational() {
		getSocket().reqIds(-1);
		return (getOrderId() != -1);
	}
}
