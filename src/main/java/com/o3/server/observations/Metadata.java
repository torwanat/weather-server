package com.o3.server.observations;

import org.json.JSONObject;
import org.json.JSONPropertyName;

import java.time.Instant;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;

public record Metadata(@JSONPropertyName("record_time_received") ZonedDateTime recordTimeReceived,
					   @JSONPropertyName("record_owner") String recordOwner, @JSONPropertyName("id") int id,
					   @JSONPropertyName("record_payload") String recordPayload) {
	public Metadata(long recordTimeReceived, String recordOwner, int id, String recordPayload) {
		this(ZonedDateTime.ofInstant(Instant.ofEpochMilli(recordTimeReceived), ZoneOffset.UTC), recordOwner, id, recordPayload);
	}


	public JSONObject toJSON() {
		return new JSONObject(this);
	}

}
