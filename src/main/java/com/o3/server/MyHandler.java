package com.o3.server;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

public class MyHandler implements HttpHandler {
	@Override
	public void handle(HttpExchange t) throws IOException {

		String method = t.getRequestMethod();

		if ("GET".equals(method)) {
			String response = handleGETRequest(t);
			handleGETResponse(t, response);
		} else if ("POST".equals(method)) {
			System.out.println("POST");
		} else {
			handleRubbishResponse(t);
		}

//			String response = t.getRequestMethod();
//			t.sendResponseHeaders(200, response.length());
//			OutputStream os = t.getResponseBody();
//			os.write(response.getBytes());
//			os.close();
	}

	private String handleGETRequest(HttpExchange t){
		String query = Objects.toString(t.getRequestURI().getQuery(), "");
		String param = "No params :c";
		try {
			param = query.split("=")[1];
		} catch (Exception ignored) {}
		return param;
	}

	private void handlePOSTRequest(HttpExchange t){

	}

	private void handleGETResponse(HttpExchange t, String responseString) throws IOException{
		byte [] bytes = responseString.getBytes(StandardCharsets.UTF_8);
		t.sendResponseHeaders(200, bytes.length);
		OutputStream os = t.getResponseBody();
		os.write(responseString.getBytes());
		os.close();
	}

	private void handleRubbishResponse(HttpExchange t) throws IOException{
		String responseString = "Not supported";
		byte [] bytes = responseString.getBytes(StandardCharsets.UTF_8);
		t.sendResponseHeaders(400, bytes.length);
		OutputStream os = t.getResponseBody();
		os.write(responseString.getBytes());
		os.close();
	}
}
