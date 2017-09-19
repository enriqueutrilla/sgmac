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
 * PathUpdater.java
 * Base abstract class that traverses a path from an initial point based
 * on incremental updates.
 */	
	

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import enriqueutrilla.sgmac.dm.PathPoint;
import enriqueutrilla.sgmac.dm.Point;
import enriqueutrilla.sgmac.util.Console;

public abstract class PathUpdater {
	
	private List<Integer> iterationsToConverge = new ArrayList<>();
	private int maxIterations = Integer.MAX_VALUE;
	
	public void setMaxIterations(int maxIterations){
		this.maxIterations = maxIterations;
	}
	
	public int getMaxIterations(){
		return this.maxIterations;
	}

	public abstract void update(PathPoint current);

	public PathPoint findMode(Point initial){
		return findMode(initial, false);
	}
	
	public PathPoint findMode(Point initial, boolean trace) {
		
		int nIterations = 0;
		PathPoint current = new PathPoint(initial);
		while (!current.isConverged() && nIterations<maxIterations) {
			update(current);
			if (trace){
				Console.log(nIterations + ": " + Arrays.toString(current.getCoordinates()));
			}
			nIterations ++;
		}
		if (current.isConverged()){
			iterationsToConverge.add(nIterations);
		}
		return current;
	}
	
	public String getConvergenceStats(){
		
		int total = iterationsToConverge.size();
		int converged = 0;
		long accumMean = 0;
		int minIteration = Integer.MAX_VALUE;
		int maxIteration = 0;
		
		if (total>0){
			for(Integer iteration: iterationsToConverge){
				if (iteration < maxIterations){
					converged++;
					accumMean += iteration;
					
					if (iteration > 0 && iteration < minIteration){
						minIteration = iteration;
					}
					
					if (iteration > maxIteration){
						maxIteration = iteration;
					}
					
				} 
			}
			
			return "Total points: " + total 
				   + ", converged " + converged 
				   + " (" + (int)(100*converged/total) + "%)\n"
                   + "Iterations to converge: "
                   + minIteration + " min, " 
                   + (int)(accumMean/converged) + " average, " 
                   + maxIteration + " max";
		} else {
			return null;
		}
	}
}
