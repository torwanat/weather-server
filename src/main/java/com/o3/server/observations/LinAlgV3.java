package com.o3.server.observations;

import java.util.ArrayList;
import java.util.List;

import static java.lang.Math.pow;
import static java.lang.Math.sqrt;

/**
 * Needed calculations for state vector to orbital element conversion
 */
public class LinAlgV3 {
	public static Double dotProduct(List<Double> v1, List<Double> v2) {
		double sum = 0;

		for (int i = 0; i < 3; i++) {
			sum += v1.get(i) * v2.get(i);
		}

		return sum;
	}

	public static List<Double> crossProduct(List<Double> v1, List<Double> v2) {
		List<Double> v3 = new ArrayList<>();
		double i = (v1.get(1) * v2.get(2)) - (v1.get(2) * v2.get(1));
		double j = (v1.get(2) * v2.get(0)) - (v1.get(0) * v2.get(2));
		double k = (v1.get(0) * v2.get(1)) - (v1.get(1) * v2.get(0));
		v3.add(i); v3.add(j); v3.add(k);

		return v3;
	}

	public static Double norm(List<Double> v1) {
		double sum = 0;
		for (int i = 0; i < v1.size(); i++) {
			sum += pow(v1.get(i), 2);
		}

		return sqrt(sum);
	}

	public static List<Double> divideBy(List<Double> v, double divisor) {
		List<Double> divided = new ArrayList<Double>();
		for (int i = 0; i < v.size(); i++) {
			divided.add(v.get(i) / divisor);
		}

		return divided;
	}

	public static List<Double> subtract(List<Double> v1, List<Double> v2) {
		List<Double> subtracted = new ArrayList<Double>();
		for (int i = 0; i < v1.size(); i++) {
			subtracted.add(v1.get(i) - v2.get(i));
		}

		return subtracted;
	}

}
