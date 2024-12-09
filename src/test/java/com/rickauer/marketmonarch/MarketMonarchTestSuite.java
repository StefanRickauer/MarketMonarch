package com.rickauer.marketmonarch;

import org.junit.platform.suite.api.SelectClasses;
import org.junit.platform.suite.api.Suite;

import com.rickauer.marketmonarch.api.connect.MailtrapServiceConnectorTest;
import com.rickauer.marketmonarch.api.connect.StockNewsConnector;
import com.rickauer.marketmonarch.configuration.*;
import com.rickauer.marketmonarch.db.*;
import com.rickauer.marketmonarch.reporting.*;

@Suite
@SelectClasses({ HealthCheckerTest.class, FileSupplierTest.class, 
	ConfigReaderTest.class, DBAccessTest.class, LineChartCreatorTest.class, ApiKeyAccessTest.class, MailtrapServiceConnectorTest.class,
	StockNewsConnector.class})
public class MarketMonarchTestSuite {
	// Nothing to do
}
