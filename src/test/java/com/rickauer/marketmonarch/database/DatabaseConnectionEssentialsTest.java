package com.rickauer.marketmonarch.database;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

@TestMethodOrder(MethodOrderer.MethodName.class)
class DatabaseConnectionEssentialsTest {

	@Test
	void A_initializationTest() {
		assertEquals("http://test.url", DatabaseConnectionEssentials.INSTANCE.getUrl());
		assertEquals("test-user", DatabaseConnectionEssentials.INSTANCE.getUsername());
		assertEquals("1234", DatabaseConnectionEssentials.INSTANCE.getPassword());
	}
	
	@Test
	void B_flushTest() {
		DatabaseConnectionEssentials.INSTANCE.flushDatabaseConnectionEssentials();
		assertEquals("", DatabaseConnectionEssentials.INSTANCE.getUrl());
		assertEquals("", DatabaseConnectionEssentials.INSTANCE.getUsername());
		assertEquals("", DatabaseConnectionEssentials.INSTANCE.getPassword());
	}

}
