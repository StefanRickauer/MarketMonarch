package com.rickauer.marketmonarch;

import org.junit.platform.suite.api.SelectClasses;
import org.junit.platform.suite.api.Suite;

import com.rickauer.marketmonarch.api.connect.AlphaVantageConnectorTest;
import com.rickauer.marketmonarch.api.connect.FmpConnectorTest;
import com.rickauer.marketmonarch.api.connect.MailtrapServiceConnectorTest;
import com.rickauer.marketmonarch.api.connect.StockNewsConnector;
import com.rickauer.marketmonarch.api.connect.StockNewsConnectorTest;
import com.rickauer.marketmonarch.api.controller.AlhaVantageRequestControllerTest;
import com.rickauer.marketmonarch.api.controller.FmpRequestControllerTest;
import com.rickauer.marketmonarch.api.controller.InteractiveBrokersApiControllerTest;
import com.rickauer.marketmonarch.api.controller.StockNewsRequestControllerTest;
import com.rickauer.marketmonarch.configuration.*;
import com.rickauer.marketmonarch.data.StockMetricsTest;
import com.rickauer.marketmonarch.db.*;
import com.rickauer.marketmonarch.db.data.processing.AggregateTradeMetricsCalculatorTest;
import com.rickauer.marketmonarch.db.data.processing.SingleTradeMetricsCalculatorTest;
import com.rickauer.marketmonarch.reporting.*;
import com.rickauer.marketmonarch.utils.FileSupplierTest;
import com.rickauer.marketmonarch.utils.StockUtilsTest;

@Suite
@SelectClasses({ HealthCheckerTest.class, FileSupplierTest.class, 
	ConfigReaderTest.class, DatabaseDaoTest.class, LineChartCreatorTest.class, ApiKeyDaoTest.class, MailtrapServiceConnectorTest.class,
	AlphaVantageConnectorTest.class, AlhaVantageRequestControllerTest.class, FmpConnectorTest.class, FmpRequestControllerTest.class,
	InteractiveBrokersApiControllerTest.class, StockUtilsTest.class, StockMetricsTest.class, FinancialDataDaoTest.class, SingleTradeMetricsCalculatorTest.class,
	AggregateTradeMetricsCalculatorTest.class})
public class MarketMonarchTestSuite {
	// Nothing to do
}
