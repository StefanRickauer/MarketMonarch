package com.rickauer.marketmonarch.database;

import static org.junit.jupiter.api.Assertions.*;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

@TestMethodOrder(MethodOrderer.MethodName.class)
class DatabaseConnectionEssentialsTest {

	@Test
	void A_initializationTest() {
		assertEquals("jdbc:mysql://localhost:3306/test_db", DatabaseConnectionEssentials.INSTANCE.getUrl());
		assertEquals("root", DatabaseConnectionEssentials.INSTANCE.getUsername());
		assertTrue(DatabaseConnectionEssentials.INSTANCE.getPassword().length() > 0);
	}

	@Test
	void B_flushTest() {
		String sqlSelectAll = "SELECT * FROM test_table";
		; // revise: Add asserts
		try (Connection conn = DriverManager.getConnection(
				DatabaseConnectionEssentials.INSTANCE.getUrl(), 
				DatabaseConnectionEssentials.INSTANCE.getUsername(), 
				DatabaseConnectionEssentials.INSTANCE.getPassword());
			PreparedStatement ps = conn.prepareStatement(sqlSelectAll);
			ResultSet rs = ps.executeQuery();)
				{
					while(rs.next()) {
						String id = rs.getString("id");
						String name = rs.getString("name");
						
						System.out.println("id: " + id + "\nname: " + name);
					}
		} catch (SQLException e) {
			throw new RuntimeException("Error reading database.", e);
		}
	}

	@Test
	void C_flushTest() {
		DatabaseConnectionEssentials.INSTANCE.flushDatabaseConnectionEssentials();
		assertEquals("", DatabaseConnectionEssentials.INSTANCE.getUrl());
		assertEquals("", DatabaseConnectionEssentials.INSTANCE.getUsername());
		assertEquals("", DatabaseConnectionEssentials.INSTANCE.getPassword());
	}
}
