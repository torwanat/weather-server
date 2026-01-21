package com.o3.server;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

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
			boolean success = handlePOSTRequest(exchange);
			handlePOSTResponse(exchange, success);
		} else {
			handleNotSupportedRequest(exchange);
		}
	}

	private boolean handlePOSTRequest(HttpExchange t){
		try {
			String payload = new BufferedReader(new InputStreamReader(t.getRequestBody(),StandardCharsets.UTF_8)).lines().collect(Collectors.joining("\n"));
			String[] separate = payload.split(":");
			String username = separate[0];
			String password = separate[1];
			if (username != null && password != null){
				return authenticator.addUser(username, password);
			} else return false;
		} catch (Exception e) {
			return false;
		}
	}

	private void handlePOSTResponse(HttpExchange t, boolean success) throws IOException {
		String responseString = success? "User added successfully" : "Error while adding user";
		byte [] bytes = responseString.getBytes(StandardCharsets.UTF_8);
		t.sendResponseHeaders(success? 200 : 403, bytes.length);
		OutputStream os = t.getResponseBody();
		os.write(responseString.getBytes());
		os.flush();
		os.close();
	}

	private void handleNotSupportedRequest(HttpExchange t) throws IOException {
		String responseString = "Not supported";
		byte [] bytes = responseString.getBytes(StandardCharsets.UTF_8);
		t.sendResponseHeaders(400, bytes.length);
		OutputStream os = t.getResponseBody();
		os.write(responseString.getBytes());
		os.flush();
		os.close();
	}
}
