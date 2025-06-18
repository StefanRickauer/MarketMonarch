package com.rickauer.marketmonarch.api.controller;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.ib.client.Contract;
import com.ib.client.EClientSocket;
import com.ib.client.EReader;
import com.ib.client.Order;
import com.rickauer.marketmonarch.api.request.InteractiveBrokersApiRequestHandler;
import com.rickauer.marketmonarch.utils.Verifyable;
import com.rickauer.marketmonarch.utils.Visitor;

import static com.rickauer.marketmonarch.MarketMonarch.PROGRAM;

public final class InteractiveBrokersApiController implements Verifyable {

	private static Logger _ibApiControllerLogger = LogManager
			.getLogger(InteractiveBrokersApiController.class.getName());

	private InteractiveBrokersApiRequestHandler _requestHandler;
	private String _host;
	private int _port;

	public InteractiveBrokersApiController() {
		// Real money trading -> port 4001
		// Paper money trading (simulation) -> port 4002
		this("127.0.0.1", 4002);
	}

	public InteractiveBrokersApiController(int port) {
		// Real money trading -> port 4001
		// Paper money trading (simulation) -> port 4002
		this("127.0.0.1", port);
	}

	public InteractiveBrokersApiController(boolean isSimulatedTrading) {
		// Real money trading -> port 4001
		// Paper money trading (simulation) -> port 4002
		this("127.0.0.1", isSimulatedTrading ? 4002 : 4001);
	}

	private InteractiveBrokersApiController(String host, int port) {
		_requestHandler = new InteractiveBrokersApiRequestHandler();
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

	public int getPortNumber() {
		return _port;
	}
	
	public int getRequestId() {
		return _requestHandler.getRequestId();
	}
	
	public int getNextRequestId() {
		return _requestHandler.getNextRequestId();
	}
	
	public int getOrderId() {
		return _requestHandler.waitForNextOrderId();
	}

	public EClientSocket getSocket() {
		return _requestHandler.getClientSocket();
	}
	
	public void cancelScannerSubscription(int requestId) {
		getSocket().cancelScannerSubscription(requestId);
		_ibApiControllerLogger.info("Canceled market subscription for request id : " + requestId + "'.");
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
