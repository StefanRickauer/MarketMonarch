package com.rickauer.marketmonarch.reporting;

import static org.junit.jupiter.api.Assertions.*;

import java.io.UnsupportedEncodingException;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import com.rickauer.marketmonarch.api.connect.MailtrapServiceConnector;
import com.rickauer.marketmonarch.configuration.DatabaseConnector;
import com.rickauer.marketmonarch.db.ApiKeyDao;

import jakarta.mail.MessagingException;

public class NotifierTest {

	private static ApiKeyDao _apiAccess;
	public static MailtrapServiceConnector _mailtrapService;
	
	@BeforeAll
	public static void initializeTestData() {
		DatabaseConnector.INSTANCE.initializeDatabaseConnector();
		_apiAccess = new ApiKeyDao(DatabaseConnector.INSTANCE.getUrlAPIKey(), DatabaseConnector.INSTANCE.getUsername(), DatabaseConnector.INSTANCE.getPassword());
		_mailtrapService = new MailtrapServiceConnector("mailtrap", _apiAccess.executeSqlQueryAndGetFirstResultAsString("SELECT token FROM credentials where provider = 'mailtrap'", "token"));
		DatabaseConnector.INSTANCE.flushDatabaseConnectionEssentials();
	}
	
	@Test
	void notifyUserInCorrectPathTest() {
		Exception exception = assertThrows(IllegalArgumentException.class, () -> {
			Notifier.notifyUser("C:\\Test\\mic", _mailtrapService.getToken(), 2.5, 3.5);
		});
		
		String actualMessage = exception.getMessage(); 
		assertEquals("The path provided returned null object. Could not notify user. Provided path: 'C:\\Test\\mic'.", actualMessage);
	}
	
	@Test
	void notifyUserNullPathTest() {
		Exception exception = assertThrows(IllegalArgumentException.class, () -> {
			Notifier.notifyUser(null, _mailtrapService.getToken(), 2.5, 3.5);
		});
		
		String actualMessage = exception.getMessage(); 
		assertEquals("The path provided returned null object. Could not notify user. Provided path: 'null'.", actualMessage);
	}
	
	@Test
	void notifyUserCorrectPathTest() {
		try {
			Notifier.notifyUser("C:\\Test\\mickey.pdf", _mailtrapService.getToken(), 2.5, 3.5);
		} catch (UnsupportedEncodingException | MessagingException e) {
			assertTrue(false);
		}
		assertTrue(true);
	}
}
