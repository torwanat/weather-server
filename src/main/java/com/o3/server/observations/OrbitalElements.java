package com.o3.server.observations;

import org.json.JSONObject;
import org.json.JSONPropertyName;

import java.util.Arrays;
import java.util.List;

import static java.lang.Math.*;

public class OrbitalElements {
	private Double semiMajorAxisAu;
	private Double eccentricity;
	private Double inclinationDeg;
	private Double longitudeAscendingNodeDeg;
	private Double argumentOfPeriapsisDeg;
	private Double meanAnomalyDeg;

	public OrbitalElements(double semiMajorAxisAu, double eccentricity, double inclinationDeg,
			double longitudeAscendingNodeDeg, double argumentOfPeriapsisDeg, double meanAnomalyDeg) {
		this.semiMajorAxisAu = semiMajorAxisAu;
		this.eccentricity = eccentricity;
		this.inclinationDeg = inclinationDeg;
		this.longitudeAscendingNodeDeg = longitudeAscendingNodeDeg;
		this.argumentOfPeriapsisDeg = argumentOfPeriapsisDeg;
		this.meanAnomalyDeg = meanAnomalyDeg;
	}

	public OrbitalElements(OrbitalElements that) {
		this.semiMajorAxisAu = that.getSemiMajorAxisAu();
		this.eccentricity = that.getEccentricity();
		this.inclinationDeg = that.getInclinationDeg();
		this.longitudeAscendingNodeDeg = that.getLongitudeAscendingNodeDeg();
		this.argumentOfPeriapsisDeg = that.getArgumentOfPeriapsisDeg();
		this.meanAnomalyDeg = that.getMeanAnomalyDeg();
	}

	/**
	 * Instantiate orbital elements from a state vector
	 *
	 * @param stateVector Vector to create elements from
	 *
	 * @return OrbitalElements
	 */
	public OrbitalElements(StateVector stateVector) {
		// Position and velocity magnitudes
		List<Double> v_vec = stateVector.getVelocityAuPerDay();
		List<Double> r_vec = stateVector.getPositionAu();
		double r = LinAlgV3.norm(r_vec);
		double v = LinAlgV3.norm(v_vec);
		double v_r = LinAlgV3.dotProduct(LinAlgV3.divideBy(r_vec, r), v_vec);
		double v_p = (double)sqrt(pow(v, 2) - pow(v_r, 2));
		double mu = 3.986e5; // Don't account for different masses

		// Orbital angular momentum
		List<Double> h_vec = LinAlgV3.crossProduct(r_vec, v_vec);
		double h = LinAlgV3.norm(h_vec);

		// Calculate inclination and convert to degrees
		this.inclinationDeg = (180.0 / PI) * acos(h_vec.get(2) / h);

		// Right ascension of the ascending node
		List<Double> K = Arrays.asList(0.0, 0.0, 1.0);
		List<Double> N_vec = LinAlgV3.crossProduct(K, h_vec);
		double N = LinAlgV3.norm(N_vec);

		double Omega = acos(N_vec.get(0) / N);
		if (N_vec.get(1) < 0) {
			Omega = 2.0 * PI - Omega;
		}
		this.longitudeAscendingNodeDeg = (180.0 / PI) * Omega;

		List<Double> e_vec = LinAlgV3.subtract(
			LinAlgV3.divideBy(LinAlgV3.crossProduct(v_vec, h_vec), mu),
			LinAlgV3.divideBy(r_vec, r)
		);
		this.eccentricity = LinAlgV3.norm(e_vec);

		this.argumentOfPeriapsisDeg = (180.0 / PI) * (2 * PI - acos(
			LinAlgV3.dotProduct(N_vec, e_vec) / (N * this.eccentricity)
		));

		double trueAnomaly = acos(
			LinAlgV3.dotProduct(LinAlgV3.divideBy(r_vec, r),
			LinAlgV3.divideBy(e_vec, this.eccentricity))
		);
		double oppositeSide = sin(trueAnomaly) * sqrt(1.0 - pow(this.eccentricity, 2));
		double eccentricAnomaly = atan2(
			oppositeSide,
			this.eccentricity + cos(trueAnomaly)
		);

		double eccentricityForKepler = this.eccentricity * (
			oppositeSide /(1.0 + this.eccentricity * cos(trueAnomaly))
		);
		this.meanAnomalyDeg = (180.0 / PI) * (eccentricAnomaly - eccentricityForKepler);

		this.semiMajorAxisAu = 1 / (2 / r - pow(v, 2) / mu);

	}

	@JSONPropertyName("semi_major_axis_au")
	public double getSemiMajorAxisAu() {
		return semiMajorAxisAu;
	}

	public void setSemiMajorAxisAu(Double semiMajorAxisAu) {
		this.semiMajorAxisAu = semiMajorAxisAu;
	}

	@JSONPropertyName("eccentricity")
	public double getEccentricity() {
		return eccentricity;
	}

	public void setEccentricity(Double eccentricity) {
		this.eccentricity = eccentricity;
	}

	@JSONPropertyName("inclination_deg")
	public double getInclinationDeg() {
		return inclinationDeg;
	}

	public void setInclinationDeg(Double inclinationDeg) {
		this.inclinationDeg = inclinationDeg;
	}

	@JSONPropertyName("longitude_ascending_node_deg")
	public double getLongitudeAscendingNodeDeg() {
		return longitudeAscendingNodeDeg;
	}

	public void setLongitudeAscendingNodeDeg(Double longitudeAscendingNodeDeg) {
		this.longitudeAscendingNodeDeg = longitudeAscendingNodeDeg;
	}

	@JSONPropertyName("argument_of_periapsis_deg")
	public double getArgumentOfPeriapsisDeg() {
		return argumentOfPeriapsisDeg;
	}

	public void setArgumentOfPeriapsisDeg(Double argumentOfPeriapsisDeg) {
		this.argumentOfPeriapsisDeg = argumentOfPeriapsisDeg;
	}

	@JSONPropertyName("mean_anomaly_deg")
	public double getMeanAnomalyDeg() {
		return meanAnomalyDeg;
	}

	public void setMeanAnomalyDeg(Double meanAnomalyDeg) {
		this.meanAnomalyDeg = meanAnomalyDeg;
	}

	public JSONObject toJson() {
		return new JSONObject(this);
	}

	@Override
	public String toString() {
		return "OrbitalElements [semiMajorAxisAu=" + semiMajorAxisAu + ", eccentricity=" + eccentricity
				+ ", inclinationDeg=" + inclinationDeg + ", longitudeAscendingNodeDeg=" + longitudeAscendingNodeDeg
				+ ", argumentOfPeriapsisDeg=" + argumentOfPeriapsisDeg + ", meanAnomalyDeg=" + meanAnomalyDeg + "]";
	}
}
