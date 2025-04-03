package com.rickauer.marketmonarch.api.controller;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.ib.client.Contract;
import com.ib.client.EClientSocket;
import com.ib.client.EReader;
import com.ib.client.ScannerSubscription;
import com.ib.client.TagValue;
import com.rickauer.marketmonarch.MarketMonarch;
import com.rickauer.marketmonarch.api.request.InteractiveBrokersApiRequestHandler;
import com.rickauer.marketmonarch.api.response.ScannerResponse;
import com.rickauer.marketmonarch.data.StockMetrics;
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
				throw new RuntimeException("Error scanning market.", e);
			}
		}
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
