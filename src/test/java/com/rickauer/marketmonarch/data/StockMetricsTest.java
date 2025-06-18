package com.rickauer.marketmonarch.data;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import com.ib.client.Contract;
import com.ib.client.Decimal;
import com.rickauer.marketmonarch.api.data.CandleStick;
import com.rickauer.marketmonarch.api.data.StockMetrics;

public class StockMetricsTest {

	private static Contract _contract;
	private static StockMetrics _stockMetrics;
	
	@BeforeAll
	public static void initializeData() {
		_contract = new Contract();
		_contract.symbol("AAPL");
		_contract.secType("STK");
		_contract.currency("USD");
		_contract.exchange("NASDAQ");
		
		_stockMetrics = new StockMetrics(_contract);
		
		createMockCandles();
	}
	
	@Test
	void calculateRelativeTradingVolumeTest() {
		_stockMetrics.calculateRelativeTradingVolume();
		assertEquals(1.0, _stockMetrics.getRelativeVolume());
	}
	
	@Test
	void calculateProfitLossChangeTest() {
		_stockMetrics.calculateProfitLossChange();
		assertEquals(50.0, _stockMetrics.getProfitLossChange());
	}
	
	@Test
	void addCandleStickInvalid() {
		CandleStick invalid = new CandleStick("20250214 16:00:00 US/Eastern", 10.0, 11.0, 11.5, 9.5, Decimal.get(1000));
		assertThrows(IllegalArgumentException.class, () -> {
			_stockMetrics.addCandleStick(invalid);
		});
	}
	
	private static void createMockCandles() {
		_stockMetrics.addCandleStick(new CandleStick("20250214 09:30:00 US/Eastern", 10.0, 11.0, 11.5, 9.5, Decimal.get(1000)));
		_stockMetrics.addCandleStick(new CandleStick("20250214 09:55:00 US/Eastern", 10.0, 11.0, 11.5, 9.5, Decimal.get(1000)));
		_stockMetrics.addCandleStick(new CandleStick("20250214 10:00:00 US/Eastern", 10.0, 11.0, 11.5, 9.5, Decimal.get(2000)));
		_stockMetrics.addCandleStick(new CandleStick("20250214 10:55:00 US/Eastern", 10.0, 11.0, 11.5, 9.5, Decimal.get(2000)));
		_stockMetrics.addCandleStick(new CandleStick("20250214 11:00:00 US/Eastern", 10.0, 11.0, 11.5, 9.5, Decimal.get(3000)));
		_stockMetrics.addCandleStick(new CandleStick("20250214 11:55:00 US/Eastern", 10.0, 11.0, 11.5, 9.5, Decimal.get(3000)));
		_stockMetrics.addCandleStick(new CandleStick("20250214 12:00:00 US/Eastern", 10.0, 11.0, 11.5, 9.5, Decimal.get(4000)));
		_stockMetrics.addCandleStick(new CandleStick("20250214 12:55:00 US/Eastern", 10.0, 11.0, 11.5, 9.5, Decimal.get(4000)));
		_stockMetrics.addCandleStick(new CandleStick("20250214 13:00:00 US/Eastern", 10.0, 11.0, 11.5, 9.5, Decimal.get(5000)));
		_stockMetrics.addCandleStick(new CandleStick("20250214 13:55:00 US/Eastern", 10.0, 11.0, 11.5, 9.5, Decimal.get(5000)));
		_stockMetrics.addCandleStick(new CandleStick("20250214 14:00:00 US/Eastern", 10.0, 11.0, 11.5, 9.5, Decimal.get(6000)));
		_stockMetrics.addCandleStick(new CandleStick("20250214 14:55:00 US/Eastern", 10.0, 11.0, 11.5, 9.5, Decimal.get(6000)));
		_stockMetrics.addCandleStick(new CandleStick("20250214 15:00:00 US/Eastern", 10.0, 11.0, 11.5, 9.5, Decimal.get(7000)));
		_stockMetrics.addCandleStick(new CandleStick("20250214 15:55:00 US/Eastern", 10.0, 11.0, 11.5, 9.5, Decimal.get(7000)));

		// Monday, February 17th: All major stock markets were closed.
		_stockMetrics.addCandleStick(new CandleStick("20250218 09:30:00 US/Eastern", 10.0, 11.0, 11.5, 9.5, Decimal.get(1000)));
		_stockMetrics.addCandleStick(new CandleStick("20250218 09:55:00 US/Eastern", 10.0, 11.0, 11.5, 9.5, Decimal.get(1000)));
		_stockMetrics.addCandleStick(new CandleStick("20250218 10:00:00 US/Eastern", 10.0, 11.0, 11.5, 9.5, Decimal.get(2000)));
		_stockMetrics.addCandleStick(new CandleStick("20250218 10:55:00 US/Eastern", 10.0, 11.0, 11.5, 9.5, Decimal.get(2000)));
		_stockMetrics.addCandleStick(new CandleStick("20250218 11:00:00 US/Eastern", 10.0, 11.0, 11.5, 9.5, Decimal.get(3000)));
		_stockMetrics.addCandleStick(new CandleStick("20250218 11:55:00 US/Eastern", 10.0, 11.0, 11.5, 9.5, Decimal.get(3000)));
		_stockMetrics.addCandleStick(new CandleStick("20250218 12:00:00 US/Eastern", 10.0, 11.0, 11.5, 9.5, Decimal.get(4000)));
		_stockMetrics.addCandleStick(new CandleStick("20250218 12:55:00 US/Eastern", 10.0, 11.0, 11.5, 9.5, Decimal.get(4000)));
		_stockMetrics.addCandleStick(new CandleStick("20250218 13:00:00 US/Eastern", 10.0, 11.0, 11.5, 9.5, Decimal.get(5000)));
		_stockMetrics.addCandleStick(new CandleStick("20250218 13:55:00 US/Eastern", 10.0, 11.0, 11.5, 9.5, Decimal.get(5000)));
		_stockMetrics.addCandleStick(new CandleStick("20250218 14:00:00 US/Eastern", 10.0, 11.0, 11.5, 9.5, Decimal.get(6000)));
		_stockMetrics.addCandleStick(new CandleStick("20250218 14:55:00 US/Eastern", 10.0, 11.0, 11.5, 9.5, Decimal.get(6000)));
		_stockMetrics.addCandleStick(new CandleStick("20250218 15:00:00 US/Eastern", 10.0, 11.0, 11.5, 9.5, Decimal.get(7000)));
		_stockMetrics.addCandleStick(new CandleStick("20250214 15:55:00 US/Eastern", 10.0, 11.0, 11.5, 9.5, Decimal.get(7000)));

		_stockMetrics.addCandleStick(new CandleStick("20250219 09:30:00 US/Eastern", 10.0, 11.0, 11.5, 9.5, Decimal.get(1000)));
		_stockMetrics.addCandleStick(new CandleStick("20250219 09:55:00 US/Eastern", 10.0, 11.0, 11.5, 9.5, Decimal.get(1000)));
		_stockMetrics.addCandleStick(new CandleStick("20250219 10:00:00 US/Eastern", 10.0, 11.0, 11.5, 9.5, Decimal.get(2000)));
		_stockMetrics.addCandleStick(new CandleStick("20250219 10:55:00 US/Eastern", 10.0, 11.0, 11.5, 9.5, Decimal.get(2000)));
		_stockMetrics.addCandleStick(new CandleStick("20250219 11:00:00 US/Eastern", 10.0, 11.0, 11.5, 9.5, Decimal.get(3000)));
		_stockMetrics.addCandleStick(new CandleStick("20250219 11:55:00 US/Eastern", 10.0, 11.0, 11.5, 9.5, Decimal.get(3000)));
		_stockMetrics.addCandleStick(new CandleStick("20250219 12:00:00 US/Eastern", 10.0, 11.0, 11.5, 9.5, Decimal.get(4000)));
		_stockMetrics.addCandleStick(new CandleStick("20250219 12:55:00 US/Eastern", 10.0, 11.0, 11.5, 9.5, Decimal.get(4000)));
		_stockMetrics.addCandleStick(new CandleStick("20250219 13:00:00 US/Eastern", 10.0, 11.0, 11.5, 9.5, Decimal.get(5000)));
		_stockMetrics.addCandleStick(new CandleStick("20250219 13:55:00 US/Eastern", 10.0, 11.0, 11.5, 9.5, Decimal.get(5000)));
		_stockMetrics.addCandleStick(new CandleStick("20250219 14:00:00 US/Eastern", 10.0, 11.0, 11.5, 9.5, Decimal.get(6000)));
		_stockMetrics.addCandleStick(new CandleStick("20250219 14:55:00 US/Eastern", 10.0, 11.0, 11.5, 9.5, Decimal.get(6000)));
		_stockMetrics.addCandleStick(new CandleStick("20250219 15:00:00 US/Eastern", 10.0, 11.0, 11.5, 9.5, Decimal.get(7000)));
		_stockMetrics.addCandleStick(new CandleStick("20250219 15:55:00 US/Eastern", 10.0, 11.0, 11.5, 9.5, Decimal.get(7000)));

		_stockMetrics.addCandleStick(new CandleStick("20250220 09:30:00 US/Eastern", 10.0, 11.0, 11.5, 9.5, Decimal.get(1000)));
		_stockMetrics.addCandleStick(new CandleStick("20250220 09:55:00 US/Eastern", 10.0, 11.0, 11.5, 9.5, Decimal.get(1000)));
		_stockMetrics.addCandleStick(new CandleStick("20250220 10:00:00 US/Eastern", 10.0, 11.0, 11.5, 9.5, Decimal.get(2000)));
		_stockMetrics.addCandleStick(new CandleStick("20250220 10:55:00 US/Eastern", 10.0, 11.0, 11.5, 9.5, Decimal.get(2000)));
		_stockMetrics.addCandleStick(new CandleStick("20250220 11:00:00 US/Eastern", 10.0, 11.0, 11.5, 9.5, Decimal.get(3000)));
		_stockMetrics.addCandleStick(new CandleStick("20250220 11:55:00 US/Eastern", 10.0, 11.0, 11.5, 9.5, Decimal.get(3000)));
		_stockMetrics.addCandleStick(new CandleStick("20250220 12:00:00 US/Eastern", 10.0, 11.0, 11.5, 9.5, Decimal.get(4000)));
		_stockMetrics.addCandleStick(new CandleStick("20250220 12:55:00 US/Eastern", 10.0, 11.0, 11.5, 9.5, Decimal.get(4000)));
		_stockMetrics.addCandleStick(new CandleStick("20250220 13:00:00 US/Eastern", 10.0, 11.0, 11.5, 9.5, Decimal.get(5000)));
		_stockMetrics.addCandleStick(new CandleStick("20250220 13:55:00 US/Eastern", 10.0, 11.0, 11.5, 9.5, Decimal.get(5000)));
		_stockMetrics.addCandleStick(new CandleStick("20250220 14:00:00 US/Eastern", 10.0, 11.0, 11.5, 9.5, Decimal.get(6000)));
		_stockMetrics.addCandleStick(new CandleStick("20250220 14:55:00 US/Eastern", 10.0, 11.0, 11.5, 9.5, Decimal.get(6000)));
		_stockMetrics.addCandleStick(new CandleStick("20250220 15:00:00 US/Eastern", 10.0, 11.0, 11.5, 9.5, Decimal.get(7000)));
		_stockMetrics.addCandleStick(new CandleStick("20250220 15:55:00 US/Eastern", 10.0, 11.0, 11.5, 9.5, Decimal.get(7000)));

		_stockMetrics.addCandleStick(new CandleStick("20250221 09:30:00 US/Eastern", 10.0, 11.0, 11.5, 9.5, Decimal.get(1000)));
		_stockMetrics.addCandleStick(new CandleStick("20250221 09:55:00 US/Eastern", 10.0, 11.0, 11.5, 9.5, Decimal.get(1000)));
		_stockMetrics.addCandleStick(new CandleStick("20250221 10:00:00 US/Eastern", 10.0, 11.0, 11.5, 9.5, Decimal.get(2000)));
		_stockMetrics.addCandleStick(new CandleStick("20250221 10:55:00 US/Eastern", 10.0, 11.0, 11.5, 9.5, Decimal.get(2000)));
		_stockMetrics.addCandleStick(new CandleStick("20250221 11:00:00 US/Eastern", 10.0, 11.0, 11.5, 9.5, Decimal.get(3000)));
		_stockMetrics.addCandleStick(new CandleStick("20250221 11:55:00 US/Eastern", 10.0, 11.0, 11.5, 9.5, Decimal.get(3000)));
		_stockMetrics.addCandleStick(new CandleStick("20250221 12:00:00 US/Eastern", 10.0, 11.0, 11.5, 9.5, Decimal.get(4000)));
		_stockMetrics.addCandleStick(new CandleStick("20250221 12:55:00 US/Eastern", 10.0, 11.0, 11.5, 9.5, Decimal.get(4000)));
		_stockMetrics.addCandleStick(new CandleStick("20250221 13:00:00 US/Eastern", 10.0, 11.0, 11.5, 9.5, Decimal.get(5000)));
		_stockMetrics.addCandleStick(new CandleStick("20250221 13:55:00 US/Eastern", 10.0, 11.0, 11.5, 9.5, Decimal.get(5000)));
		_stockMetrics.addCandleStick(new CandleStick("20250221 14:00:00 US/Eastern", 10.0, 11.0, 11.5, 9.5, Decimal.get(6000)));
		_stockMetrics.addCandleStick(new CandleStick("20250221 14:55:00 US/Eastern", 10.0, 11.0, 11.5, 9.5, Decimal.get(6000)));
		_stockMetrics.addCandleStick(new CandleStick("20250221 15:00:00 US/Eastern", 10.0, 11.0, 11.5, 9.5, Decimal.get(7000)));
		_stockMetrics.addCandleStick(new CandleStick("20250221 15:55:00 US/Eastern", 10.0, 11.0, 11.5, 9.5, Decimal.get(7000)));

		_stockMetrics.addCandleStick(new CandleStick("20250224 09:30:00 US/Eastern", 10.0, 11.0, 11.5, 9.5, Decimal.get(1000)));
		_stockMetrics.addCandleStick(new CandleStick("20250224 09:55:00 US/Eastern", 10.0, 11.0, 11.5, 9.5, Decimal.get(1000)));
		_stockMetrics.addCandleStick(new CandleStick("20250224 10:00:00 US/Eastern", 10.0, 11.0, 11.5, 9.5, Decimal.get(2000)));
		_stockMetrics.addCandleStick(new CandleStick("20250224 10:55:00 US/Eastern", 10.0, 11.0, 11.5, 9.5, Decimal.get(2000)));
		_stockMetrics.addCandleStick(new CandleStick("20250224 11:00:00 US/Eastern", 10.0, 11.0, 11.5, 9.5, Decimal.get(3000)));
		_stockMetrics.addCandleStick(new CandleStick("20250224 11:55:00 US/Eastern", 10.0, 11.0, 11.5, 9.5, Decimal.get(3000)));
		_stockMetrics.addCandleStick(new CandleStick("20250224 12:00:00 US/Eastern", 10.0, 11.0, 11.5, 9.5, Decimal.get(4000)));
		_stockMetrics.addCandleStick(new CandleStick("20250224 12:55:00 US/Eastern", 10.0, 11.0, 11.5, 9.5, Decimal.get(4000)));
		_stockMetrics.addCandleStick(new CandleStick("20250224 13:00:00 US/Eastern", 10.0, 11.0, 11.5, 9.5, Decimal.get(5000)));
		_stockMetrics.addCandleStick(new CandleStick("20250224 13:55:00 US/Eastern", 10.0, 11.0, 11.5, 9.5, Decimal.get(5000)));
		_stockMetrics.addCandleStick(new CandleStick("20250224 14:00:00 US/Eastern", 10.0, 11.0, 11.5, 9.5, Decimal.get(6000)));
		_stockMetrics.addCandleStick(new CandleStick("20250224 14:55:00 US/Eastern", 10.0, 11.0, 11.5, 9.5, Decimal.get(6000)));
		_stockMetrics.addCandleStick(new CandleStick("20250224 15:00:00 US/Eastern", 10.0, 11.0, 11.5, 9.5, Decimal.get(7000)));
		_stockMetrics.addCandleStick(new CandleStick("20250224 15:55:00 US/Eastern", 10.0, 11.0, 11.5, 9.5, Decimal.get(7000)));

		_stockMetrics.addCandleStick(new CandleStick("20250225 09:30:00 US/Eastern", 10.0, 11.0, 11.5, 9.5, Decimal.get(1000)));
		_stockMetrics.addCandleStick(new CandleStick("20250225 09:55:00 US/Eastern", 10.0, 11.0, 11.5, 9.5, Decimal.get(1000)));
		_stockMetrics.addCandleStick(new CandleStick("20250225 10:00:00 US/Eastern", 10.0, 11.0, 11.5, 9.5, Decimal.get(2000)));
		_stockMetrics.addCandleStick(new CandleStick("20250225 10:55:00 US/Eastern", 10.0, 11.0, 11.5, 9.5, Decimal.get(2000)));
		_stockMetrics.addCandleStick(new CandleStick("20250225 11:00:00 US/Eastern", 10.0, 11.0, 11.5, 9.5, Decimal.get(3000)));
		_stockMetrics.addCandleStick(new CandleStick("20250225 11:55:00 US/Eastern", 10.0, 11.0, 11.5, 9.5, Decimal.get(3000)));
		_stockMetrics.addCandleStick(new CandleStick("20250225 12:00:00 US/Eastern", 10.0, 11.0, 11.5, 9.5, Decimal.get(4000)));
		_stockMetrics.addCandleStick(new CandleStick("20250225 12:55:00 US/Eastern", 10.0, 11.0, 11.5, 9.5, Decimal.get(4000)));
		_stockMetrics.addCandleStick(new CandleStick("20250225 13:00:00 US/Eastern", 10.0, 11.0, 11.5, 9.5, Decimal.get(5000)));
		_stockMetrics.addCandleStick(new CandleStick("20250225 13:55:00 US/Eastern", 10.0, 11.0, 11.5, 9.5, Decimal.get(5000)));
		_stockMetrics.addCandleStick(new CandleStick("20250225 14:00:00 US/Eastern", 10.0, 11.0, 11.5, 9.5, Decimal.get(6000)));
		_stockMetrics.addCandleStick(new CandleStick("20250225 14:55:00 US/Eastern", 10.0, 11.0, 11.5, 9.5, Decimal.get(6000)));
		_stockMetrics.addCandleStick(new CandleStick("20250225 15:00:00 US/Eastern", 10.0, 11.0, 11.5, 9.5, Decimal.get(7000)));
		_stockMetrics.addCandleStick(new CandleStick("20250225 15:55:00 US/Eastern", 10.0, 10.0, 11.5, 9.5, Decimal.get(7000)));

		_stockMetrics.addCandleStick(new CandleStick("20250226 09:30:00 US/Eastern", 10.0, 11.0, 11.5, 9.5, Decimal.get(1000)));
		_stockMetrics.addCandleStick(new CandleStick("20250226 09:55:00 US/Eastern", 10.0, 11.0, 11.5, 9.5, Decimal.get(1000)));
		_stockMetrics.addCandleStick(new CandleStick("20250226 10:00:00 US/Eastern", 10.0, 11.0, 11.5, 9.5, Decimal.get(2000)));
		_stockMetrics.addCandleStick(new CandleStick("20250226 10:55:00 US/Eastern", 10.0, 11.0, 11.5, 9.5, Decimal.get(2000)));
		_stockMetrics.addCandleStick(new CandleStick("20250226 11:00:00 US/Eastern", 10.0, 11.0, 11.5, 9.5, Decimal.get(3000)));
		_stockMetrics.addCandleStick(new CandleStick("20250226 11:55:00 US/Eastern", 10.0, 11.0, 11.5, 9.5, Decimal.get(3000)));
		_stockMetrics.addCandleStick(new CandleStick("20250226 12:00:00 US/Eastern", 10.0, 11.0, 11.5, 9.5, Decimal.get(4000)));
		_stockMetrics.addCandleStick(new CandleStick("20250226 12:55:00 US/Eastern", 10.0, 11.0, 11.5, 9.5, Decimal.get(4000)));
		_stockMetrics.addCandleStick(new CandleStick("20250226 13:00:00 US/Eastern", 10.0, 11.0, 11.5, 9.5, Decimal.get(5000)));
		_stockMetrics.addCandleStick(new CandleStick("20250226 13:55:00 US/Eastern", 10.0, 11.0, 11.5, 9.5, Decimal.get(5000)));
		_stockMetrics.addCandleStick(new CandleStick("20250226 14:00:00 US/Eastern", 10.0, 11.0, 11.5, 9.5, Decimal.get(6000)));
		_stockMetrics.addCandleStick(new CandleStick("20250226 14:55:00 US/Eastern", 10.0, 11.0, 11.5, 9.5, Decimal.get(6000)));
		_stockMetrics.addCandleStick(new CandleStick("20250226 15:00:00 US/Eastern", 10.0, 11.0, 11.5, 9.5, Decimal.get(7000)));
		_stockMetrics.addCandleStick(new CandleStick("20250226 15:55:00 US/Eastern", 10.0, 15.0, 11.5, 9.5, Decimal.get(7000)));
	}

}
