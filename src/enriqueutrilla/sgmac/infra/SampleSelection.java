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
 * SampleSelection.java
 * Implementation of a decimation of a list of DatasetPoints.
 */

import java.util.ArrayList;
import java.util.List;

import enriqueutrilla.sgmac.dm.DatasetPoint;

public class SampleSelection<T extends DatasetPoint> {
	
	private int samplesInterval = 1;
	private int seed = 0;
	private List<? extends T> forceInclude = null;
	
	public List<T> sample(List<? extends T> inputs){
//		int samplesInterval = (int)Math.ceil(1.0/ratio);
//		if(maxPoints>0){
//			int targetRatio = (int)Math.ceil(inputs.size()/maxPoints);			
//			samplesInterval = Math.max(samplesInterval, targetRatio);
//		}
		
		int expectedSize = (int)Math.ceil(inputs.size()/samplesInterval); 
		
		List<T> outputs = new ArrayList<>(expectedSize);
		
		if (forceInclude != null){
			outputs.addAll(forceInclude);
		}
		
		int index = seed%samplesInterval;
		while (index<inputs.size()){
			T point = inputs.get(index);
			if (!filterOut(point, forceInclude)){
				outputs.add(point);
			}
			index += samplesInterval;
		}
				
		return outputs;
	}
	
	public SampleSelection<T> withRatio(double ratio){
		this.samplesInterval = (int)Math.ceil(1.0/ratio);
		return this;
	}
	
//	public SampleSelection<T> withMaxPoints(int maxPoints){
//		this.maxPoints = maxPoints;
//		return this;
//	}
	
	public SampleSelection<T> withSeed(int seed){
		this.seed = seed;
		return this;
	}
	
	public SampleSelection<T> withForceInclude(List<? extends T> forceInclude){
		this.forceInclude = forceInclude;
		return this;
	}
	
	protected boolean filterOut(T point, List<? extends T> forceInclude ){
		boolean remove = false;
		if (forceInclude!=null){			
			for(int i = 0; i<forceInclude.size(); i++){
				remove |= (point.getId()==forceInclude.get(i).getId());
			}	
		}
		return remove;
	}
	
}
