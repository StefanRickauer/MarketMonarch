package com.rickauer.marketmonarch.reporting;

import static org.junit.jupiter.api.Assertions.*;

import java.io.UnsupportedEncodingException;

import org.junit.jupiter.api.Test;

import jakarta.mail.MessagingException;

class NotifierTest {

	public static final String EXPECTED_MESSAGE = "The path provided returned null object. Could not notify user.";
	
	@Test
	void notifyUserInCorrectPathTest() {
		Exception exception = assertThrows(IllegalArgumentException.class, () -> {
			Notifier.notifyUser("C:\\Users\\noNameForM3\\Test\\mic");
		});
		
		String actualMessage = exception.getMessage(); 
		assertEquals(EXPECTED_MESSAGE, actualMessage);
	}
	
	@Test
	void notifyUserNullPathTest() {
		Exception exception = assertThrows(IllegalArgumentException.class, () -> {
			Notifier.notifyUser(null);
		});
		
		String actualMessage = exception.getMessage(); 
		assertEquals(EXPECTED_MESSAGE, actualMessage);
	}
	
	@Test
	void notifyUserCorrectPathTest() {
		try {
			Notifier.notifyUser("C:\\Users\\noNameForM3\\Test\\mickey.pdf");
		} catch (UnsupportedEncodingException | MessagingException e) {
			assertTrue(false);
		}
		assertTrue(true);
	}
}
