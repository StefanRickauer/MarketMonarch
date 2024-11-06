package com.rickauer.marketmonarch.configuration;

import static org.junit.jupiter.api.Assertions.*;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import com.rickauer.marketmonarch.configuration.ConfigReader;

@TestMethodOrder(MethodOrderer.MethodName.class)
public class ConfigReaderTest {

	@Test
	void A_initializationTest() {
		ConfigReader.INSTANCE.initializeConfigReader();
		assertEquals("jdbc:mysql://localhost:3306/test_db", ConfigReader.INSTANCE.getUrlTestDB());
		assertEquals("root", ConfigReader.INSTANCE.getUsername());
		assertTrue(ConfigReader.INSTANCE.getPassword().length() > 0);
	}

	@Test
	void B_queryTest() {
		String sqlSelectAll = "SELECT * FROM test_table";
		
		Map<String, String> expectedValues = new HashMap<>();
		expectedValues.put("id", "my new value");
		expectedValues.put("id2", "my new value2");
		
		try (Connection conn = DriverManager.getConnection(
				ConfigReader.INSTANCE.getUrlTestDB(), 
				ConfigReader.INSTANCE.getUsername(), 
				ConfigReader.INSTANCE.getPassword());
			PreparedStatement ps = conn.prepareStatement(sqlSelectAll);
			ResultSet rs = ps.executeQuery();)
				{
					while(rs.next()) {
						String id = rs.getString("id");
						String name = rs.getString("name");
						
						assertTrue(expectedValues.containsKey(id));
						assertTrue(name.equals(expectedValues.get(id)));
					}
		} catch (SQLException e) {
			throw new RuntimeException("Error reading database.", e);
		}
	}

	@Test
	void C_flushTest() {
		ConfigReader.INSTANCE.flushDatabaseConnectionEssentials();
		assertEquals("", ConfigReader.INSTANCE.getUrlTestDB());
		assertEquals("", ConfigReader.INSTANCE.getUsername());
		assertEquals("", ConfigReader.INSTANCE.getPassword());
	}
	
	@Test
	void D_existenceTest() {
		assertTrue(ConfigReader.INSTANCE.isSourceFilePresent());
	}
}
