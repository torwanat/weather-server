package com.o3.server.observations;

import org.json.JSONObject;
import org.json.JSONPropertyName;

import java.util.List;

public record StateVector(@JSONPropertyName("position_au") List<Double> positionAu,
						  @JSONPropertyName("velocity_au_per_day") List<Double> velocityAuPerDay) {


	public JSONObject toJSON() {
		return new JSONObject(this);
	}
}
