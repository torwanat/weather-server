package com.o3.server.observations;

import org.json.JSONObject;
import org.json.JSONPropertyName;

import java.util.ArrayList;
import java.util.List;

public class StateVector {
	private ArrayList<Double> positionAu;
	private ArrayList<Double> velocityAuPerDay;

	public StateVector(List<Double> positionAu, List<Double> velocityAuPerDay) {
		this.positionAu = new ArrayList<>(positionAu);
		this.velocityAuPerDay = new ArrayList<>(velocityAuPerDay);
	}

	public StateVector(StateVector that) {
		this.positionAu = new ArrayList<>(that.getPositionAu().stream().map(Double::valueOf).toList());
		this.velocityAuPerDay = new ArrayList<>(that.getVelocityAuPerDay().stream().map(Double::valueOf).toList());
	}

	@JSONPropertyName("position_au")
	public List<Double> getPositionAu() {
		return positionAu;
	}

	public void setPositionAu(List<Double> positionAu) {
		if (positionAu != null)
			this.positionAu = new ArrayList<>(positionAu);
		else
			this.positionAu = null;
	}

	@JSONPropertyName("velocity_au_per_day")
	public List<Double> getVelocityAuPerDay() {
		return velocityAuPerDay;
	}

	public void setVelocityAuPerDay(List<Double> velocityAuPerDay) {
		if (velocityAuPerDay != null)
			this.velocityAuPerDay = new ArrayList<>(velocityAuPerDay);
		else
			this.velocityAuPerDay = null;
	}

	public JSONObject toJson() {
		return new JSONObject(this);
	}

	@Override
	public String toString() {
		return "List<Double> [positionAu=" + positionAu + ", velocityAuPerDay=" + velocityAuPerDay + "]";
	}
}
