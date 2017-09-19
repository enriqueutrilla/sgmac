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
 * SampledPdfPoint.java
 * Datamodel class representing the sampled value of the PDF and its 
 * gradient at a given point.
 */

public class SampledPdfPoint extends DatasetPoint{
	
	private static final long serialVersionUID = 4541443401188152348L;
	protected int nContributions = 0;
	protected double pdfValue = 0;
	protected double[] pdfGradient;
	protected boolean localMaximum = false;
	

	public SampledPdfPoint(long id, double[] coordinates) {
		super(id, coordinates);
		this.pdfValue = 0;
		this.pdfGradient = new double[coordinates.length];
		this.nContributions = 0;
	}
	
	public SampledPdfPoint(DatasetPoint point) {
		this(point.getId(), point.getCoordinates());
	}
	
	public SampledPdfPoint(DatasetPoint point, double pdfValue, double[] pdfGradient) {
		this(point.getId(), point.getCoordinates(), pdfValue, pdfGradient);
	}
	
	public SampledPdfPoint(long id, double[] coordinates, double pdfValue, double[] pdfGradient) {
		this(id, coordinates);
		this.pdfValue = pdfValue;
		this.pdfGradient = pdfGradient;
		this.nContributions = 1;
	}
	
	public void setGradient(double[] pdfGradient) {
		this.pdfGradient = pdfGradient;
	}
	
	public void addContribution(double pdfValue, double[] pdfGradient) {
		this.pdfValue += pdfValue;
		for (int i=0; i<this.pdfGradient.length; i++) {
			this.pdfGradient[i] += pdfGradient[i];
		}
		this.nContributions++;
	}

	public int getNContributions() {
		return nContributions;
	}

	public double getPdfValue() {
		return pdfValue;
	}

	public double[] getPdfGradient() {
		return pdfGradient;
	}

	public boolean isLocalMaximum() {
		return localMaximum;
	}

	public void setLocalMaximum(boolean localMaximum) {
		this.localMaximum = localMaximum;
	}

}
