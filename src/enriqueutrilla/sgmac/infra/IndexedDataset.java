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
 * IndexedDataset.java
 * List of DatasetPoints indexed to provide efficiently a list of the points
 * that are closer than a given limit to a central point. 
 * The filtering is an upper limit (all points inside the n-sphere of given
 * radius are returned, but some extra points further awaymay be returned too).
 * This implementation might also be replaced by an R-Tree or similar.
 */

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import enriqueutrilla.sgmac.dm.DatasetPoint;
import enriqueutrilla.sgmac.util.Console;

public class IndexedDataset<T extends DatasetPoint> {

	protected boolean indexed = false;
	protected Node<T> root = null;
	protected Configuration conf;
	protected List<T> data;
	protected List<T> lastQuery;

	public IndexedDataset(List<T> data, double[] sigmas, double cellSizeFactor) {

		this.data = data;
		this.lastQuery = new ArrayList<>(data.size());

		conf = new Configuration();
		conf.cellSizes = new double[sigmas.length];
		conf.offsets = new double[sigmas.length];
		conf.nNodes = new double[sigmas.length];
		for (int i = 0; i < sigmas.length; i++) {
			conf.cellSizes[i] = cellSizeFactor * sigmas[i];
			conf.offsets[i] = 0;
		}
		conf.lastCoordToIndex = -1;
		conf.cellSizeFactor = cellSizeFactor;
	}

	public IndexedDataset(List<T> data, double[] sigmas, double cellSizeFactor, int lastCoordToIndex) {

		this.data = data;
		this.lastQuery = new ArrayList<>(data.size() / 4);

		conf = new Configuration();
		conf.cellSizes = new double[sigmas.length];
		conf.offsets = new double[sigmas.length];
		for (int i = 0; i < sigmas.length; i++) {
			conf.cellSizes[i] = cellSizeFactor * sigmas[i];
			conf.offsets[i] = 0;
		}
		conf.nNodes = new double[sigmas.length];
		conf.lastCoordToIndex = lastCoordToIndex;
		conf.cellSizeFactor = cellSizeFactor;
	}

	public IndexedDataset(List<T> data, double[] sigmas, double cellSizeFactor, int lastCoordToIndex,
			double[] offsets) {

		this.data = data;
		this.lastQuery = new ArrayList<>(data.size() / 4);

		conf = new Configuration();
		conf.cellSizes = new double[sigmas.length];
		for (int i = 0; i < sigmas.length; i++) {
			conf.cellSizes[i] = cellSizeFactor * sigmas[i];
		}
		conf.offsets = offsets;
		conf.nNodes = new double[sigmas.length];
		conf.lastCoordToIndex = lastCoordToIndex;
		conf.cellSizeFactor = cellSizeFactor;
	}

	public List<T> getAll() {
		return data;
	}

	public void indexData() {

		root = new Node<T>(-1, conf);
		for (T point : data) {
			root.indexPoint(point);
		}
		indexed = true;

		Console.log("Number of nodes at each level: " + Arrays.toString(conf.nNodes));
	}

	public List<T> getNeighborhood(double[] coordinates, double minRadiusInSigmas) {

		if (minRadiusInSigmas < 0) {
			return getAll();
		} else {
			int[] indexes = conf.getIndexFor(coordinates);

			if (!indexed) {
				indexData();
			}

			int neighborhoodSize = 0;
			if (minRadiusInSigmas > 0) {
				neighborhoodSize = (int) Math.ceil(minRadiusInSigmas / conf.cellSizeFactor);
			}

			this.lastQuery.clear();

			root.getNeighborhood(indexes, neighborhoodSize, lastQuery);
			return lastQuery;
		}
	}

	protected class Configuration {
		int lastCoordToIndex = -1;
		double cellSizeFactor = 1;
		double[] offsets;
		double[] cellSizes;
		double[] nNodes;

		public int getIndexFor(double[] coordinates, int coordIndex) {
			double coord = coordinates[coordIndex];
			double offset = offsets[coordIndex];
			double width = cellSizes[coordIndex];
			int index = (int) Math.floor((coord - offset) / width);
			return index;
		}

		public int[] getIndexFor(double[] coordinates) {
			int[] indexes = new int[coordinates.length];
			for (int i = 0; i < indexes.length; i++) {
				indexes[i] = getIndexFor(coordinates, i);
			}
			return indexes;
		}
	}

	protected class Node<V extends DatasetPoint> {
		int coordIndex;
		Configuration conf;

		protected List<V> points = null;
		protected Map<Integer, Node<V>> children = null;

		public Node(int coordIndex, Configuration conf) {
			this.coordIndex = coordIndex;
			this.conf = conf;
			if (coordIndex >= 0) {
				conf.nNodes[coordIndex]++;
			}
		}

		protected boolean isLeaf() {
			return (this.coordIndex == (conf.cellSizes.length - 1)
					|| conf.lastCoordToIndex >= 0 && conf.lastCoordToIndex == this.coordIndex);
		}

		public void indexPoint(V point) {
			if (isLeaf()) {
				if (points == null) {
					points = new LinkedList<V>();
				}
				points.add(point);
			} else {
				if (children == null) {
					children = new HashMap<>();
				}
				int index = conf.getIndexFor(point.getCoordinates(), coordIndex + 1);
				Node<V> child = children.get(index);
				if (child == null) {
					child = new Node<V>(coordIndex + 1, conf);
					children.put(index, child);
				}
				child.indexPoint(point);
			}
		}

		public List<V> getCell(int[] indexes) {
			if (isLeaf()) {
				return points;
			} else {
				List<V> result = null;
				if (children != null) {
					Node<V> child = children.get(indexes[coordIndex + 1]);
					if (child != null) {
						result = child.getCell(indexes);
					}
				}
				return result;
			}
		}

		public void getNeighborhood(int[] indexes, int size, List<V> accumulator) {
			if (isLeaf()) {
				accumulator.addAll(points);
			} else {
				if (children != null) {
					int index = indexes[coordIndex + 1];
					for (int i = index - size; i <= index + size; i++) {

						Node<V> child = children.get(i);
						if (child != null) {
							child.getNeighborhood(indexes, size, accumulator);
						}
					}
				}
			}
		}
	}

}
