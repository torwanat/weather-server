package com.o3.server.observations;

import org.json.JSONPropertyName;

public class Weather {
	private double temperatureInKelvins;
	private double cloudinessPercentage;
	private double backgroundLightVolume;

	public Weather(double temperatureInKelvins, double cloudinessPercentage, double backgroundLightVolume) {
		this.temperatureInKelvins = temperatureInKelvins;
		this.cloudinessPercentage = cloudinessPercentage;
		this.backgroundLightVolume = backgroundLightVolume;
	}

	public Weather(Weather that) {
		this.temperatureInKelvins = that.getTemperatureInKelvins();
		this.cloudinessPercentage = that.getCloudinessPercentage();
		this.backgroundLightVolume = that.getBackgroundLightVolume();
	}

	@JSONPropertyName("temperature_in_kelvins")
	public double getTemperatureInKelvins() {
		return temperatureInKelvins;
	}

	public void setTemperatureInKelvins(double temperatureInKelvins) {
		this.temperatureInKelvins = temperatureInKelvins;
	}

	@JSONPropertyName("cloudiness_percentage")
	public double getCloudinessPercentage() {
		return cloudinessPercentage;
	}

	public void setCloudinessPercentage(double cloudinessPercentage) {
		this.cloudinessPercentage = cloudinessPercentage;
	}

	@JSONPropertyName("background_light_volume")
	public double getBackgroundLightVolume() {
		return backgroundLightVolume;
	}

	public void setBackgroundLightVolume(double backgroundLightVolume) {
		this.backgroundLightVolume = backgroundLightVolume;
	}
}
