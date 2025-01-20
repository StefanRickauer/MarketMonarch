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
import com.rickauer.marketmonarch.api.controller.StockNewsRequestControllerTest;
import com.rickauer.marketmonarch.configuration.*;
import com.rickauer.marketmonarch.db.*;
import com.rickauer.marketmonarch.reporting.*;

// Test cases for stock news api were removed because subscription was canceled so api calls won't succeed anymore

@Suite
@SelectClasses({ HealthCheckerTest.class, FileSupplierTest.class, 
	ConfigReaderTest.class, DBAccessTest.class, LineChartCreatorTest.class, ApiKeyAccessTest.class, MailtrapServiceConnectorTest.class,
	AlphaVantageConnectorTest.class, AlhaVantageRequestControllerTest.class, FmpConnectorTest.class, FmpRequestControllerTest.class})
public class MarketMonarchTestSuite {
	// Nothing to do
}
