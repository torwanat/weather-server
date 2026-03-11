package com.o3.server;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
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
		String method = t.getRequestMethod();
		if ("POST".equals(method)) {
			createCollection(t);
		}  else {
			sendResponseWithData(t, "Not supported", 400);
		}
	}

	private void handleAddToCollection(HttpExchange t) {
		String method = t.getRequestMethod();
		if ("POST".equals(method)) {
			addToCollection(t);
		}  else {
			sendResponseWithData(t, "Not supported", 400);
		}
	}

	private void handleGetCollection(HttpExchange t, String idPath){
		try {
			int id = Integer.parseInt(idPath.split("/")[1]);
			getRecordsFromCollection(t, id);
		} catch (Exception e) {
			e.printStackTrace();
			sendEmptyResponse(t, 400);
		}
	}

	private void getAllCollections(HttpExchange t) {
		try {
			JSONArray responseCollectionsIDs = db.getCollectionsIDs();

			String responseString = responseCollectionsIDs.toString();
			sendResponseWithData(t, responseString, 200);
		} catch (Exception e) {
			e.printStackTrace();
			sendEmptyResponse(t, 400);
		}
	}

	private void createCollection(HttpExchange t) {
		try {
			String payload = new BufferedReader(new InputStreamReader(t.getRequestBody(),StandardCharsets.UTF_8)).lines().collect(Collectors.joining("\n"));
			List<Integer> collectionIDs = new ArrayList<>();
			if (!payload.isEmpty()) {
				JSONArray collectionIDsJSON = new JSONArray(payload);
				for (int i = 0; i < collectionIDsJSON.length(); i++) {
					collectionIDs.add(collectionIDsJSON.getInt(i));
				}
			}
			int createdCollectionId = db.createCollection(collectionIDs);

			if (createdCollectionId != -1) {
				JSONObject responseJSON = new JSONObject();
				responseJSON.put("created_collection_id", createdCollectionId);
				sendResponseWithData(t, responseJSON.toString(), 200);
			}  else {
				sendEmptyResponse(t, 400);
			}
		} catch (Exception e) {
			e.printStackTrace();
			sendEmptyResponse(t, 400);
		}
	}

	private void addToCollection(HttpExchange t) {
		try {
			String payload = new BufferedReader(new InputStreamReader(t.getRequestBody(),StandardCharsets.UTF_8)).lines().collect(Collectors.joining("\n"));
			List<Integer> idsToAdd = new ArrayList<>();

			JSONObject requestBody = new JSONObject(payload);
			int collectionId = requestBody.getInt("collection_id");
			JSONArray idsToAddJSON = requestBody.getJSONArray("message_ids");
			for (int i = 0; i < idsToAddJSON.length(); i++) {
				idsToAdd.add(idsToAddJSON.getInt(i));
			}

			db.addToCollection(collectionId, idsToAdd);
			sendEmptyResponse(t, 200);
		} catch (Exception e) {
			e.printStackTrace();
			sendEmptyResponse(t, 400);
		}
	}

	private void getRecordsFromCollection(HttpExchange t, int id) {
		try {
			JSONArray responseRecords = db.getRecordsFromCollection(id);
			String responseString = responseRecords.toString();
			sendResponseWithData(t, responseString, 200);
		} catch (Exception e) {
			e.printStackTrace();
			sendEmptyResponse(t, 400);
		}
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
