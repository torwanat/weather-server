package com.o3.server;

import com.o3.server.observations.Metadata;
import com.o3.server.observations.ObservationRecord;
import com.o3.server.observations.Observatory;
import org.json.JSONArray;
import org.json.JSONObject;

import java.sql.*;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

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
//			e.printStackTrace();
		}
	}

	private void initDb() throws SQLException {
		String dbName = System.getenv("DATABASE_PATH");
		String database = "jdbc:sqlite:" + dbName;

		dbConnection = DriverManager.getConnection(database);

		if (dbConnection != null) {
			Statement createStatement = dbConnection.createStatement();

			String createUserTable = "CREATE TABLE users (" +
				"username VARCHAR(50) NOT NULL PRIMARY KEY," +
				"password VARCHAR(50) NOT NULL," +
				"email VARCHAR(50)," +
				"nickname VARCHAR(50)" +
				")";
			createStatement.executeUpdate(createUserTable);

			String createDataTable = "CREATE TABLE data (" +
				"id INTEGER PRIMARY KEY," +
				"timestamp INTEGER NOT NULL," +
				"owner VARCHAR(50) NOT NULL," +
				"payload TEXT NOT NULL," +
				"observatories TEXT," +
				"data TEXT NOT NULL," +
				"CHECK (observatories IS NULL OR json_valid(observatories))," +
				"CHECK (json_valid(data))" +
				")";
			createStatement.executeUpdate(createDataTable);

			createStatement.close();
		}
	}

	public boolean setUser(JSONObject user) throws SQLException {
		if (checkIfUserExists(user.getString("username"))){
			System.out.println("User already exists");
			return false;
		}

		PreparedStatement setUserQuery = dbConnection.prepareStatement("INSERT INTO users (username, password, email, nickname) " +
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

	public void addRecord(ObservationRecord observationRecord, String payload, String username) throws SQLException {
		ZonedDateTime now = ZonedDateTime.now(ZoneId.of("UTC"));
		long timestamp = now.toInstant().toEpochMilli();

		String nickname = getNickname(username);

		PreparedStatement insertObservationQuery = dbConnection.prepareStatement("INSERT INTO data (timestamp, owner, payload, data) " +
			"VALUES (?, ?, ?, ?)");
		insertObservationQuery.setLong(1, timestamp);
		insertObservationQuery.setString(2, nickname);
		insertObservationQuery.setString(3, payload);
		insertObservationQuery.setString(4, observationRecord.toJSON().toString());

		insertObservationQuery.executeUpdate();
		insertObservationQuery.close();
	}

	public void addRecord(ObservationRecord observationRecord, String payload, String username, JSONArray observatories) throws SQLException {
		ZonedDateTime now = ZonedDateTime.now(ZoneId.of("UTC"));
		long timestamp = now.toInstant().toEpochMilli();

		String nickname = getNickname(username);

		PreparedStatement insertObservationQuery = dbConnection.prepareStatement("INSERT INTO data (timestamp, owner, payload, observatories, data) " +
			"VALUES (?, ?, ?, ?, ?)");
		insertObservationQuery.setLong(1, timestamp);
		insertObservationQuery.setString(2, nickname);
		insertObservationQuery.setString(3, payload);
		insertObservationQuery.setString(4, observatories.toString());
		insertObservationQuery.setString(5, observationRecord.toJSON().toString());

		insertObservationQuery.executeUpdate();
		insertObservationQuery.close();
	}

	private String getNickname(String username) throws SQLException {
		PreparedStatement getNicknameQuery = dbConnection.prepareStatement("SELECT nickname FROM users WHERE username = ?");
		getNicknameQuery.setString(1, username);

		ResultSet rs = getNicknameQuery.executeQuery();

		return rs.next() ? rs.getString("nickname") : "";
	}

	public JSONArray getRecords() throws SQLException {
		Statement getRecordsQuery = dbConnection.createStatement();
		String getRecordsString = "SELECT * FROM data";
		JSONArray recordsArray = new JSONArray();

		ResultSet rs = getRecordsQuery.executeQuery(getRecordsString);

		while (rs.next()) {
			long timestamp = rs.getLong("timestamp");
			String recordOwner = rs.getString("owner");
			int id = rs.getInt("id");
			String payload = rs.getString("payload");
			JSONObject data = new JSONObject(rs.getString("data"));

			Metadata metadata = new Metadata(timestamp, recordOwner, id, payload);

			if(rs.getString("observatories") != null) {
				JSONArray observatoriesJSON = new JSONArray(rs.getString("observatories"));
				List<Observatory> observatories = new ArrayList<Observatory>();

				for (int i = 0; i < observatoriesJSON.length(); i++) {
					JSONObject observatoryJSON = observatoriesJSON.getJSONObject(i);
					Observatory observatory = new Observatory(
						observatoryJSON.getDouble("longitude"),
						observatoryJSON.getDouble("latitude"),
						observatoryJSON.getString("observatory_name")
					);
					observatories.add(observatory);
				}

				metadata.setObservatories(observatories);
			}

			data.put("metadata", metadata.toJSON());
			recordsArray.put(data);
		}

		return recordsArray;
	}
}
