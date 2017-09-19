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
 * ModalClustering.java
 * Basic loop of a Modal Clustering algorithm.
 * Used by both BasicMac and SgMac
 */

import java.util.ArrayList;
import java.util.List;

import enriqueutrilla.sgmac.dm.Cluster;
import enriqueutrilla.sgmac.dm.DatasetPoint;
import enriqueutrilla.sgmac.dm.PathPoint;
import enriqueutrilla.sgmac.dm.Point;
import enriqueutrilla.sgmac.util.Console;
import enriqueutrilla.sgmac.util.Distances;

public class ModalClustering {
	
	protected PathUpdater updater;
	protected double[] sigma2;
	protected double threshold;

	public ModalClustering(PathUpdater updater, double sigma2[], double threshold) {
		super();
		this.updater = updater;
		this.sigma2 = sigma2;
		this.threshold = threshold;
	}

	public List<Cluster> cluster(List<DatasetPoint> points){
		
		Cluster notConverged = new Cluster(null);
		List<Cluster> clusters = new ArrayList<>();
		
		int counter = 0;
		for (DatasetPoint point: points) {
			if (counter>0 && counter%10000==0){
				Console.log("Processing point " + counter + "/" + points.size());
			}
			PathPoint mode = updater.findMode(point);
			if (!mode.isConverged()) {
				notConverged.add(point);
			} else {
				Cluster cluster = findCluster(clusters, mode);
				if(cluster == null) {
					cluster = new Cluster(mode);
					clusters.add(cluster);
				}
				cluster.add(point);
			}
			counter++;
		}
		if (notConverged.getMembers().size()>0) {
			clusters.add(notConverged);
		}
		return clusters;
	}
	
	protected Cluster findCluster(List<Cluster> clusters, Point mode) {
		Cluster result = null;
		for(Cluster cluster:clusters) {
			if (Distances.mahalanobis(mode, cluster.getMode(), sigma2)<threshold) {
				result = cluster;
				continue;
			}
		}
		return result;
	}
}
