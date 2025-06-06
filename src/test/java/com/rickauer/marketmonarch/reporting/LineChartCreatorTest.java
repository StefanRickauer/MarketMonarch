package com.rickauer.marketmonarch.reporting;

import static org.junit.jupiter.api.Assertions.*;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import com.ib.client.Decimal;
import com.rickauer.marketmonarch.api.data.CandleStick;
import com.rickauer.marketmonarch.utils.FileSupplier;
import com.rickauer.marketmonarch.utils.StockUtils;

public class LineChartCreatorTest {

	private static List<CandleStick> mockData;
	private static double stopLoss;
	
	@BeforeAll
	public static void initializeTestData() {
		FileSupplier.createFolder(FileSupplier.getTemporaryFolder());
		mockData = new ArrayList<>();
		ZonedDateTime time = ZonedDateTime.now(ZoneId.of("US/Eastern"));
		for (int i = 300; i >= 0; i--) {
			
			mockData.add(new CandleStick(
					time.minusSeconds(i * 5).toString(),		// <- not proud of this piece of code, but hey, it works ;)
					generateRandomDoubleBetweenXandY(5, 20), 
					generateRandomDoubleBetweenXandY(5, 20), 
					generateRandomDoubleBetweenXandY(5, 20), 
					generateRandomDoubleBetweenXandY(5, 20), 
					Decimal.get(generateRandomDoubleBetweenXandY(0, 10000))
					));
		}
		stopLoss = generateRandomDoubleBetweenXandY(6, 10);
	}
	
	private static double generateRandomDoubleBetweenXandY(int lower, int upper) {
		return (Math.random() * (upper - lower)) + lower;
	}
	
	@Test
	void callDefaultConstructorTest() throws InterruptedException {
		LineChartCreator creator = new LineChartCreator();
		creator.createLineGraphAndSaveFile(mockData, stopLoss);
		Thread.sleep(2000);
		assertTrue(new File(LineChartCreator.LINECHART).exists());
	}

	@AfterAll
	public static void removeTestData() {
		FileSupplier.deleteFolder(FileSupplier.getTemporaryFolder());
	}
}
