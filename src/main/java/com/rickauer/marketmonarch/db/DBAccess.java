package com.rickauer.marketmonarch.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.rickauer.marketmonarch.HealthChecker;
import com.rickauer.marketmonarch.utils.Verifyable;
import com.rickauer.marketmonarch.utils.Visitor;

; // Unvollständing preparedStatement noch verwenden.

public abstract class DBAccess implements Verifyable {

	private static Logger _dbLogger = LogManager.getLogger(DBAccess.class.getName()); 
	
	private Connection connect;
	private Statement statement;
	private PreparedStatement preparedStatement;
	private ResultSet resultSet;

	public DBAccess() {
		connect = null;
		statement = null;
		preparedStatement = null;
		resultSet = null;
	}
	
	public DBAccess(final String dbUrl, final String user, final String password) {
		
		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
			connect = DriverManager.getConnection(dbUrl, user, password);
			statement = connect.createStatement();
		} catch (ClassNotFoundException | SQLException e) {
			throw new RuntimeException("Error creating object.", e);
		}
	}
	
	public boolean isReadyForOperation(int timeout) {
		
		if (connect != null) {
			try {
				return connect.isValid(timeout);
			} catch (SQLException e) {
				throw new RuntimeException("Could not connect to database.", e);
			}
		}
		
		return false;
	}

	public ResultSet executeSqlQuery(String query) {
		try {
			resultSet = statement.executeQuery(query);
			return resultSet;
		} catch (SQLException e) {
			throw new RuntimeException("Error executing query: '" + query + "'.", e);
		}
	}
	
	public void close() {
		try {

			if (resultSet != null) {
				_dbLogger.info("Closing: " + resultSet.getClass().getName());
			}
			
			if (statement != null) {
				_dbLogger.info("Closing: " + statement.getClass().getName());
			}
			
			if (connect != null) {
				_dbLogger.info("Closing: " + connect.getClass().getName());
			}
			
		} finally {
			try { if (resultSet != null) resultSet.close(); } catch (Exception e) { /*Ignore*/ } 
			try { if (statement != null) statement.close(); } catch (Exception e) { /*Ignore*/ } 
			try { if (connect != null) connect.close(); } catch (Exception e) { /*Ignore*/ } 
		}
	}
}
