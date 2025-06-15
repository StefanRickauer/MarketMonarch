package com.rickauer.marketmonarch.reporting;

import static org.junit.jupiter.api.Assertions.*;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.ta4j.core.Bar;
import org.ta4j.core.BarSeries;
import org.ta4j.core.BaseBar;
import org.ta4j.core.BaseBarSeriesBuilder;
import org.ta4j.core.num.DecimalNum;

import com.ib.client.Decimal;
import com.rickauer.marketmonarch.api.data.CandleStick;
import com.rickauer.marketmonarch.utils.FileSupplier;
import com.rickauer.marketmonarch.utils.StockUtils;

public class LineChartCreatorTest {

	static String targetFolderPath;
	static File targetFolder;
	static String targetFilePath;
	
	
	@BeforeAll
	static void initializeTestEnvironment() {
		targetFolderPath = "./src/test/resources/test-session";
		targetFolder = new File(targetFolderPath);
		
		if (!targetFolder.exists()) {
			targetFolder.mkdir();
		}
		targetFilePath = targetFolderPath + "/Linechart.jpeg";
	}
	
	@Test
	void createLineGraphAndSaveFileTest() {
		BarSeries testSeries = new BaseBarSeriesBuilder()
				.withNumTypeOf(DecimalNum::valueOf)
				.build();
		String csvData = FileSupplier.readFile("./src/test/resources/data.csv");
		String[] csvLine = csvData.split("\n");
		int i = 0;
		ZonedDateTime start = ZonedDateTime.now(ZoneId.of("US/Eastern")).withNano(0);
		for (String line : csvLine) {
			String[] value = line.split(",");
			Bar baseBar = new BaseBar(
					Duration.ofMillis(5), 
					start.plusSeconds(i * 5), 
					DecimalNum.valueOf(Double.parseDouble(value[1])), 
					DecimalNum.valueOf(Double.parseDouble(value[2])),
					DecimalNum.valueOf(Double.parseDouble(value[3])), 
					DecimalNum.valueOf(Double.parseDouble(value[4])), 
					DecimalNum.valueOf(Double.parseDouble(value[5])),
					DecimalNum.valueOf(0));
			
			testSeries.addBar(baseBar);
			i++;
		}
		LineChartCreator.createLineGraphAndSaveFile(testSeries, 13.14, testSeries.getBar(275).getEndTime(), 13.4, testSeries.getBar(4595).getEndTime(), 15.755, targetFilePath);
		File lineChart = new File("C:\\Test\\MM\\Linechart.jpeg");
		assertTrue(lineChart.exists());
	}
	
	@AfterAll
	static void deleteTestEnvironment() {
		FileSupplier.deleteFolder(targetFolderPath);
	}
}
