package com.o3.server;

import com.sun.net.httpserver.BasicAuthenticator;
import org.json.JSONObject;

import java.sql.SQLException;

public class UserAuthenticatorDB extends BasicAuthenticator {
	private ServerDatabase db = null;

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

	public boolean addUser(String username, String password, String email, String nickname) {
		boolean result;

		try {
			result = db.setUser(
				new JSONObject()
					.put("username", username)
					.put("password", password)
					.put("email", email)
					.put("nickname", nickname)
			);
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}

		return result;
	}
}
