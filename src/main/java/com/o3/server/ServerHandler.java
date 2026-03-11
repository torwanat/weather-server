package com.o3.server;

import com.o3.server.observations.*;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class ServerHandler implements HttpHandler {
	private final ServerDatabase db;

	public ServerHandler() {
		super();
		db = ServerDatabase.getInstance();
	}

	@Override
	public void handle(HttpExchange t) {
//		System.out.println("Request handled in thread " + Thread.currentThread().getName());

		String method = t.getRequestMethod();

		if ("GET".equals(method)) {
			handleGETRequest(t);
		} else if ("POST".equals(method)) {
			boolean success = handlePOSTRequest(t);
			handleEmptyResponse(t, success ? 200 : 400);
		} else if ("PUT".equals(method)) {
			boolean success = handlePUTRequest(t);
			handleEmptyResponse(t, success ? 200 : 400);
		} else {
			handleResponseWithData(t, "Not supported", 400);
		}
	}

	private void handleGETRequest(HttpExchange t){
		try {
			JSONArray responseObservations = db.getRecords();

			if (responseObservations.isEmpty()){
				System.out.println("Empty");
				handleEmptyResponse(t, 204);
				return;
			}

			String responseString = responseObservations.toString();
			handleResponseWithData(t, responseString, 200);

		} catch (SQLException e) {
			e.printStackTrace();
			handleResponseWithData(t, "Internal server error", 500);
		}
	}

	private boolean handlePOSTRequest(HttpExchange t){
		String payload = new BufferedReader(new InputStreamReader(t.getRequestBody(),StandardCharsets.UTF_8)).lines().collect(Collectors.joining("\n"));
		String username = t.getPrincipal().getUsername();
		return parsePOSTRequest(payload, username);
	}

	private boolean handlePUTRequest(HttpExchange t){
		String payload = new BufferedReader(new InputStreamReader(t.getRequestBody(),StandardCharsets.UTF_8)).lines().collect(Collectors.joining("\n"));
		String username = t.getPrincipal().getUsername();
		String query = t.getRequestURI().getQuery();
		return parsePUTRequest(payload, username, query);
	}

	private void handleEmptyResponse(HttpExchange t, int code){
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

	private boolean parsePUTRequest(String payload, String username, String query){
		int id = getIdFromParam(query);
		if (id == -1){
			return false;
		}

		return parsePUTRequest(payload, username, id);
	}

	private JSONObject parseObservationRecord(JSONObject requestBody) throws JSONException {
		String targetBodyName = requestBody.getString("target_body_name");
		String centerBodyName = requestBody.getString("center_body_name");
		ZonedDateTime epoch = ZonedDateTime.parse(requestBody.getString("epoch"));

		boolean hasOrbitalElements = requestBody.has("orbital_elements");
		boolean hasStateVector = requestBody.has("state_vector");

		if (!hasOrbitalElements && !hasStateVector){
			throw new JSONException("Missing orbital_elements or state_vector");
		}

		OrbitalElements orbitalElements = null;
		StateVector stateVector = null;

		if (hasOrbitalElements) {
			double semiMajorAxisAu = requestBody.getJSONObject("orbital_elements").getDouble("semi_major_axis_au");
			double eccentricity = requestBody.getJSONObject("orbital_elements").getDouble("eccentricity");
			double inclinationDeg = requestBody.getJSONObject("orbital_elements").getDouble("inclination_deg");
			double longitudeAscendingNodeDeg = requestBody.getJSONObject("orbital_elements").getDouble("longitude_ascending_node_deg");
			double argumentOfPeriapsisDeg = requestBody.getJSONObject("orbital_elements").getDouble("argument_of_periapsis_deg");
			double meanAnomalyDeg = requestBody.getJSONObject("orbital_elements").getDouble("mean_anomaly_deg");
			orbitalElements = new OrbitalElements(semiMajorAxisAu, eccentricity, inclinationDeg, longitudeAscendingNodeDeg, argumentOfPeriapsisDeg, meanAnomalyDeg);
		}

		if (hasStateVector) {
			JSONArray positionAuJSON = requestBody.getJSONObject("state_vector").getJSONArray("position_au");
			JSONArray velocityAuPerDayJSON = requestBody.getJSONObject("state_vector").getJSONArray("velocity_au_per_day");
			List<Double> positionAu = IntStream.range(0, positionAuJSON.length()).mapToObj(positionAuJSON::getDouble).toList();
			List<Double> velocityAuPerDay = IntStream.range(0, velocityAuPerDayJSON.length()).mapToObj(velocityAuPerDayJSON::getDouble).toList();
			stateVector = new StateVector(positionAu, velocityAuPerDay);
		}

		ObservationRecord observationRecord = new  ObservationRecord(targetBodyName, centerBodyName, epoch);

		JSONObject observationRecordJSON = observationRecord.toJSON();
		if (hasOrbitalElements) {
			observationRecordJSON.put("orbital_elements", orbitalElements.toJSON());
		}
		if (hasStateVector) {
			observationRecordJSON.put("state_vector", stateVector.toJSON());
		}

		return observationRecordJSON;
	}

	private String getRecordPayload(JSONObject requestBody) throws JSONException {
		return requestBody.getJSONObject("metadata").getString("record_payload");
	}

	private List<Observatory> getObservatories(JSONObject requestBody) throws JSONException {
		List<Observatory> observatories = new ArrayList<>();

		if (requestBody.getJSONObject("metadata").has("observatory")) {
			JSONArray observatoriesJSON = requestBody.getJSONObject("metadata").getJSONArray("observatory");
			for(int i = 0; i < observatoriesJSON.length(); i++){
				JSONObject observatoryJSON = observatoriesJSON.getJSONObject(i);
				double longitude = observatoryJSON.getDouble("longitude");
				double latitude = observatoryJSON.getDouble("latitude");
				String observatoryName = observatoryJSON.getString("observatory_name");
				Observatory observatory = new Observatory(latitude, longitude, observatoryName);

				if (observatoryJSON.has("weather")) {
					Weather currentWeather = getWeather(latitude, longitude);
					observatory.setWeather(currentWeather);
				}

				observatories.add(observatory);
			}
		}

		return observatories;
	}

	private String getUpdateReason(JSONObject requestBody) throws JSONException {

		String updateReason = "N/A";
		if (requestBody.getJSONObject("metadata").has("update_reason")) {
			updateReason = requestBody.getJSONObject("metadata").getString("update_reason");
		}

		return updateReason;
	}



	private boolean parsePUTRequest(String payload, String username, int id){
		try {
			JSONObject requestBody = new JSONObject(payload);
			JSONObject observationRecordJSON = parseObservationRecord(requestBody);
			String recordPayload = getRecordPayload(requestBody);
			List<Observatory> observatories = getObservatories(requestBody);
			String updateReason = getUpdateReason(requestBody);

			return db.updateRecord(observationRecordJSON, recordPayload, username, observatories, id, updateReason);
		} catch (Exception e) {
//			e.printStackTrace();
			System.out.println("Error while parsing PUT request");
			return false;
		}
	}

	private boolean parsePOSTRequest(String payload, String username) {
		try {
			JSONObject requestBody = new JSONObject(payload);
			JSONObject observationRecordJSON = parseObservationRecord(requestBody);
			String recordPayload = getRecordPayload(requestBody);
			List<Observatory> observatories = getObservatories(requestBody);

			db.addRecord(observationRecordJSON, recordPayload, username, observatories);
			return true;
		} catch (Exception e) {
//			e.printStackTrace();
			System.out.println("Error while parsing POST request");
			return false;
		}
	}

	private Weather getWeather(double latitude, double longitude) {
		try (HttpClient client = HttpClient.newHttpClient()) {
			HttpRequest request = HttpRequest.newBuilder()
				.uri(URI.create("http://localhost:4001/wfs?latlon=" + latitude + "," + longitude + "&parameters=Temperature,TotalCloudCover,RadiationGlobalAccumulation"))
				.GET()
				.build();

			HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

			String responseBody = response.body();
			List<String> responseBodyParts = Arrays.asList(responseBody.split("BsWfs:ParameterValue"));
			double temperatureInKelvins = sanitizeValue(responseBodyParts.get(1)) + 273.15;
			double cloudinessPercentage = sanitizeValue(responseBodyParts.get(3));
			double backgroundLightVolume = sanitizeValue(responseBodyParts.get(5));

			return new Weather(temperatureInKelvins, cloudinessPercentage, backgroundLightVolume);
		} catch (Exception e) {
			e.printStackTrace();
			return new Weather(0.0, 0.0, 0.0);
		}
	}

	private double sanitizeValue(String value) {
		return Double.parseDouble((value.substring(value.indexOf(">") + 1, value.indexOf("<"))).replaceAll(",", "."));
	}

	private int getIdFromParam(String query) {
		if (query != null && !query.isEmpty()) {
			if (query.contains("&")) {
				return -1;
			}

			String[] keyValue = query.split("=");
			if (keyValue.length == 2 && keyValue[0].equals("id")) {
				return Integer.parseInt(keyValue[1]);
			}
		}

		return -1;
	}
}
