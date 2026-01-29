package com.o3.server.observations;

import org.json.JSONObject;
import org.json.JSONPropertyName;

import java.util.List;

public class StateVector {
	private List<Double> positionAu;
	private List<Double> velocityAuPerDay;

	public StateVector(List<Double> positionAu, List<Double> velocityAuPerDay){
		this.positionAu = positionAu;
		this.velocityAuPerDay = velocityAuPerDay;
	}

	@JSONPropertyName("position_au")
	public List<Double> getPositionAu() {
		return positionAu;
	}

	public void setPositionAu(List<Double> positionAu) {
		this.positionAu = positionAu;
	}

	@JSONPropertyName("velocity_au_per_day")
	public List<Double> getVelocityAuPerDay() {
		return velocityAuPerDay;
	}

	public void setVelocityAuPerDay(List<Double> velocityAuPerDay) {
		this.velocityAuPerDay = velocityAuPerDay;
	}

	public JSONObject toJSON(){
		return new JSONObject(this);
	}
}
