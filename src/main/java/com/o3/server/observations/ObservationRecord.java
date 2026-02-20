package com.o3.server.observations;

import com.o3.server.Constants;
import org.json.JSONObject;
import org.json.JSONPropertyIgnore;
import org.json.JSONPropertyName;

import java.time.ZonedDateTime;

public record ObservationRecord(@JSONPropertyName("target_body_name") String targetBodyName,
								@JSONPropertyName("center_body_name") String centerBodyName,
								@JSONPropertyIgnore ZonedDateTime epoch) {


	@JSONPropertyName("epoch")
	public String getEpochString() {
		return epoch.format(Constants.FORMATTER);
	}


	public JSONObject toJSON() {
		return new JSONObject(this);
	}
}
