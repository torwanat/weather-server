package com.o3.server.observations;

import org.json.JSONPropertyName;

public class Observatory {
	private String name;
	private Double latitude;
	private Double longitude;
	private Weather weather;

	public Observatory(String name, Double latitude, Double longitude) {
		this.name = name;
		this.latitude = latitude;
		this.longitude = longitude;
	}

	public Observatory(String name) {
		this(name, null, null);
	}

	public Observatory(Observatory that) {
		this(that.getName(), that.getLatitude(), that.getLongitude());
		if (that.getWeather() != null)
			this.weather = new Weather(that.getWeather());
	}

	@JSONPropertyName("observatory_name")
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Double getLatitude() {
		return latitude;
	}

	public void setLatitude(Double latitude) {
		this.latitude = latitude;
	}

	public Double getLongitude() {
		return longitude;
	}

	public void setLongitude(Double longitude) {
		this.longitude = longitude;
	}

	public void setWeather(Weather weather) {
		this.weather = weather;
	}

	public Weather getWeather() {
		return weather;
	}
}
