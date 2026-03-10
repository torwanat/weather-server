package com.o3.server;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import org.json.JSONArray;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.stream.Collectors;

public class CollectionHandler implements HttpHandler {
	private final ServerDatabase db;

	public CollectionHandler() {
		super();
		db =  ServerDatabase.getInstance();
	}

	@Override
	public void handle(HttpExchange t) {
		String method = t.getRequestMethod();

		if ("GET".equals(method)) {
			handleGETRequest(t);
		} else if ("POST".equals(method)) {
			handlePOSTRequest(t);
		} else {
			handleResponseWithData(t, "Not supported", 400);
		}
	}

	private void handleGETRequest(HttpExchange t) {
		try {
			JSONArray responseCollectionsIDs = db.getCollectionsIDs();

			String responseString = responseCollectionsIDs.toString();
			handleResponseWithData(t, responseString, 200);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void handlePOSTRequest(HttpExchange t) {
		
	}



	private void handleEmptyResponse(HttpExchange t, int code) {
		try {
			t.sendResponseHeaders(code, -1);
			OutputStream os = t.getResponseBody();
			os.flush();
			os.close();
		} catch (Exception e){
			e.printStackTrace();
		}
	}

	private void handleResponseWithData(HttpExchange t, String responseString, int code) {
		try {
			byte[] bytes = responseString.getBytes(StandardCharsets.UTF_8);
			t.sendResponseHeaders(code, bytes.length);
			OutputStream os = t.getResponseBody();
			os.write(responseString.getBytes());
			os.flush();
			os.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
