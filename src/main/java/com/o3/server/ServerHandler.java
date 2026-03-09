package com.o3.server;

import com.o3.server.observations.*;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;
import java.time.ZonedDateTime;
import java.util.ArrayList;
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
			handleEmptyResponse(t, success? 200 : 400);
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

	private boolean parsePOSTRequest(String payload, String username) {
		try {
			JSONObject requestBody = new JSONObject(payload);
			String targetBodyName = requestBody.getString("target_body_name");
			String centerBodyName = requestBody.getString("center_body_name");
			ZonedDateTime epoch = ZonedDateTime.parse(requestBody.getString("epoch"));

			boolean hasOrbitalElements = requestBody.has("orbital_elements");
			boolean hasStateVector = requestBody.has("state_vector");
			boolean hasObservatory =  requestBody.getJSONObject("metadata").has("observatory");

			if (!hasOrbitalElements && !hasStateVector){
				return false;
			}

			OrbitalElements orbitalElements = null;
			StateVector stateVector = null;
			List<Observatory> observatories = new ArrayList<>();

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

			String recordPayload = requestBody.getJSONObject("metadata").getString("record_payload");

			if(hasObservatory){
				JSONArray observatoriesJSON = requestBody.getJSONObject("metadata").getJSONArray("observatory");
				for(int i = 0; i < observatoriesJSON.length(); i++){
					JSONObject observatoryJSON = observatoriesJSON.getJSONObject(i);
					double longitude = observatoryJSON.getDouble("longitude");
					double latitude = observatoryJSON.getDouble("latitude");
					String observatoryName = observatoryJSON.getString("observatory_name");
					Observatory observatory = new Observatory(latitude, longitude, observatoryName);

					if (observatoryJSON.has("weather")) {
						JSONObject weatherJSON = observatoryJSON.getJSONObject("weather");
						double temperatureInKelvins = weatherJSON.getDouble("temperature_in_kelvins");
						double cloudinessPercentage = weatherJSON.getDouble("cloudiness_percentage");
						double backgroundLightVolume = weatherJSON.getDouble("background_light_volume");
						Weather weather = new Weather(temperatureInKelvins, cloudinessPercentage, backgroundLightVolume);

						observatory.setWeather(weather);
					}

					observatories.add(observatory);
				}
			}

			JSONObject observationRecordJSON = observationRecord.toJSON();
			if (hasOrbitalElements) {
				observationRecordJSON.put("orbital_elements", orbitalElements.toJSON());
			}
			if (hasStateVector) {
				observationRecordJSON.put("state_vector", stateVector.toJSON());
			}


			db.addRecord(observationRecordJSON, recordPayload, username, observatories);
			return true;
		} catch (Exception e) {
//			e.printStackTrace();
			System.out.println("Error while parsing POST request");
			return false;
		}
	}
}
