package com.rickauer.marketmonarch.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.rickauer.marketmonarch.utils.Verifyable;

public abstract class DatabaseDao implements Verifyable {

	private static Logger _dbLogger = LogManager.getLogger(DatabaseDao.class.getName()); 
	
	private Connection connect;
	private Statement statement;
	private ResultSet resultSet;

	public DatabaseDao() {
		connect = null;
		statement = null;
		resultSet = null;
	}
	
	public DatabaseDao(final String dbUrl, final String user, final String password) {
		
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
	
	public String executeSqlQueryAndGetFirstResultAsString(String query, String column) {
		try {
			resultSet = statement.executeQuery(query);
			while (resultSet.next()) {
				return resultSet.getString(column);
			} 
			return "";
		} catch (Exception e) {
			throw new RuntimeException("Error executing '" + query + "'.");
		}
	}
	
	public int executeSqlUpdate(String query) {
		try {
			return statement.executeUpdate(query);
		} catch (Exception e) {
			System.out.println(e);
			throw new RuntimeException("Error executing '" + query + "'.");
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
