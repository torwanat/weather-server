package com.o3.server.observations;

import org.json.JSONObject;
import org.json.JSONPropertyName;

public record Observatory(@JSONPropertyName("latitude") double latitude,
						  @JSONPropertyName("longitude") double longitude,
						  @JSONPropertyName("observatory_name") String observatoryName) {


	public JSONObject toJSON() {
		return new JSONObject(this);
	}
}
