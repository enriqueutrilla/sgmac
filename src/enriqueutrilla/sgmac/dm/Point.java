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
 * Point.java
 * Datamodel class representing an inmutable n-dimensional point, 
 * with double values as coordinates.
 */

import java.io.Serializable;
import java.util.Arrays;

public class Point implements Serializable {

	private static final long serialVersionUID = -8249574716161393884L;
	protected double[] coordinates;

	public Point(double[] coordinates) {
		super();
		this.coordinates = coordinates;
	}

	public double[] getCoordinates() {
		return coordinates;
	}

	public int dim() {
		return coordinates.length;
	}

	public double get(int i) {
		return coordinates[i];
	}

	@Override
	public String toString() {
		return Arrays.toString(coordinates);
	}

}
