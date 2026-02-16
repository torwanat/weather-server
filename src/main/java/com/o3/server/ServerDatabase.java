package com.o3.server;

import org.json.JSONObject;

import java.sql.*;

public class ServerDatabase {
	private Connection dbConnection = null;
	private static  ServerDatabase dbInstance = null;

	public static synchronized ServerDatabase getInstance() {
		if (dbInstance == null){
			dbInstance = new ServerDatabase();
		}

		return dbInstance;
	}

	private ServerDatabase() {
		try {
			initDb();
		} catch (SQLException e) {
			System.out.println("Database already found");
		}
	}

	private void initDb() throws SQLException {
		String dbName = "ServerDatabase";
		String database = "jdbc:sqlite:" + dbName;

		dbConnection = DriverManager.getConnection(database);

		if (dbConnection != null) {
			String createUserTable = "CREATE TABLE users (" +
				"username VARCHAR(50) NOT NULL PRIMARY KEY," +
				"password VARCHAR(50) NOT NULL," +
				"email VARCHAR(50)," +
				"nickname VARCHAR(50)" +
				")";
			Statement createStatement = dbConnection.createStatement();
			createStatement.executeUpdate(createUserTable);
			createStatement.close();
		}
	}

	public boolean setUser(JSONObject user) throws SQLException {
		if (checkIfUserExists(user.getString("username"))){
			System.out.println("User already exists");
			return false;
		}

		PreparedStatement setUserQuery = dbConnection.prepareStatement("INSERT INTO users " +
			"VALUES (?, ?, ?, ?)");
		setUserQuery.setString(1, user.getString("username"));
		setUserQuery.setString(2, user.getString("password"));
		setUserQuery.setString(3, user.getString("email"));
		setUserQuery.setString(4, user.getString("nickname"));

		setUserQuery.executeUpdate();
		setUserQuery.close();

		return true;
	}

	public boolean authenticateUser (String username, String password) throws SQLException {
		PreparedStatement authenticateQuery = dbConnection.prepareStatement("SELECT username, password FROM users WHERE username = ?");
		authenticateQuery.setString(1, username);

		ResultSet rs = authenticateQuery.executeQuery();

		return rs.next() && rs.getString("password").equals(password);
	}

	private boolean checkIfUserExists (String username) throws SQLException {
		PreparedStatement checkUserQuery = dbConnection.prepareStatement("SELECT username FROM users WHERE username  = ?");
		checkUserQuery.setString(1, username);

		ResultSet rs = checkUserQuery.executeQuery();

		return rs.next();
	}
}
