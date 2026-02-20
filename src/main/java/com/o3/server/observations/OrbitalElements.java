package com.o3.server.observations;

import org.json.JSONObject;
import org.json.JSONPropertyName;

public record OrbitalElements(@JSONPropertyName("semi_major_axis_au") double semiMajorAxisAu,
							  @JSONPropertyName("eccentricity") double eccentricity,
							  @JSONPropertyName("inclination_deg") double inclinationDeg,
							  @JSONPropertyName("longitude_ascending_node_deg") double longitudeAscendingNodeDeg,
							  @JSONPropertyName("argument_of_periapsis_deg") double argumentOfPeriapsisDeg,
							  @JSONPropertyName("mean_anomaly_deg") double meanAnomalyDeg) {


	public JSONObject toJSON() {
		return new JSONObject(this);
	}
}
