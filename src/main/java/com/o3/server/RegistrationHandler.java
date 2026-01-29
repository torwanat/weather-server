package com.o3.server;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.stream.Collectors;

public class RegistrationHandler implements HttpHandler {
	UserAuthenticator authenticator;

	RegistrationHandler(UserAuthenticator userAuthenticator){
		authenticator = userAuthenticator;
	}

	@Override
	public void handle(HttpExchange exchange) throws IOException {
		String requestMethod = exchange.getRequestMethod();

		if("POST".equals(requestMethod)){
			handlePOSTRequest(exchange);
		} else {
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
				if (username != null && password != null && email != null && !username.isEmpty() && !password.isEmpty() && !email.isEmpty()){
					if (authenticator.addUser(username, password, email)) {
						handleResponse(t, "User added successfully", 200);
						return;
					} else {
						handleResponse(t, "User already exists", 403);
						return;
					}
				}
				handleResponse(t, "Empty fields not allowed", 400);
			} catch (JSONException e) {
				handleResponse(t, "Bad JSON input", 400);

			}
		} catch (Exception e) {
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
