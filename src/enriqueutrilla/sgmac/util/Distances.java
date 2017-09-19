package enriqueutrilla.sgmac.util;

/**
 * Copyright 2017 Enrique Utrilla Molina
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * 
 * Distances.java
 * Static utilities for calculation of different measures of distance.
 */

import org.apache.commons.math3.util.FastMath;

import enriqueutrilla.sgmac.dm.Point;

public class Distances {

	public static double mahalanobis(Point x, Point u, double sigma2[]) {
		return mahalanobis(x.getCoordinates(), u.getCoordinates(), sigma2);
	}
	
	public static double mahalanobis(double[] x, double[] u, double sigma2[]) {
		double accum =0;
		for(int i=0; i<x.length; i++) {
			double diff = x[i] - u[i];
			accum += diff * diff / sigma2[i];
		}
		return FastMath.sqrt(accum);
	}
	
	public static double norm(double[] x, double sigma2[]) {
		double accum =0;
		for(int i=0; i<x.length; i++) {
			accum += x[i] * x[i] / sigma2[i];
		}
		return FastMath.sqrt(accum);
	}
	
	public static void normalise(double[] x, double sigma2[]) {
		double norm = norm(x, sigma2);
		for(int i=0; i<x.length; i++) {
			x[i] /= norm;
		}
	}
	
	public static boolean inSquareTolerance(Point x, Point u, double[] delta) {
		boolean inside = true;
		int i = 0;
		while(inside && i<x.dim()) {
			inside &= Math.abs(x.get(i) - u.get(i)) < delta[i];
			i++;
		}
		return inside;
	}
}
