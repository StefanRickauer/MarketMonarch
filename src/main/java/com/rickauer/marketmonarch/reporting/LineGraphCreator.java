package com.rickauer.marketmonarch.reporting;

import java.io.File;
import java.io.IOException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtils;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;

import com.rickauer.marketmonarch.configuration.FileSupplier;

public class LineGraphCreator {
	
	private static final String CWD = System.getProperty("user.dir");
	
	private static Logger lineGraphCreatorLogger = LogManager.getLogger(LineGraphCreator.class.getName());
	
	DefaultCategoryDataset tradingData;
	
	public LineGraphCreator() {
		tradingData = new DefaultCategoryDataset();
	}
	
	public void createLineGraph() {
		
		lineGraphCreatorLogger.info("Creating line graph.");
		
		; // Abfrage funktioniert nicht
		if (tradingData == null) {
			lineGraphCreatorLogger.error("Missing data: Unable to create line graph.");
			throw new UnsupportedOperationException("No trading data present. Unable to create chart.");
		}
		
		// Hier müssen Daten bereits in tradingData gespeichert sein.
		JFreeChart lineChartObject = ChartFactory.createLineChart("Invested and Total Capital, Return", "Trading Day", "Euro", tradingData, PlotOrientation.VERTICAL, true, true, false);
		
		int width = 1280; 
		int height = 960;
		String memoryLocation = FileSupplier.printTemporaryFolder() + "/Linechart.jpeg";
		File lineGraph = new File(memoryLocation);
		try {
			ChartUtils.saveChartAsJPEG(lineGraph, lineChartObject, width, height);
			lineGraphCreatorLogger.info("Line graph saved to: " + memoryLocation);
		} catch (IOException e) {
			throw new RuntimeException("Could not save line graph.", e);
		}
	}
}
