package com.o3.server.observations;

import org.json.JSONObject;
import org.json.JSONPropertyName;

import java.time.Instant;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.List;

public class Metadata {
	private ZonedDateTime recordTimeReceived;
	private String recordOwner;
	private int id;
	private String recordPayload;
	private List<Observatory> observatories;

	public Metadata(ZonedDateTime recordTimeReceived, String recordOwner, String recordPayload, int id, List<Observatory> observatories) {
		this.recordTimeReceived = recordTimeReceived;
		this.recordOwner = recordOwner;
		this.recordPayload = recordPayload;
		this.id = id;
		this.observatories = observatories;
	}

	public Metadata(long timestamp, String recordOwner, int id, String recordPayload, List<Observatory> observatories) {
		this.recordTimeReceived = ZonedDateTime.ofInstant(Instant.ofEpochMilli(timestamp), ZoneOffset.UTC);
		this.recordOwner = recordOwner;
		this.recordPayload = recordPayload;
		this.id = id;
		this.observatories = observatories;
	}

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

	@JSONPropertyName("observatory")
	public List<Observatory> getObservatories() {
		return observatories;
	}

	public void setObservatories(List<Observatory> observatories) {
		this.observatories = observatories;
	}
}
