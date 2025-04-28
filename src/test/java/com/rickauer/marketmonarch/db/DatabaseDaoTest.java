package com.rickauer.marketmonarch.db;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

public class DatabaseDaoTest {

	// Successful initializations are being tested in HealthCheckerTest class
	
	@Test
	void assertThrowsException1() {
		@SuppressWarnings("unused")
		Exception exception = assertThrows(RuntimeException.class, () -> {
			new ApiKeyDao("", "", ""); 
		});
		
		String expectedMessage = "Error creating object.";
		String actualMessage = exception.getMessage();
		
		assertTrue(actualMessage.contains(expectedMessage));
	}

	@Test
	void assertThrowsException2() {
		@SuppressWarnings("unused")
		Exception exception = assertThrows(RuntimeException.class, () -> {
			new FinancialDataDao("", "", "");
		});
		
		String expectedMessage = "Error creating object.";
		String actualMessage = exception.getMessage();
		
		assertTrue(actualMessage.contains(expectedMessage));
	}
}
