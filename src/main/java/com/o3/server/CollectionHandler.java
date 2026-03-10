package com.o3.server;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import org.json.JSONArray;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.stream.Collectors;

public class CollectionHandler implements HttpHandler {
	private final ServerDatabase db;

	public CollectionHandler() {
		super();
		db =  ServerDatabase.getInstance();
	}

	@Override
	public void handle(HttpExchange t) {
		String fullPath = t.getRequestURI().getPath();
		String contextPath = t.getHttpContext().getPath();
		String subPath = fullPath.substring(contextPath.length());

		switch (subPath) {
			case "/create":
				handleCreateCollection(t);
				break;
			case "/add":
				handleAddToCollection(t);
				break;
			case "":
				handleCollections(t);
				break;
			default:
				handleGetCollection(t, subPath);
				break;
		}
	}

	private void handleCollections(HttpExchange t) {
		String method = t.getRequestMethod();

		if ("GET".equals(method)) {
			getAllCollections(t);
		} else {
			sendResponseWithData(t, "Not supported", 400);
		}
	}

	private void handleCreateCollection(HttpExchange t) {

	}

	private void handleAddToCollection(HttpExchange t) {

	}

	private void handleGetCollection(HttpExchange t, String id){

	}

	private void getAllCollections(HttpExchange t) {
		try {
			JSONArray responseCollectionsIDs = db.getCollectionsIDs();

			String responseString = responseCollectionsIDs.toString();
			sendResponseWithData(t, responseString, 200);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void createCollection(HttpExchange t) {
		try {
			String payload = new BufferedReader(new InputStreamReader(t.getRequestBody(),StandardCharsets.UTF_8)).lines().collect(Collectors.joining("\n"));
			if (payload.isEmpty()) {

			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private boolean addToCollection(int id, List<Integer> idsToAdd) {
		return true;
	}



	private void sendEmptyResponse(HttpExchange t, int code) {
		try {
			t.sendResponseHeaders(code, -1);
			OutputStream os = t.getResponseBody();
			os.flush();
			os.close();
		} catch (Exception e){
			e.printStackTrace();
		}
	}

	private void sendResponseWithData(HttpExchange t, String responseString, int code) {
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
