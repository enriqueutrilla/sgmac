package enriqueutrilla.sgmac.dm;

/**
 * Copyright 2017 Enrique Utrilla Molina
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 * 
 * DatasetPoint.java
 * Datamodel class representing a Point that has also a long id
 */

public class DatasetPoint extends Point {

	private static final long serialVersionUID = -3316928683651998592L;
	long id;

	public DatasetPoint(long id, double[] coordinates) {
		super(coordinates);
		this.id = id;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	@Override
	public String toString() {
		return "ID " + id + ": " + super.toString();
	}
}
