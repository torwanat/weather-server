package com.o3.server.observations;

import org.json.JSONObject;
import org.json.JSONPropertyName;

public final class Observatory {
	private final double latitude;
	private final double longitude;
	private final String observatoryName;
	private Weather weather;

	public Observatory(double latitude,
					   double longitude,
					   String observatoryName,
					   Weather weather) {
		this.latitude = latitude;
		this.longitude = longitude;
		this.observatoryName = observatoryName;
		this.weather = weather;
	}

	public Observatory(double latitude, double longitude, String observatoryName) {
		this.latitude = latitude;
		this.longitude = longitude;
		this.observatoryName = observatoryName;
	}

	public void setWeather(Weather weather) {
		this.weather = weather;
	}


	public JSONObject toJSON() {
		return new JSONObject(this);
	}

	@JSONPropertyName("latitude")
	public double getLatitude() {
		return latitude;
	}

	@JSONPropertyName("longitude")
	public double getLongitude() {
		return longitude;
	}

	@JSONPropertyName("observatory_name")
	public String getObservatoryName() {
		return observatoryName;
	}

	@JSONPropertyName("weather")
	public Weather weather() {
		return weather;
	}

}
