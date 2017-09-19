package enriqueutrilla.sgmac;

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
 * SgMac.java
 * Implementation of Sampled Gradient Modal Association Clustering algorithm
 */

import java.util.Arrays;
import java.util.List;

import enriqueutrilla.sgmac.dm.Cluster;
import enriqueutrilla.sgmac.dm.DatasetPoint;
import enriqueutrilla.sgmac.dm.Point;
import enriqueutrilla.sgmac.dm.SampledPdfPoint;
import enriqueutrilla.sgmac.infra.IndexedDataset;
import enriqueutrilla.sgmac.infra.ModalClustering;
import enriqueutrilla.sgmac.infra.PathUpdater;
import enriqueutrilla.sgmac.infra.PdfGradientSampler;
import enriqueutrilla.sgmac.infra.PdfLocalMaximaSearch;
import enriqueutrilla.sgmac.infra.SampleSelection;
import enriqueutrilla.sgmac.infra.SampledGradientUpdater;
import enriqueutrilla.sgmac.util.Console;

public class SgMac implements Mac {

	protected double[] sigmas2;
	protected double[] sigmas;

	// parameters for first sampling
	protected double decimationRatio=1;
	protected int seed1=0;

	// parameters for local maxima search
	protected double locMaxDistanceThreshold=7;

	// parameters for second sampling
	protected int targetSamplePoints=Integer.MAX_VALUE;
	protected int seed2=0;

	// parameters for clustering
	protected int maxIterations=1000;
	protected double stepFactor = 1;

	// This threshold is used in the regular MAC algorithm.
	// It is not really useful in SG-MAC since "modes" are always exactly
	// one of the selected sampling points
	protected double clusterPositionTolerance = 0.01;

	protected IndexedDataset<SampledPdfPoint> sampledPdfIdx = null;
	protected IndexedDataset<SampledPdfPoint> localMaximaIdx = null;
	protected List<Cluster> clusters = null;

	protected boolean initialized = false;

	protected int cellSizeFactor = 2;

	
	public SgMac(double[] sigmas) {

		this.sigmas = sigmas;
		this.sigmas2 = new double[sigmas.length];
		for (int i = 0; i < sigmas.length; i++) {
			this.sigmas2[i] = sigmas[i] * sigmas[i];
		}
	}
	
	
    // Fluid style API to set properties
	public SgMac withDecimationRatio(double decimationRatio) {
		this.decimationRatio = decimationRatio;
		return this;
	}

	public SgMac withSeed1(int seed1) {
		this.seed1 = seed1;
		return this;
	}

	public SgMac withTargetSamplePoints(int targetSamplePoints) {
		this.targetSamplePoints = targetSamplePoints;
		return this;
	}

	public SgMac withSeed2(int seed2) {
		this.seed2 = seed2;
		return this;
	}

	public SgMac withMaxIterations(int maxIterations) {
		this.maxIterations = maxIterations;
		return this;
	}

	public SgMac withStepFactor(double stepFactor) {
		this.stepFactor = stepFactor;
		return this;
	}

	public SgMac withClusterPositionTolerance(double clusterPositionTolerance) {
		this.clusterPositionTolerance = clusterPositionTolerance;
		return this;
	}
	
	public String getMacSubtype() {
		return "SG-MAC";
	}

	public String getParamDesc() {
		return "s^2=" + Arrays.toString(sigmas2) + ", f=" + decimationRatio + ", s1=" + seed1 + ", r="
				+ locMaxDistanceThreshold + ", n=" + targetSamplePoints + ", s2=" + seed2 + ", d=" + stepFactor;
	}

	
	
	public Point findMode(Point initial, boolean trace) {
		PathUpdater updater = new SampledGradientUpdater(sampledPdfIdx, localMaximaIdx, sigmas2, stepFactor,
				locMaxDistanceThreshold);
		updater.setMaxIterations(maxIterations);
		return updater.findMode(initial, trace);
	}

	public void initialize(List<DatasetPoint> points) {

		// --------- Stage 1: Selection of sample points from the dataset ---------

		// The purposes of this decimation are:
		// - reduce a bit the number of calculations, although this affects accuracy vs MAC
		// - make sure the samples fit into the available memory
		// Usually this step is skipped (decimation==1.0)

		List<DatasetPoint> stage1Points = null;
		if (decimationRatio >= 1.0) {
			stage1Points = points;
			Console.log("No decimation, proceeding with " + stage1Points.size() + " points");

		} else {
			SampleSelection<DatasetPoint> stage1 = new SampleSelection<DatasetPoint>().withRatio(decimationRatio)
					.withSeed(seed1);
			stage1Points = stage1.sample(points);
			Console.log("Selected " + stage1Points.size() + " points for sampling");
		}

		// --------- Stage 2: Calculation of PDF and its gradient at each sampling point ---------
		
		Console.log("Calculating PDF sampling at " + stage1Points.size() + " points...");

		IndexedDataset<DatasetPoint> kernelsIdx = new IndexedDataset<>(points, sigmas, 2, 2);
		kernelsIdx.indexData();

		PdfGradientSampler<DatasetPoint> stage2 = new PdfGradientSampler<>(stage1Points, sigmas2);
		// we use the full dataset to calculate the PDF at each point
		List<SampledPdfPoint> stage2Points = stage2.samplePdf(kernelsIdx);

		Console.log("Calculation of PDF sampling completed");
		

		// --------- Stage 3: Identification of local maxima. ---------
		Console.log("Looking for local maxima");

		IndexedDataset<SampledPdfPoint> sampledIdx = new IndexedDataset<>(stage2Points, sigmas, 2, 2);
		sampledIdx.indexData();

		PdfLocalMaximaSearch stage3 = new PdfLocalMaximaSearch(sigmas2, locMaxDistanceThreshold);
		localMaximaIdx = new IndexedDataset<>(stage3.getLocalMaxima(sampledIdx), sigmas, 2, 2);
		localMaximaIdx.indexData();

		Console.log(localMaximaIdx.getAll().size() + " local maxima found");

		// --------- Stage 4: Decimate PDF smaples ---------
		// The purposes of this decimation are:
		// - reduce a bit the number of calculations while doing the clustering
		// - make sure the samples fit into the available memory
		// Tests show that the clustering is less sensitive to decimations at this point
		// Nevertheless, it can have an impact in accuracy, and convergence might be slower
		// in particular with relatively small values of sigma
		// By default the target is Integer.MAX_VALUE, so no decimation is performed

		if (targetSamplePoints >= stage2Points.size()) {
			Console.log("No further resampling is required");
			sampledPdfIdx = new IndexedDataset<>(stage2Points, sigmas, 2, 2);
		} else {
			Console.log("Selecting points for resampling");
			double ratio = (1.0 * targetSamplePoints) / stage2Points.size();

			SampleSelection<SampledPdfPoint> sampler = new SampleSelection<SampledPdfPoint>().withRatio(ratio)
					.withSeed(seed2).withForceInclude(localMaximaIdx.getAll());
			List<SampledPdfPoint> samples = sampler.sample(stage2Points);

			Console.log("Selection of " + samples.size() + " points completed");

			sampledPdfIdx = new IndexedDataset<>(samples, sigmas, 2, 2);
		}

		initialized = true;
	}

	public void cluster(List<DatasetPoint> points) {

		if (!initialized) {
			initialize(points);
		}

		// --------- Stage 4: Path calculation and classification of each data point into a cluster ---------
		Console.log("Starting clustering...");

		PathUpdater updater = new SampledGradientUpdater(sampledPdfIdx, localMaximaIdx, sigmas2, stepFactor,
				locMaxDistanceThreshold);
		updater.setMaxIterations(maxIterations);
		ModalClustering clustering = new ModalClustering(updater, sigmas2, clusterPositionTolerance);

		this.clusters = clustering.cluster(points);

		Console.log("Clustering completed.");

		// print convergence stats
		Console.log(updater.getConvergenceStats());

	}

	public List<SampledPdfPoint> getSampledPdf() {
		return sampledPdfIdx.getAll();
	}

	public List<SampledPdfPoint> getLocalMaxima() {
		return localMaximaIdx.getAll();
	}

	public List<Cluster> getClusters() {
		return clusters;
	}

}
