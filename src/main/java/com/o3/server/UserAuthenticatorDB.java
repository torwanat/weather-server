package com.o3.server;

import com.sun.net.httpserver.BasicAuthenticator;
import org.json.JSONObject;

import java.sql.SQLException;

public class UserAuthenticatorDB extends BasicAuthenticator {
	private final ServerDatabase db;

	public UserAuthenticatorDB() {
		super("datarecord");
		db = ServerDatabase.getInstance();
	}

	@Override
	public boolean checkCredentials(String username, String password) {
		boolean isValid;

		try {
			isValid = db.authenticateUser(username, password);
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}

		return isValid;
	}

	public boolean addUser(User user) {
		boolean result;

		try {
			result = db.setUser(
				new JSONObject()
					.put("username", user.getUsername())
					.put("password", user.getPassword())
					.put("email", user.getEmail())
					.put("nickname", user.getNickname())
			);
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}

		return result;
	}
}
