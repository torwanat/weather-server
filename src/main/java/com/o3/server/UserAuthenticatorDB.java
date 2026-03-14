package com.o3.server;

import ch.qos.logback.classic.Logger;
import com.sun.net.httpserver.BasicAuthenticator;
import org.json.JSONObject;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;

public class UserAuthenticatorDB extends BasicAuthenticator {
	private final ServerDatabase db;
	private static final Logger logger = (Logger) LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME);

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
			logger.error("Error while authenticating user", e);
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
			logger.error("Error while adding user", e);
			return false;
		}

		return result;
	}
}
