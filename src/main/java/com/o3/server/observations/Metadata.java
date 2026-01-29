package com.o3.server.observations;

import org.json.JSONPropertyName;

import java.util.ArrayList;
import java.util.List;

public class Metadata {
	private String recordPayload;
	private String owner;
	private ArrayList<Observatory> observatories;

	public Metadata(String recordPayload) {
		this.recordPayload = recordPayload;
	}

	public Metadata(Metadata that) {
		this.recordPayload = that.getRecordPayload();
		this.owner = that.getOwner();
		this.observatories = new ArrayList<>(that.getObservatories().stream().map(Observatory::new).toList());
	}

	@JSONPropertyName("observatory")
	public List<Observatory> getObservatories() {
		return observatories;
	}

	public void setObservatories(List<Observatory> observatories) {
		this.observatories = new ArrayList<>(observatories);
	}

	@JSONPropertyName("record_owner")
	public String getOwner() {
		return owner;
	}

	public void setOwner(String owner) {
		this.owner = owner;
	}

	@JSONPropertyName("record_payload")
	public String getRecordPayload() {
		return recordPayload;
	}

	public void setRecordPayload(String recordPayload) {
		this.recordPayload = recordPayload;
	}
}
