package com.o3.server.observations;

import com.o3.server.Constants;
import org.json.JSONObject;
import org.json.JSONPropertyIgnore;
import org.json.JSONPropertyName;

import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

public class ObservationRecord {
	private String targetBodyName;
	private String centerBodyName;
	private ZonedDateTime epoch;
	private OrbitalElements orbitalElements;
	private StateVector stateVector;
	private Metadata metadata;

	public ObservationRecord(String targetBodyName, String centerBodyName, ZonedDateTime epoch, StateVector stateVector,
							 String metadataString) {
		this.targetBodyName = targetBodyName;
		this.centerBodyName = centerBodyName;
		this.epoch = epoch;
		this.stateVector = stateVector;
		this.metadata = new Metadata(metadataString);
	}

	public ObservationRecord(String targetBodyName, String centerBodyName, ZonedDateTime epoch,
							 OrbitalElements orbitalElements, String metadataString) {
		this.targetBodyName = targetBodyName;
		this.centerBodyName = centerBodyName;
		this.epoch = epoch;
		this.orbitalElements = orbitalElements;
		this.metadata = new Metadata(metadataString);
	}

	public ObservationRecord(String targetBodyName, String centerBodyName, ZonedDateTime epoch,
							 OrbitalElements orbitalElements, StateVector stateVector, String metadataString) {
		this.targetBodyName = targetBodyName;
		this.centerBodyName = centerBodyName;
		this.epoch = epoch;
		this.orbitalElements = orbitalElements;
		this.stateVector = stateVector;
		this.metadata = new Metadata(metadataString);
	}

	public ObservationRecord(String targetBodyName, String centerBodyName, ZonedDateTime epoch, String metadataString) {
		this.targetBodyName = targetBodyName;
		this.centerBodyName = centerBodyName;
		this.epoch = epoch;
		this.metadata = new Metadata(metadataString);
	}

	public ObservationRecord(String targetBodyName, String centerBodyName, ZonedDateTime epoch, StateVector stateVector) {
		this.targetBodyName = targetBodyName;
		this.centerBodyName = centerBodyName;
		this.epoch = epoch;
		this.stateVector = stateVector;
	}

	public ObservationRecord(String targetBodyName, String centerBodyName, ZonedDateTime epoch,
							 OrbitalElements orbitalElements) {
		this.targetBodyName = targetBodyName;
		this.centerBodyName = centerBodyName;
		this.epoch = epoch;
		this.orbitalElements = orbitalElements;
	}

	public ObservationRecord(String targetBodyName, String centerBodyName, ZonedDateTime epoch) {
		this.targetBodyName = targetBodyName;
		this.centerBodyName = centerBodyName;
		this.epoch = epoch;
	}

	public ObservationRecord(String targetBodyName, String centerBodyName, ZonedDateTime epoch,
							 OrbitalElements orbitalElements, StateVector stateVector) {
		this.targetBodyName = targetBodyName;
		this.centerBodyName = centerBodyName;
		this.epoch = epoch;
		this.orbitalElements = orbitalElements;
		this.stateVector = stateVector;
	}

	public ObservationRecord(ObservationRecord that) {
		this.targetBodyName = that.getTargetBodyName();
		this.centerBodyName = that.getCenterBodyName();
		this.epoch = that.getEpoch();
		this.orbitalElements = new OrbitalElements(that.getOrbitalElements());
		this.stateVector = new StateVector(that.getStateVector());
		this.metadata = new Metadata(that.getMetadata());
	}

	public Metadata getMetadata() {
		return metadata;
	}

	public void setMetadata(Metadata metadata) {
		this.metadata = metadata;
	}

	@JSONPropertyName("center_body_name")
	public String getCenterBodyName() {
		return centerBodyName;
	}

	public void setCenterBodyName(String centerBodyName) {
		this.centerBodyName = centerBodyName;
	}

	@JSONPropertyName("target_body_name")
	public String getTargetBodyName() {
		return targetBodyName;
	}

	public void setTargetBodyName(String targetBodyName) {
		this.targetBodyName = targetBodyName;
	}

	@JSONPropertyName("epoch")
	public String getEpochString() {
		return epoch
				.withZoneSameInstant(ZoneOffset.UTC)
				.format(Constants.FORMATTER);
	}

	@JSONPropertyIgnore
	public ZonedDateTime getEpoch() {
		return epoch;
	}

	public void setEpoch(ZonedDateTime epoch) {
		this.epoch = epoch;
	}

	@JSONPropertyName("orbital_elements")
	public OrbitalElements getOrbitalElements() {
		return orbitalElements;
	}

	public void setOrbitalElements(OrbitalElements orbitalElements) {
		this.orbitalElements = orbitalElements;
	}

	@JSONPropertyName("state_vector")
	public StateVector getStateVector() {
		return stateVector;
	}

	public void setStateVector(StateVector stateVector) {
		this.stateVector = stateVector;
	}

	@Override
	public String toString() {
		return "MessageHorizons [targetBodyName=" + targetBodyName + ", centerBodyName=" + centerBodyName + ", epoch="
				+ epoch + ", orbitalElements=" + orbitalElements + ", stateVector=" + stateVector + "]";
	}

	public String toJsonString() {
		return toJson().toString();
	}

	public String toJsonString(int indentFactor) {
		return toJson().toString(indentFactor);
	}

	public JSONObject toJson() {
		JSONObject msg = new JSONObject(this);

		return msg;
	}
}
