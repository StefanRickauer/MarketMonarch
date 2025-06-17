package com.rickauer.marketmonarch.reporting;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Properties;

import com.rickauer.marketmonarch.MarketMonarch;

import jakarta.activation.DataHandler;
import jakarta.activation.FileDataSource;
import jakarta.mail.Authenticator;
import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import jakarta.mail.PasswordAuthentication;
import jakarta.mail.Session;
import jakarta.mail.Transport;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeBodyPart;
import jakarta.mail.internet.MimeMessage;
import jakarta.mail.internet.MimeMultipart;

public final class Notifier {
	
	public static final String SENDER = "sessionfeed@neurotrace.one";
	public static final String RECIPIENT = "stefanrickauer@gmail.com";
	
	public static void notifyUser(String pdfPath) throws UnsupportedEncodingException, MessagingException {
		Properties props = new Properties();
		props.put("mail.smtp.auth", "true");
		props.put("mail.smtp.starttls.enable", "true");
		props.put("mail.smtp.host", "live.smtp.mailtrap.io");
		props.put("mail.smtp.port", "587");
		
		Session session = Session.getInstance(props, new Authenticator() {
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication("api", MarketMonarch._mailtrapService.getToken()); 	
			}
		});
		
		MimeMessage message = new MimeMessage(session);
		message.setFrom(new InternetAddress(SENDER, MarketMonarch.PROGRAM_AND_VERSION));
		message.addRecipient(Message.RecipientType.TO, new InternetAddress(RECIPIENT));
		message.setSubject(createSubject());
		
		MimeBodyPart messageBodyPart = new MimeBodyPart();
		messageBodyPart.setText(createContent());
		MimeMultipart multipart = new MimeMultipart();
		multipart.addBodyPart(messageBodyPart);
		
		// Creating a MimeBodyPart for the file attachment.
		FileDataSource source = createFileDataSource(pdfPath);
		
		if (source == null) {
			throw new IllegalArgumentException("The path provided returned null object. Could not notify user.");
		}
		messageBodyPart = new MimeBodyPart();
		messageBodyPart.setDataHandler(new DataHandler(source));
		messageBodyPart.setFileName(getFileName(pdfPath));
		multipart.addBodyPart(messageBodyPart);
		message.setContent(multipart);		
		Transport.send(message);
	}
	
	private static String createSubject() {
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
		String timeStamp = ZonedDateTime.now().format(formatter);
		
		String subject = timeStamp + ": Session Report";
		return subject;
	}
	
	private static String createContent() {
		String content = String.format("""
			Guten Tag,

            dies ist ein automatisch erstellter Session-Report.
            Eine PDF-Datei mit allen Informationen finden Sie im Anhang.

            Für den Fall, dass die PDF-Erzeugung fehlgeschlagen ist, finden Sie nachfolgend die wichtigsten Trading-Daten:

            Durchschnittlicher Kaufpreis:     %.2f
            Durchschnittlicher Verkaufspreis: %.2f

            P&L (Gewinn/Verlust):             %.2f

            Mit freundlichen Grüßen
            Ihr MarketMonarch System
            """,
            MarketMonarch._tradingContext.getAverageBuyFillPrice(),
            MarketMonarch._tradingContext.getAverageSellFillPrice(),
            (MarketMonarch._tradingContext.getAverageSellFillPrice() - MarketMonarch._tradingContext.getAverageBuyFillPrice()));
		
		return content;
	}
	
	private static FileDataSource createFileDataSource(String path) {
		
		if (path == null) {
			return null;
		}
		
		File existenceCheck = new File(path);
		
		if (!existenceCheck.exists()) {
			return null;
		}
		
		return new FileDataSource(path);
	}
	
	private static String getFileName(String fullPath) {
		String[] parts = fullPath.split("\\\\");
		return parts[parts.length - 1];
	}
}
