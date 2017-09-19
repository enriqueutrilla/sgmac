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
 * Gaussian.java
 * Static functions calculating Gaussian functions.
 */

import org.apache.commons.math3.util.FastMath;

import enriqueutrilla.sgmac.dm.Point;

public class Gaussian {
	
	public static double getUnnormalizedGaussian(Point x, Point u, double sigma2[]) {
		double distance = Distances.mahalanobis(x, u, sigma2);
		return FastMath.exp(-0.5 * distance);
	}
	
	public static double[] getUnnormalizedGradient(Point x, Point u, double sigma2[], double pdf) {

		double[] gradient = new double[x.dim()];
		for(int i=0; i<x.dim(); i++) {
			gradient[i] = ((u.get(i) - x.get(i)) / sigma2[i]) * pdf ;
		}
		
		return gradient;
	}

}
