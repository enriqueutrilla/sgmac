package enriqueutrilla.sgmac.dm;

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
 * PathPoint.java
 * Datamodel class representing a mutable Point
 */

import java.util.Arrays;

public class PathPoint extends Point {

	private static final long serialVersionUID = 2580922433985487369L;
	protected boolean converged = false;

	public PathPoint(double[] initialCoordinates) {
		super(Arrays.copyOf(initialCoordinates, initialCoordinates.length));
	}

	public PathPoint(Point initialCoordinates) {
		this(initialCoordinates.getCoordinates());
	}

	public void setCoordinates(double[] newCoordinates) {
		this.coordinates = Arrays.copyOf(newCoordinates, newCoordinates.length);
	}

	public void add(double[] increments) {
		for (int i = 0; i < this.coordinates.length; i++) {
			this.coordinates[i] += increments[i];
		}
	}

	public boolean isConverged() {
		return converged;
	}

	public void setConverged(boolean converged) {
		this.converged = converged;
	}

}
