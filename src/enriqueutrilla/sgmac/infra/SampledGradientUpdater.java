package enriqueutrilla.sgmac.infra;

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
 * SampledGradientUpdater.java
 * Path updater of the SgMac algorithm.
 */

import java.util.ArrayList;
import java.util.List;

import enriqueutrilla.sgmac.dm.PathPoint;
import enriqueutrilla.sgmac.dm.SampledPdfPoint;
import enriqueutrilla.sgmac.util.Distances;

public class SampledGradientUpdater extends PathUpdater {

	protected IndexedDataset<SampledPdfPoint> pdfSamplesIdx;
	protected IndexedDataset<SampledPdfPoint> localMaximaIdx;
	protected double[] sigmas;
	protected double[] sigmas2;
	protected double stepFactor;
	protected double[] step;
	protected double distanceThreshold;

	protected List<double[]> lastPoints = new ArrayList<>();
	protected int index = 0;
	protected int maxPoints = 5;
	protected double[] lastAvgPosition;

	public SampledGradientUpdater(IndexedDataset<SampledPdfPoint> pdfSamplesIdx,
			IndexedDataset<SampledPdfPoint> localMaximaIdx, double[] sigmas2, double stepFactor,
			double distanceThreshold) {
		this.pdfSamplesIdx = pdfSamplesIdx;
		this.localMaximaIdx = localMaximaIdx;
		this.sigmas2 = sigmas2;
		this.sigmas = new double[sigmas2.length];
		for (int i = 0; i < sigmas2.length; i++) {
			this.sigmas[i] = Math.sqrt(sigmas2[i]);
		}

		this.stepFactor = stepFactor;// *sigmaNorm;
		this.distanceThreshold = distanceThreshold;
	}

	@Override
	public void update(PathPoint current) {
		// Estimate gradient in <current>

		// Check 1st convergence condition (being close enough to a known local maximum)
		SampledPdfPoint closestSample = null;
		for (SampledPdfPoint pdfSample : localMaximaIdx.getNeighborhood(current.getCoordinates(), distanceThreshold)) {
			double distance = Distances.mahalanobis(current, pdfSample, sigmas2);
			if (distance < distanceThreshold) {
				closestSample = pdfSample;
				break;
			}
		}

		if (closestSample != null) {
			current.setCoordinates(closestSample.getCoordinates());
			current.setConverged(true);
		} else {
			double[] increment = new double[current.dim()];

			closestSample = getClosestSample(current);
			for (int i = 0; i < increment.length; i++) {
				increment[i] = stepFactor * closestSample.getPdfGradient()[i];
			}

			current.add(increment);

			double norm = Distances.norm(increment, sigmas2);
			if (norm < 0.001) {
				current.setConverged(true);
			}
		}
	}

	protected SampledPdfPoint getClosestSample(PathPoint current) {

		double minDistance = Double.MAX_VALUE;
		SampledPdfPoint closestSample = null;

		// Try first looking for data at 3 sigmas, then data at 7 sigmas, then all
		double[] radius = new double[] { 3.0, 7.0, -1 };
		int i = 0;

		while (closestSample == null && i < radius.length) {
			for (SampledPdfPoint pdfSample : pdfSamplesIdx.getNeighborhood(current.getCoordinates(), radius[i])) {

				double distance = Distances.mahalanobis(current, pdfSample, sigmas2);
				if (distance < minDistance) {
					minDistance = distance;
					closestSample = pdfSample;
				}

				// if minDistance = 0 there is no need to check any other
				if (minDistance == 0) {
					break;
				}
			}
			i++;
		}

		return closestSample;
	}

}
