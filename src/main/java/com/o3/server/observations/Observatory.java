package com.o3.server.observations;

import org.json.JSONObject;
import org.json.JSONPropertyName;

public class Observatory {
	private double latitude;
	private double longitude;
	private String observatoryName;

	public Observatory(double latitude, double longitude, String observatoryName) {
		this.latitude = latitude;
		this.longitude = longitude;
		this.observatoryName = observatoryName;
	}

	@JSONPropertyName("latitude")
	public double getLatitude() {
		return latitude;
	}

	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}

	@JSONPropertyName("longitude")
	public double getLongitude() {
		return longitude;
	}

	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}

	@JSONPropertyName("observatory_name")
	public String getObservatoryName() {
		return observatoryName;
	}

	public void setObservatoryName(String observatoryName) {
		this.observatoryName = observatoryName;
	}

	public JSONObject toJSON() {
		return new JSONObject(this);
	}
}
