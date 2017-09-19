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
 * Cluster.java
 * Datamodel class representing a cluster, including its mode and list of members
 */

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Cluster implements Serializable {

	private static final long serialVersionUID = -2396093774263688443L;
	protected Point mode;
	protected List<Point> members = new ArrayList<>();

	public Cluster(Point mode) {
		this.mode = mode;
	}

	public void add(Point member) {
		this.members.add(member);
	}

	public Point getMode() {
		return mode;
	}

	public List<Point> getMembers() {
		return members;
	}

}
