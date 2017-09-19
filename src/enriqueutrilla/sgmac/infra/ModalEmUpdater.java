package enriqueutrilla.sgmac.infra;

import java.util.List;

import enriqueutrilla.sgmac.dm.DatasetPoint;
import enriqueutrilla.sgmac.dm.PathPoint;
import enriqueutrilla.sgmac.dm.Point;
import enriqueutrilla.sgmac.util.Distances;
import enriqueutrilla.sgmac.util.Gaussian;

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
 * ModalEmUpdater.java
 * Path updater of the BasicMac algorithm.
 * Reimplemented by translating the Matlab code available at:
 *    http://personal.psu.edu/jol2/hmac/
 */	
	
public class ModalEmUpdater extends PathUpdater{

	protected IndexedDataset<DatasetPoint> kernels = null;
	protected double[] sigma2;
	protected double threshold = 1e-6;
	
	public ModalEmUpdater(IndexedDataset<DatasetPoint> kernels, double[] sigma2) {
		this.kernels = kernels;
		this.sigma2 = sigma2;
	}
	
	@Override
	public void update(PathPoint current) {
		
//  Equivalent Matlab code:
//		f=mvnpdf(x0,data,diag(sigma^2*ones(dim,1)));
//        p=f/sum(f); // update p
//        x0=p'*data; // update x
		
		double sum = 0;
		List<DatasetPoint> closeKernels = kernels.getNeighborhood(current.getCoordinates(), 8);
		double[] f = new double[closeKernels.size()]; 
		for(int i=0;i<closeKernels.size();i++) {
			Point kernel=closeKernels.get(i);
			f[i] = Gaussian.getUnnormalizedGaussian(current, kernel, sigma2);
			sum += f[i];
		}
		double[] next = new double[current.dim()];
		for(int i=0;i<closeKernels.size();i++) {
			double p = f[i]/sum;
			double[] kernel = closeKernels.get(i).getCoordinates();
			for (int j=0; j<current.dim();j++) {
				next[j]+=p*kernel[j];
			}
		}
		double distance = Distances.mahalanobis(current.getCoordinates(), next, sigma2);
		current.setConverged(distance < threshold);
		current.setCoordinates(next);
		
	}
	
	

}
