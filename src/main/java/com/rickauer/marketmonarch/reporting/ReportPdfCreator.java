package com.rickauer.marketmonarch.reporting;

import java.awt.Color;
import java.io.IOException;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.GregorianCalendar;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDDocumentInformation;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.encryption.AccessPermission;
import org.apache.pdfbox.pdmodel.encryption.StandardProtectionPolicy;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.font.Standard14Fonts;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;

import com.rickauer.marketmonarch.MarketMonarch;

public final class ReportPdfCreator {
	
	private static Logger _reportCreatorLogger = LogManager.getLogger(ReportPdfCreator.class.getName());
	
	public static String createSessionReport(String filePath) {
		
		_reportCreatorLogger.info("Createing PDF session report.");
		
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd_HHmm");
		String timeStamp = ZonedDateTime.now().format(formatter);
		String sessionFileName = timeStamp + ".pdf";
		String sessionFolder = MarketMonarch.CURRENT_SESSION_STORAGE_FOLDER + "\\" + sessionFileName;

		double entry = MarketMonarch._tradingContext.getAverageBuyFillPrice();
		double exit = MarketMonarch._tradingContext.getAverageSellFillPrice();
		double stopLoss = MarketMonarch._tradingContext.getStopLossAuxPrice();
		int volume = MarketMonarch._tradingContext.getQuantityAsInteger();
		double riskPercent = (entry - stopLoss) * 100 / entry;
		double riskValue = (entry - stopLoss) * volume;
		double winLossTotal = exit - entry;
		double winLossPercent = winLossTotal * 100 / entry;

		try (PDDocument document = new PDDocument()) {
			
			// Create pages
			PDPage pageWithStats = new PDPage(PDRectangle.A4);
			PDPage pageWithDiagram = new PDPage(PDRectangle.A4);
			
			// Add page to document
			document.addPage(pageWithStats);
			document.addPage(pageWithDiagram);
			
			
			// Add content to page
			try (PDPageContentStream contentStreamPageOne = new PDPageContentStream(document, pageWithStats)){
				
				float textMargin = 50;
				float yStart = pageWithStats.getMediaBox().getHeight() - textMargin;
				float leading = 20;
				float pageWidth = pageWithStats.getMediaBox().getWidth();
				
				// Text start
				contentStreamPageOne.beginText();
				
				String title = "Trade-Kennzahlen";
				float fontSize = 24.0f;
				PDFont font = new PDType1Font(Standard14Fonts.FontName.HELVETICA_BOLD);
				float titleWidth = font.getStringWidth(title) / 1000 * fontSize;
				float titleX = (pageWidth - titleWidth) / 2;
				contentStreamPageOne.setFont(font, fontSize);
				contentStreamPageOne.setNonStrokingColor(new Color(100, 149, 237));
				contentStreamPageOne.newLineAtOffset(titleX, yStart);
				contentStreamPageOne.showText(title);
				contentStreamPageOne.endText();
				
				contentStreamPageOne.setNonStrokingColor(Color.BLACK);
				contentStreamPageOne.setFont(new PDType1Font(Standard14Fonts.FontName.HELVETICA), 12);
				
				float labelX = textMargin;
				float valueX = pageWidth / 2;
				float textY = yStart - 80; // yStart - leading * 2;
				
				String[][] rows = { 
						{ "Symbol:", MarketMonarch._tradingContext.getContract().symbol() },
						{ "Positionsgröße:", volume + " Einheiten" },
						{ "Einstandspreis pro Aktie:", String.format("%.2f €", entry) },
						{ "Einstandspreis total:", String.format("%.2f €", entry * volume) },
						{ "Verkaufspreis pro Aktie:", String.format("%.2f €", exit) },
						{ "Verkaufspreis total:", String.format("%.2f €", exit * volume) },
						{ "Stop Loss:", String.format("%.2f", stopLoss) },
						{ "Erwartetes Risiko", String.format("%.2f %%", riskPercent) },
						{ "Risikobetrag:", String.format("%.2f €", riskValue) },
						{ "Gewinn/Verlust pro Aktie (Euro):", String.format("%.2f €", winLossTotal) },
						{ "Gewinn/Verlust gesamt (Euro):", String.format("%.2f €", winLossTotal * volume) },
						{ "Gewinn/Verlust (Prozent):", String.format("%.2f %%", winLossPercent) } };
				
				for (String[] row : rows) {
					contentStreamPageOne.beginText();
					contentStreamPageOne.newLineAtOffset(labelX, textY);
					contentStreamPageOne.showText(row[0]);
					contentStreamPageOne.endText();
					
					contentStreamPageOne.beginText();
					contentStreamPageOne.newLineAtOffset(valueX, textY);
					contentStreamPageOne.showText(row[1]);
					contentStreamPageOne.endText();
					
					textY -= leading;
				}
			}
			
			
			
			
			// Create Image Object
			PDImageXObject pdImage = PDImageXObject.createFromFile(filePath, document);
			// Prepare Content Stream
			try (PDPageContentStream contentStreamPageTwo = new PDPageContentStream(document, pageWithDiagram)) {				
				// Size of the diagram
				float imageWidth = pdImage.getWidth();
				float imageHeight = pdImage.getHeight();
				// Target size for PDF
				float targetWidth = 500;
				float scale = targetWidth / imageWidth;
				float targetHeight = imageHeight * scale;
				// Position of image
				float x = 50;
				float pageHeight = PDRectangle.A4.getHeight();
				float topMargin = 50;
				float y = pageHeight - topMargin - targetHeight;
				// Draw Image in the PDF document at position
				contentStreamPageTwo.drawImage(pdImage, x, y, targetWidth, targetHeight);
			}
			
			
			
			// Set document properties ===================================
			PDDocumentInformation pdd = document.getDocumentInformation();
			pdd.setAuthor("Stefan Rickauer");
			pdd.setTitle("PDF Box Test");
			pdd.setCreator("PdfTest");
			pdd.setSubject("Test case for pdf box library");
			
			Calendar date = new GregorianCalendar();
			date.set(Calendar.getInstance().get(Calendar.YEAR), Calendar.getInstance().get(Calendar.MONTH),
					Calendar.getInstance().get(Calendar.DAY_OF_MONTH));
			pdd.setCreationDate(date);
			// pdd.setModificationDate(date);
			pdd.setKeywords("sample, pdf test");
			
			// Encrypt document ==========================================
			// Create access permission object
			AccessPermission access = new AccessPermission();
			
			// Create StandardProtectionPolicy object (owner pwd, user pwd, permission)
			StandardProtectionPolicy spp = new StandardProtectionPolicy("1234", "5678", access);
			
			// set the length of the encryption key
			spp.setEncryptionKeyLength(256);
			
			// set permissions
			spp.setPermissions(access);
			
			// protect document
			document.protect(spp);
			// Save document
			document.save(sessionFolder);

		} catch (IOException e) {
			_reportCreatorLogger.error("Error creating session report.");
		}

		_reportCreatorLogger.info("Saved Session Report to: " + sessionFolder);
		
		return sessionFileName;
	}
}
