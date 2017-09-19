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
 * Basic interface for implementations of the Modal Association Clustering algorithm
 */

import java.util.List;

import enriqueutrilla.sgmac.dm.Cluster;
import enriqueutrilla.sgmac.dm.DatasetPoint;

public interface Mac {

	public void cluster(List<DatasetPoint> points);
	public List<Cluster> getClusters();
	public String getMacSubtype();
	public String getParamDesc();
}
