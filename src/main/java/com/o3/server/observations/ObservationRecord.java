package com.o3.server.observations;

import com.o3.server.Constants;
import org.json.JSONObject;
import org.json.JSONPropertyIgnore;
import org.json.JSONPropertyName;

import java.time.ZonedDateTime;

public class ObservationRecord {
	private String targetBodyName;
	private String centerBodyName;
	private ZonedDateTime epoch;
	private OrbitalElements orbitalElements;
	private StateVector stateVector;

	public ObservationRecord(String targetBodyName, String centerBodyName, ZonedDateTime epoch, OrbitalElements orbitalElements){
		this.targetBodyName = targetBodyName;
		this.centerBodyName = centerBodyName;
		this.epoch = epoch;
		this.orbitalElements = orbitalElements;
	}

	public ObservationRecord(String targetBodyName, String centerBodyName, ZonedDateTime epoch, StateVector stateVector){
		this.targetBodyName = targetBodyName;
		this.centerBodyName = centerBodyName;
		this.epoch = epoch;
		this.stateVector = stateVector;
	}

	public ObservationRecord(String targetBodyName, String centerBodyName, ZonedDateTime epoch, OrbitalElements orbitalElements, StateVector stateVector){
		this.targetBodyName = targetBodyName;
		this.centerBodyName = centerBodyName;
		this.epoch = epoch;
		this.orbitalElements = orbitalElements;
		this.stateVector = stateVector;
	}

	@JSONPropertyName("target_body_name")
	public String getTargetBodyName() {
		return targetBodyName;
	}

	public void setTargetBodyName(String targetBodyName) {
		this.targetBodyName = targetBodyName;
	}

	@JSONPropertyName("center_body_name")
	public String getCenterBodyName() {
		return centerBodyName;
	}


	public void setCenterBodyName(String centerBodyName) {
		this.centerBodyName = centerBodyName;
	}

	@JSONPropertyName("epoch")
	public String getEpochString () {
		return epoch.format(Constants.FORMATTER);
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

	public JSONObject toJSON() {
		return new JSONObject(this);
	}
}
