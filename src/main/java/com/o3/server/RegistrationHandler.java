package com.o3.server;

import ch.qos.logback.classic.Logger;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.stream.Collectors;

public class RegistrationHandler implements HttpHandler {
	final UserAuthenticatorDB authenticator;
	private static final Logger logger = (Logger) LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME);

	RegistrationHandler(UserAuthenticatorDB userAuthenticatorDB){
		authenticator = userAuthenticatorDB;
	}

	@Override
	public void handle(HttpExchange exchange) throws IOException {
		String requestMethod = exchange.getRequestMethod();

		if("POST".equals(requestMethod)){
			handlePOSTRequest(exchange);
		} else {
			logger.warn("Method {} not supported in /register", requestMethod);
			handleResponse(exchange, "Not supported", 400);
		}
	}

	private void handlePOSTRequest(HttpExchange t){
		try {
			String payload = new BufferedReader(new InputStreamReader(t.getRequestBody(),StandardCharsets.UTF_8)).lines().collect(Collectors.joining("\n"));
			try {
				JSONObject requestBody = new JSONObject(payload);
				String username = requestBody.getString("username");
				String password = requestBody.getString("password");
				String email = requestBody.getString("email");
				String nickname = requestBody.getString("nickname");

				User user = new User(username, password, email, nickname);

				if (username != null && password != null  && !username.isEmpty() && !password.isEmpty()){
					if (authenticator.addUser(user)) {
						handleResponse(t, "User added successfully", 200);
					} else {
						handleResponse(t, "User already exists", 403);
					}
					return;
				}
				handleResponse(t, "Empty fields not allowed", 400);
			} catch (JSONException e) {
				logger.error("Bad JSON payload", e);
				handleResponse(t, "Bad JSON payload", 400);

			}
		} catch (Exception e) {
			logger.error("Error while processing POST request", e);
			e.printStackTrace();
		}
	}

	private void handleResponse(HttpExchange t, String responseString, int code) throws IOException {
		byte [] bytes = responseString.getBytes(StandardCharsets.UTF_8);
		t.sendResponseHeaders(code, bytes.length);
		OutputStream os = t.getResponseBody();
		os.write(responseString.getBytes());
		os.flush();
		os.close();
	}
}
