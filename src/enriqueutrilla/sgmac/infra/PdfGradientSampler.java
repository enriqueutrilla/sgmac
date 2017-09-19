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
 * PdfGradientSampler.java
 * Class that calculates the value and gradient of the PDF function at a given point.
 */	

import java.util.ArrayList;
import java.util.List;

import enriqueutrilla.sgmac.dm.DatasetPoint;
import enriqueutrilla.sgmac.dm.Point;
import enriqueutrilla.sgmac.dm.SampledPdfPoint;
import enriqueutrilla.sgmac.util.Console;
import enriqueutrilla.sgmac.util.Gaussian;

public class PdfGradientSampler<T extends DatasetPoint> {
	
	private List<T> samplePoints;
	private double[] sigma2;
	
	private int neighborhoodInSigmas = 8;
	private boolean logStats = true;
	
	private long accumulatedRetrievalTime = 0;
	private long accumulatedKernelsRetrieved = 0;
	private long accumulatedPdfCalculationTime = 0;
	
	public PdfGradientSampler(List<T> samplePoints, double[] sigma2){
		this.samplePoints = samplePoints;
		this.sigma2 = sigma2;
	}
	
	public int getNeighborhoodInSigmas() {
		return neighborhoodInSigmas;
	}

	public void setNeighborhoodInSigmas(int neighborhoodInSigmas) {
		this.neighborhoodInSigmas = neighborhoodInSigmas;
	}

	public boolean isLogStats() {
		return logStats;
	}

	public void setLogStats(boolean logStats) {
		this.logStats = logStats;
	}

	
	
	public List<SampledPdfPoint> samplePdf(IndexedDataset<T> kernels){
		
		List<SampledPdfPoint> samples = new ArrayList<>(samplePoints.size());

		for(T point:samplePoints) {
			SampledPdfPoint sample = getSampledPdf(point, kernels, sigma2);
			samples.add(sample);
		}
		
		if (logStats){
			Console.log("Avg retrieval time: " + accumulatedRetrievalTime/samplePoints.size() + " ns");
			Console.log("Avg kernels retrieved  time: " + accumulatedKernelsRetrieved/samplePoints.size());
			Console.log("Avg PDF calculation time: " + accumulatedPdfCalculationTime/samplePoints.size() + " ns");
		}
			
		return samples;
	}
	
	public SampledPdfPoint getSampledPdf(final T x, IndexedDataset<T> ds, double sigma2[]) {
				
		long startTime, endTime;
		
		startTime = System.nanoTime();
		List<T> kernels = ds.getNeighborhood(x.getCoordinates(), neighborhoodInSigmas);
		
		endTime = System.nanoTime();
		
		accumulatedRetrievalTime += (endTime-startTime)/1000;
		
		accumulatedKernelsRetrieved += kernels.size();
		
		double sum=0;
		List<T> closeKernels = ds.getNeighborhood(x.getCoordinates(), 8);
		double[] f = new double[closeKernels.size()]; 
		for(int i=0;i<closeKernels.size();i++) {
			Point kernel=closeKernels.get(i);
			f[i] = Gaussian.getUnnormalizedGaussian(x, kernel, sigma2);
			sum += f[i];
		}
		double[] next = new double[x.dim()];
		for(int i=0;i<closeKernels.size();i++) {
			double p = f[i]/sum;
			double[] kernel = closeKernels.get(i).getCoordinates();
			for (int j=0; j<x.dim();j++) {
				next[j]+=p*kernel[j];
			}
		}
		double[] gradient = new double[x.dim()];
		for (int i=0;i<x.dim();i++){
			gradient[i] = next[i]-x.getCoordinates()[i];
		}
		final SampledPdfPoint point = new SampledPdfPoint(x, sum, gradient);
		
		endTime = System.nanoTime();
		
		accumulatedPdfCalculationTime += (endTime-startTime)/1000;

		return point;
	}
	
}
