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
 * Mac.java
 * Java implementation of the original Modal Association Clustering algorithm.
 * Reimplemented by translating the Matlab code available at:
 *    http://personal.psu.edu/jol2/hmac/
 */

import java.util.Arrays;
import java.util.List;

import enriqueutrilla.sgmac.dm.Cluster;
import enriqueutrilla.sgmac.dm.DatasetPoint;
import enriqueutrilla.sgmac.infra.IndexedDataset;
import enriqueutrilla.sgmac.infra.ModalClustering;
import enriqueutrilla.sgmac.infra.ModalEmUpdater;
import enriqueutrilla.sgmac.infra.PathUpdater;

public class BasicMac implements Mac{
	
	private double[] sigmas;
	private double[] sigmas2;
	
	private double clusterPositionTolerance = 0.001;
	
	private List<Cluster> clusters;
	
	public BasicMac(double[] sigmas){
		
		this.sigmas = sigmas;
		this.sigmas2 = new double[sigmas.length];
		for(int i=0;i<sigmas.length;i++){
			this.sigmas2[i] = sigmas[i]*sigmas[i];
		}
	}
	
	public String getMacSubtype(){
		return "Basic MAC";
	}
	
	public String getParamDesc(){
		return "s^2=" + Arrays.toString(sigmas2);
	}
	
	public void cluster(List<DatasetPoint> points) {
		
		
		IndexedDataset<DatasetPoint> kernelsIdx = new IndexedDataset<>(points,sigmas, 2);
		kernelsIdx.indexData();
		
		PathUpdater updaterEm = new ModalEmUpdater(kernelsIdx, sigmas2);
		
		ModalClustering clusteringEm = new ModalClustering(updaterEm, sigmas2, clusterPositionTolerance);
		
		clusters = clusteringEm.cluster(points);
	}

	public List<Cluster> getClusters() {
		return clusters;
	}
	
	
}
