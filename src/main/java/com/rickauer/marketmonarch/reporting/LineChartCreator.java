package com.rickauer.marketmonarch.reporting;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtils;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.annotations.XYDrawableAnnotation;
import org.jfree.chart.annotations.XYTextAnnotation;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.chart.ui.TextAnchor;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.ta4j.core.Bar;
import org.ta4j.core.BarSeries;

import com.rickauer.marketmonarch.MarketMonarch;
import com.rickauer.marketmonarch.api.data.CandleStick;
import com.rickauer.marketmonarch.utils.FileSupplier;

public final class LineChartCreator {
	
	public static final String LINECHART = MarketMonarch.CURRENT_SESSION_STORAGE_FOLDER + "\\Linechart.jpeg";
	private static Logger _lineGraphCreatorLogger = LogManager.getLogger(LineChartCreator.class.getName());
	
	
	public static void createLineGraphAndSaveFile(BarSeries tradedStock, double stopLoss) {
		
		Font font = new Font("Arial", Font.BOLD, 12);
		
		XYSeries series = new XYSeries("Trade");
		for (Bar stick : tradedStock.getBarData()) {
			series.add(stick.getEndTime().toInstant().toEpochMilli(), stick.getClosePrice().doubleValue());
		}
		
		XYSeriesCollection dataset = new XYSeriesCollection(series);
		JFreeChart chart = ChartFactory.createTimeSeriesChart(
				"Linienchart - 5 Sekunden Zeitintervall",
				"Zeit",
				"Preis pro Aktie",
				dataset,
				false, true, false
				);
		
		XYPlot plot = chart.getXYPlot();
		
		DateAxis axis = (DateAxis) plot.getDomainAxis();
		axis.setDateFormatOverride(new SimpleDateFormat("HH:mm:ss"));
		
		XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer(true, false);
		renderer.setSeriesPaint(0, new Color(40, 100, 200));
		renderer.setSeriesStroke(0, new BasicStroke(1.0f));
		plot.setRenderer(renderer);
		
		Date buyTime = Date.from(MarketMonarch._tradingContext.getEntryDetected().toInstant()); 
		double buyPrice = MarketMonarch._tradingContext.getAverageBuyFillPrice();
		Date sellTime = Date.from(MarketMonarch._tradingContext.getExitTriggered().toInstant());
		double sellPrice = MarketMonarch._tradingContext.getAverageSellFillPrice();
		
		XYDrawableAnnotation buyMarker = new XYDrawableAnnotation(
				buyTime.getTime(),
				buyPrice,
				14,
				14,
				(Graphics2D g2, Rectangle2D area) -> {
					g2.setColor(Color.GRAY.brighter());
					g2.fill(new Ellipse2D.Double(area.getX(), area.getY(), area.getWidth(), area.getHeight()));
					g2.setColor(Color.BLACK);
					g2.draw(new Ellipse2D.Double(area.getX(), area.getY(), area.getWidth(), area.getHeight()));
				});
		
		plot.addAnnotation(buyMarker);
		
		XYTextAnnotation buyText = new XYTextAnnotation("Kauf", buyTime.getTime(), buyPrice + 0.1);
		buyText.setFont(font);
		buyText.setPaint(Color.GRAY.darker());
		plot.addAnnotation(buyText);

		Color sellColor = (sellPrice - buyPrice > 0) ? Color.GREEN : Color.RED;
		
		XYDrawableAnnotation sellMarker = new XYDrawableAnnotation(
				sellTime.getTime(),
				sellPrice,
				14,
				14,
				(Graphics2D g2, Rectangle2D area) -> {
					g2.setColor(sellColor.brighter());
					g2.fill(new Ellipse2D.Double(area.getX(), area.getY(), area.getWidth(), area.getHeight()));
					g2.setColor(Color.BLACK);
					g2.draw(new Ellipse2D.Double(area.getX(), area.getY(), area.getWidth(), area.getHeight()));
				});
		plot.addAnnotation(sellMarker);
		
		XYTextAnnotation sellText = new XYTextAnnotation("Verkauf", sellTime.getTime(), sellPrice + 0.1);
		sellText.setFont(font);
		sellText.setPaint(sellColor.darker());
		plot.addAnnotation(sellText);
		
		XYSeries stopLossSeries = new XYSeries("Stop Loss");
		stopLossSeries.add(buyTime.getTime(), stopLoss);
		stopLossSeries.add(sellTime.getTime(), stopLoss);
		
		dataset.addSeries(stopLossSeries);
		XYTextAnnotation stopLossLabel = new XYTextAnnotation(
				"Stop Loss", 
				buyTime.getTime() + (sellTime.getTime() - buyTime.getTime()) / 2,
				stopLoss + 0.02);
		stopLossLabel.setFont(font);
		stopLossLabel.setPaint(Color.RED);
		stopLossLabel.setTextAnchor(TextAnchor.BOTTOM_CENTER);
		renderer.setSeriesStroke(1, new BasicStroke(
				1.0f,
				BasicStroke.CAP_BUTT,
				BasicStroke.JOIN_BEVEL, 
				0,
				new float[] {5.0f, 5.0f },
				0.0f));
		plot.addAnnotation(stopLossLabel);
		
		plot.setDomainGridlineStroke(new BasicStroke(0.5f));
		plot.setRangeGridlineStroke(new BasicStroke(0.5f));
		chart.setBackgroundPaint(Color.WHITE);
		plot.setBackgroundPaint(Color.WHITE);
		plot.setDomainGridlinePaint(new Color(220, 220, 220));
		plot.setRangeGridlinePaint(new Color(220, 220, 220));
		
		chart.setBorderVisible(false);
		plot.setOutlineVisible(false);
		
		File lineGraph = new File(LINECHART);
		int chartWidth = 1200;
		int chartHeight = 600;
		
		try {
			ChartUtils.saveChartAsJPEG(lineGraph, chart, chartWidth, chartHeight);
			_lineGraphCreatorLogger.info("Line graph saved to: " + LINECHART);
		} catch (IOException e) {
			throw new RuntimeException("Could not save line graph.", e);
		}
	}
}
