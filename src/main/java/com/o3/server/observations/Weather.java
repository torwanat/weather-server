package com.o3.server.observations;

import org.json.JSONObject;
import org.json.JSONPropertyName;

public record Weather(@JSONPropertyName("temperature_in_kelvins") double temperatureInKelvins,
					  @JSONPropertyName("cloudiness_percentage") double cloudinessPercentage,
					  @JSONPropertyName("background_light_volume") double backgroundLightVolume) {

	public JSONObject toJSON() {
		return new JSONObject(this);
	}
}
