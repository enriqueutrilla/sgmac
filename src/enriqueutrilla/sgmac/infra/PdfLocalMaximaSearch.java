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
 * PdfLocalMaximaSearch.java
 * Class that finds which sampled points are a local maxima, meaning that
 * no other sampled point has a higher PDF value in an n-sphere of radius
 * threshold in Mahalanobis distance.
 */	

import java.util.ArrayList;
import java.util.List;

import enriqueutrilla.sgmac.dm.SampledPdfPoint;
import enriqueutrilla.sgmac.util.Distances;

public class PdfLocalMaximaSearch {

	private double threshold;
	private double[] sigmas2;

	public PdfLocalMaximaSearch(double[] sigmas2, double threshold) {
		this.sigmas2 = sigmas2;
		this.threshold = threshold;
	}

	public List<SampledPdfPoint> getLocalMaxima(IndexedDataset<SampledPdfPoint> samples) {

		List<SampledPdfPoint> maxima = new ArrayList<>();

		for (SampledPdfPoint u : samples.getAll()) {
			boolean localMaximum = true;
			List<SampledPdfPoint> neighborhood = samples.getNeighborhood(u.getCoordinates(), threshold);
			if (neighborhood.size() > 2) {
				for (SampledPdfPoint x : neighborhood) {
					if (x.getPdfValue() > u.getPdfValue() && Distances.mahalanobis(x, u, sigmas2) < threshold) {
						localMaximum = false;
					}
				}
				if (localMaximum) {
					u.setLocalMaximum(true);
					maxima.add(u);
				}
			} else {
				SampledPdfPoint closestSample = getClosestSample(u, samples, sigmas2);
				if (closestSample.getPdfValue() < u.getPdfValue()) {
					u.setLocalMaximum(true);
					maxima.add(u);
				}
			}
		}
		return maxima;
	}

	protected SampledPdfPoint getClosestSample(SampledPdfPoint current, IndexedDataset<SampledPdfPoint> pdfSamplesIdx,
			double[] sigmas2) {

		double minDistance = Double.MAX_VALUE;
		SampledPdfPoint closestSample = null;

		// Try first looking for data at 3 sigmas, then data at 7 sigmas, then all
		double[] radius = new double[] { 3.0, 7.0, -1 };
		int i = 0;

		while (closestSample == null && i < radius.length) {
			for (SampledPdfPoint pdfSample : pdfSamplesIdx.getNeighborhood(current.getCoordinates(), radius[i])) {

				double distance = Distances.mahalanobis(current, pdfSample, sigmas2);
				if (distance > 0 && distance < minDistance) {
					minDistance = distance;
					closestSample = pdfSample;
				}

			}
			i++;
		}

		return closestSample;
	}
}
