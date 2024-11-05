package com.rickauer.marketmonarch.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import com.rickauer.marketmonarch.utils.Verifyable;
import com.rickauer.marketmonarch.utils.Visitor;

; // Unvollständing preparedStatement noch einbauen. close()-Methode überarbeiten -> wenn jetzt bei erstem Versuch eine Exception ausgelöst wird, bleiben die anderen offen

public abstract class DBAccess implements Verifyable {

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
				resultSet.close();
			}
			
			if (statement != null) {
				statement.close();
			}
			
			if (connect != null) {
				connect.close();
			}
			
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
}
