package com.rickauer.marketmonarch.configuration;

import static org.junit.jupiter.api.Assertions.*;

import java.io.File;

import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

@TestMethodOrder(MethodOrderer.MethodName.class)
public class FileSupplierTest {

	private static final String CWD = System.getProperty("user.dir");
	private static final String TMP = CWD + "/temp";
	
	@Test
	void A_creationTest() {
		FileSupplier.createTemporaryFolder();
		
		File temp = new File(TMP);
		assertTrue(temp.exists());
	}
	
	@Test
	void B_deletionTest() {
		
		@SuppressWarnings("unused")
		File subfolder = new File(TMP + "/subfolder");
		File subfolder2 = new File(TMP + "/subfolder/subfolder2");
		
		subfolder.mkdir();
		subfolder2.mkdir();
		
		if (!subfolder2.exists()) {
			System.err.println("Error creating test data. Subfolder2 does not exist.");
			assertTrue(false);	// Test invalid if checking for deleted folders that never existed
		}
		
		FileSupplier.deleteTemporaryFolder();
		assertFalse(new File(TMP).exists());
	}
}
