package com.rickauer.marketmonarch.database;

import static org.junit.Assert.assertEquals;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class DatabaseConnectionEssentialsTest {

	@Test
	void test() {
		DatabaseConnectionEssentials conn = DatabaseConnectionEssentials.readEssentials();
		assertEquals("http://test.url", conn.getUrl());
		assertEquals("test-user", conn.getUsername());
		assertEquals("1234", conn.getPassword());
	}

}
