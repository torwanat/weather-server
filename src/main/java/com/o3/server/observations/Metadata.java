package com.o3.server.observations;

import org.json.JSONObject;
import org.json.JSONPropertyName;

import java.time.Instant;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;

public class Metadata {
	private ZonedDateTime recordTimeReceived;
	private String recordOwner;
	private int id;
	private String recordPayload;

	public Metadata(ZonedDateTime recordTimeReceived, String recordOwner, int id, String recordPayload){
		this.recordTimeReceived = recordTimeReceived;
		this.recordOwner = recordOwner;
		this.id = id;
		this.recordPayload = recordPayload;
	}

	public Metadata(long timestamp, String recordOwner, int id, String recordPayload) {
		this.recordTimeReceived = ZonedDateTime.ofInstant(Instant.ofEpochMilli(timestamp), ZoneOffset.UTC);
		this.recordOwner = recordOwner;
		this.id = id;
		this.recordPayload = recordPayload;
	}

	@JSONPropertyName("record_time_received")
	public ZonedDateTime getRecordTimeReceived() {
		return recordTimeReceived;
	}

	public void setRecordTimeReceived(ZonedDateTime recordTimeReceived) {
		this.recordTimeReceived = recordTimeReceived;
	}

	@JSONPropertyName("record_owner")
	public String getRecordOwner() {
		return recordOwner;
	}

	public void setRecordOwner(String recordOwner) {
		this.recordOwner = recordOwner;
	}

	@JSONPropertyName("id")
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	@JSONPropertyName("record_payload")
	public String getRecordPayload() {
		return recordPayload;
	}

	public void setRecordPayload(String recordPayload) {
		this.recordPayload = recordPayload;
	}

	public JSONObject toJSON() { return new JSONObject(this); }
}
