package com.rickauer.marketmonarch.reporting;

import static org.junit.jupiter.api.Assertions.*;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import com.rickauer.marketmonarch.configuration.FileSupplier;

public class LineChartCreatorTest {

	private File lineChart = new File(LineChartCreator.LINECHART);
	
	@BeforeAll
	public static void initializeTestData() {
		FileSupplier.createTemporaryFolder();
	}
	
	@Test
	void callDefaultConstructorTest() throws IOException, InterruptedException {
		LineChartCreator creator = new LineChartCreator();
		creator.createLineGraph();
		Desktop.getDesktop().open(lineChart);
		Thread.sleep(2000);
		assertTrue(new File(LineChartCreator.LINECHART).exists());
	}

	@AfterAll
	public static void removeTestData() {
		FileSupplier.deleteTemporaryFolder();
	}
}
