package com.rickauer.marketmonarch.utils;

import static org.junit.jupiter.api.Assertions.*;

import java.io.File;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;


@TestMethodOrder(MethodOrderer.MethodName.class)
public class FileSupplierTest {

	private static final String CWD = System.getProperty("user.dir");
	private static final String TMP = CWD + "/temp";
	private static final String TXT_FILE = CWD + "\\src\\test\\resources\\test.txt";
	
	@Test
	void A_creationTest() {
		FileSupplier.createFolder(FileSupplier.getTemporaryFolder());
		
		File temp = new File(TMP);
		assertTrue(temp.exists());
	}
	
	@Test
	void B_deletionTest() {
		
		File subfolder = new File(TMP + "/subfolder");
		File subfolder2 = new File(TMP + "/subfolder/subfolder2");
		
		subfolder.mkdir();
		subfolder2.mkdir();
		
		if (!subfolder2.exists()) {
			System.err.println("Error creating test data. Subfolder2 does not exist.");
			assertTrue(false);	// Test invalid if checking for deleted folders that never existed
		}
		
		FileSupplier.deleteFolder(FileSupplier.getTemporaryFolder());
		assertFalse(new File(TMP).exists());
	}
	
	@Test
	void C_writeTest() {
		File file = new File(TXT_FILE);
		
		assertFalse(file.exists());
		
		FileSupplier.writeFile(TXT_FILE, "Das ist ein Test");
		
		assertTrue(file.exists());
	}
	
	@Test
	void D_readTest() {
		String content = FileSupplier.readFile(TXT_FILE);
		
		assertEquals("Das ist ein Test", content);
	}
	
	@AfterAll
	static void cleanTestEnvironment() {
		File file = new File(TXT_FILE);
		
		file.delete();
	}
}
