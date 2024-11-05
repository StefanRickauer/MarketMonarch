package com.rickauer.marketmonarch.configuration;

import static org.junit.jupiter.api.Assertions.*;

import java.io.File;

import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;

public class FileSupplierTest {

	private static final String CWD = System.getProperty("user.dir");
	private static final String TMP = CWD + "/temp";
	
	@Test
	@Order(1)
	void creationTest() {
		FileSupplier.createTemporaryFolder();
		
		File temp = new File(TMP);
		assertTrue(temp.exists());
	}
	
	@Test
	@Order(2)
	void deletionTest() {
		
		@SuppressWarnings("unused")
		File subfolder = new File(TMP + "/subfolder");
		File subfolder2 = new File(TMP + "/subfolder/subfolder2");
		
		if (!subfolder2.exists()) {
			System.err.println("Error creating test data. Subfolder2 does not exist.");
		}
		
		FileSupplier.deleteTemporaryFolder();
		assertFalse(new File(TMP).exists());
	}
}
