package com.o3.server.observations;

import org.json.JSONObject;
import org.json.JSONPropertyName;

public class OrbitalElements {
	private double semiMajorAxisAu;
	private double eccentricity;
	private double inclinationDeg;
	private double longitudeAscendingNodeDeg;
	private double argumentOfPeriapsisDeg;
	private double meanAnomalyDeg;

	public OrbitalElements(double semiMajorAxisAu, double eccentricity, double inclinationDeg,
						   double longitudeAscendingNodeDeg, double argumentOfPeriapsisDeg, double meanAnomalyDeg){
		this.semiMajorAxisAu = semiMajorAxisAu;
		this.eccentricity = eccentricity;
		this.inclinationDeg = inclinationDeg;
		this.longitudeAscendingNodeDeg = longitudeAscendingNodeDeg;
		this.argumentOfPeriapsisDeg = argumentOfPeriapsisDeg;
		this.meanAnomalyDeg = meanAnomalyDeg;
	}

	@JSONPropertyName("semi_major_axis_au")
	public double getSemiMajorAxisAu() {
		return semiMajorAxisAu;
	}

	public void setSemiMajorAxisAu(double semiMajorAxisAu) {
		this.semiMajorAxisAu = semiMajorAxisAu;
	}

	@JSONPropertyName("eccentricity")
	public double getEccentricity() {
		return eccentricity;
	}

	public void setEccentricity(double eccentricity) {
		this.eccentricity = eccentricity;
	}

	@JSONPropertyName("inclination_deg")
	public double getInclinationDeg() {
		return inclinationDeg;
	}

	public void setInclinationDeg(double inclinationDeg) {
		this.inclinationDeg = inclinationDeg;
	}

	@JSONPropertyName("longitude_ascending_node_deg")
	public double getLongitudeAscendingNodeDeg() {
		return longitudeAscendingNodeDeg;
	}

	public void setLongitudeAscendingNodeDeg(double longitudeAscendingNodeDeg) {
		this.longitudeAscendingNodeDeg = longitudeAscendingNodeDeg;
	}

	@JSONPropertyName("argument_of_periapsis_deg")
	public double getArgumentOfPeriapsisDeg() {
		return argumentOfPeriapsisDeg;
	}

	public void setArgumentOfPeriapsisDeg(double argumentOfPeriapsisDeg) {
		this.argumentOfPeriapsisDeg = argumentOfPeriapsisDeg;
	}

	@JSONPropertyName("mean_anomaly_deg")
	public double getMeanAnomalyDeg() {
		return meanAnomalyDeg;
	}

	public void setMeanAnomalyDeg(double meanAnomalyDeg) {
		this.meanAnomalyDeg = meanAnomalyDeg;
	}

	public JSONObject toJSON() {
		return new JSONObject(this);
	}
}
