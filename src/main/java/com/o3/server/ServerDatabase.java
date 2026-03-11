package com.o3.server;

import com.o3.server.observations.Metadata;
import com.o3.server.observations.Observatory;
import org.apache.commons.codec.digest.Crypt;
import org.json.JSONArray;
import org.json.JSONObject;

import java.sql.*;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

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

			String createCollectionsTable = "CREATE TABLE collections(" +
				"id INTEGER PRIMARY KEY," +
				"messages TEXT NOT NULL" +
				")";
			createStatement.executeUpdate(createCollectionsTable);

			createStatement.close();
		}
	}

	public boolean setUser(JSONObject user) throws SQLException {
		if (checkIfUserExists(user.getString("username"))){
			System.out.println("User already exists");
			return false;
		}

		String hashedPassword = hashPassword(user.getString("password"));

		PreparedStatement setUserQuery = dbConnection.prepareStatement("INSERT INTO users (username, password, email, nickname) " +
			"VALUES (?, ?, ?, ?)");
		setUserQuery.setString(1, user.getString("username"));
		setUserQuery.setString(2, hashedPassword);
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

		if(rs.next()){
			String hashedPassword = rs.getString("password");
			return hashedPassword.equals(Crypt.crypt(password, hashedPassword));
		}

		return false;
	}

	private boolean checkIfUserExists (String username) throws SQLException {
		PreparedStatement checkUserQuery = dbConnection.prepareStatement("SELECT username FROM users WHERE username  = ?");
		checkUserQuery.setString(1, username);

		ResultSet rs = checkUserQuery.executeQuery();

		return rs.next();
	}

	private String hashPassword (String password) {
		return Crypt.crypt(password);
	}

	public void addRecord(JSONObject observationRecordJSON, String payload, String username, List<Observatory> observatories) throws SQLException {
		ZonedDateTime now = ZonedDateTime.now(ZoneId.of("UTC"));
		long timestamp = now.toInstant().toEpochMilli();

		String nickname = getNickname(username);

		PreparedStatement insertObservationQuery;

		if (observatories.isEmpty()) {
			insertObservationQuery = dbConnection.prepareStatement("INSERT INTO data (timestamp, owner, payload, data) " +
				"VALUES (?, ?, ?, ?)");
			insertObservationQuery.setLong(1, timestamp);
			insertObservationQuery.setString(2, nickname);
			insertObservationQuery.setString(3, payload);
			insertObservationQuery.setString(4, observationRecordJSON.toString());
		} else {
			JSONArray observatoriesJSONArray = new JSONArray(observatories);
			insertObservationQuery = dbConnection.prepareStatement("INSERT INTO data (timestamp, owner, payload, observatories, data) " +
				"VALUES (?, ?, ?, ?, ?)");
			insertObservationQuery.setLong(1, timestamp);
			insertObservationQuery.setString(2, nickname);
			insertObservationQuery.setString(3, payload);
			insertObservationQuery.setString(4, observatoriesJSONArray.toString());
			insertObservationQuery.setString(5, observationRecordJSON.toString());
		}

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

		ResultSet rs = getRecordsQuery.executeQuery(getRecordsString);

		return extractRecords(rs);
	}

	public JSONArray getCollectionsIDs() throws SQLException {
		Statement getCollectionsIDsQuery = dbConnection.createStatement();
		String getCollectionsIDsString = "SELECT id FROM collections";
		JSONArray collectionsIDsArray = new JSONArray();

		ResultSet rs = getCollectionsIDsQuery.executeQuery(getCollectionsIDsString);

		while (rs.next()) {
			int id = rs.getInt("id");
			collectionsIDsArray.put(id);
		}
		return collectionsIDsArray;
	}

	public int createCollection(List<Integer> collectionIDs) throws SQLException {
		PreparedStatement addNewCollectionQuery = dbConnection.prepareStatement("INSERT INTO collections (messages) VALUES (?)",
			Statement.RETURN_GENERATED_KEYS);
		addNewCollectionQuery.setString(1, collectionIDs.toString());
		addNewCollectionQuery.executeUpdate();

		ResultSet rs = addNewCollectionQuery.getGeneratedKeys();
		if (rs.next()) {
			return rs.getInt(1);
		} else  {
			return -1;
		}
	}

	public void addToCollection(int collectionID, List<Integer> idsToAdd) throws SQLException {
		PreparedStatement addToCollectionQuery = dbConnection.prepareStatement("UPDATE collections SET messages = ? WHERE id = ?");
		addToCollectionQuery.setString(1, idsToAdd.toString());
		addToCollectionQuery.setInt(2, collectionID);
		addToCollectionQuery.executeUpdate();
		addToCollectionQuery.close();
	}

	public JSONArray getRecordsFromCollection(int collectionId) throws SQLException {
		List<Integer> recordIds = getCollectionRecordIds(collectionId);

		String idCountPlaceholder = recordIds.stream().map(i -> "?").collect(Collectors.joining(", "));
		String getRecordsByIdsStatement = "SELECT * FROM data WHERE id IN (" + idCountPlaceholder + ")";
		PreparedStatement getRecordsByIdsQuery = dbConnection.prepareStatement(getRecordsByIdsStatement);

		for (int i = 0; i < recordIds.size(); i++){
			getRecordsByIdsQuery.setInt(i + 1, recordIds.get(i));
		}

		ResultSet rs = getRecordsByIdsQuery.executeQuery();

		return extractRecords(rs);
	}

	private JSONArray extractRecords(ResultSet rs) throws SQLException {
		JSONArray recordsArray = new JSONArray();

		while (rs.next()) {
			long timestamp = rs.getLong("timestamp");
			String recordOwner = rs.getString("owner");
			int id = rs.getInt("id");
			String payload = rs.getString("payload");
			JSONObject data = new JSONObject(rs.getString("data"));

			Metadata metadata = new Metadata(timestamp, recordOwner, id, payload);
			JSONObject metadataJSON = metadata.toJSON();

			if(rs.getString("observatories") != null) {
				JSONArray observatoriesJSON = new JSONArray(rs.getString("observatories"));

				metadataJSON.put("observatory", observatoriesJSON);
			}

			data.put("metadata", metadataJSON);
			recordsArray.put(data);
		}

		return recordsArray;
	}

	private List<Integer> getCollectionRecordIds(int collectionId) throws SQLException {
		PreparedStatement getCollectionRecordIdsQuery = dbConnection.prepareStatement("SELECT messages FROM collections WHERE id = ?");
		getCollectionRecordIdsQuery.setInt(1, collectionId);
		List<Integer> recordIds = new ArrayList<>();

		ResultSet rs = getCollectionRecordIdsQuery.executeQuery();
		if (rs.next()) {
			JSONArray recordIdsJSON = new JSONArray(rs.getString("messages"));
			for (int i = 0; i < recordIdsJSON.length(); i++) {
				recordIds.add(recordIdsJSON.getInt(i));
			}
		}
		getCollectionRecordIdsQuery.close();

		return recordIds;
	}
}
