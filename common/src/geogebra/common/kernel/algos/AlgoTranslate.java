/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

 */

/*
 * AlgoTranslatePoint.java
 *
 * Created on 24. September 2001, 21:37
 */

package geogebra.common.kernel.algos;

import geogebra.common.euclidian.EuclidianConstants;
import geogebra.common.kernel.Construction;
import geogebra.common.kernel.Matrix.Coords;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoList;
import geogebra.common.kernel.geos.GeoVec3D;
import geogebra.common.kernel.geos.Translateable;

/**
 * 
 * @author Markus
 * @version
 */
public class AlgoTranslate extends AlgoTransformation {

	private Translateable out;
	private GeoElement inGeo, outGeo;
	protected GeoElement v; // input

	/**
	 * Creates labeled translation algo
	 * 
	 * @param cons
	 * @param label
	 * @param in
	 * @param v
	 */
	public AlgoTranslate(Construction cons, String label, GeoElement in,
			GeoVec3D v) {
		this(cons, in, v);
		outGeo.setLabel(label);
	}

	/**
	 * Creates unlabeled translation algo
	 * 
	 * @param cons
	 * @param in
	 * @param v
	 */
	public AlgoTranslate(Construction cons, GeoElement in, GeoElement v) {
		super(cons);
		this.v = v;

		inGeo = in;

		// create out
		if (inGeo.isGeoPolyLine() || inGeo.isGeoPolygon()
				|| inGeo.isLimitedPath()) {

			outGeo = copyInternal(cons, inGeo);
			out = (Translateable) outGeo;
		} else if (in.isGeoList()) {
			outGeo = new GeoList(cons);
		} else {
			outGeo = copy(inGeo);
			out = (Translateable) outGeo;
		}

		setInputOutput();
		compute();
	}

	@Override
	public Algos getClassName() {
		return Algos.AlgoTranslate;
	}

	@Override
	public int getRelatedModeID() {
		return EuclidianConstants.MODE_TRANSLATE_BY_VECTOR;
	}

	// for AlgoElement
	@Override
	protected void setInputOutput() {
		input = new GeoElement[2];
		input[0] = inGeo;
		input[1] = v;

		setOutputLength(1);
		setOutput(0, outGeo);
		setDependencies(); // done by AlgoElement
	}

	@Override
	public GeoElement getResult() {
		return outGeo;
	}

	// calc translated point
	@Override
	public final void compute() {
		if (inGeo.isGeoList()) {
			transformList((GeoList) inGeo, (GeoList) outGeo);
			return;
		}
		outGeo.set(inGeo);
		out.translate(getVectorCoords());
		if (inGeo.isLimitedPath()) {
			this.transformLimitedPath(inGeo, outGeo);
		}
	}

	protected Coords getVectorCoords() {
		GeoVec3D vec = (GeoVec3D) v;
		return new Coords(vec.x, vec.y, vec.z);
	}

	@Override
	final public String toString() {

		// Michael Borcherds 2008-03-24 simplified code!
		return app
				.getPlain("TranslationOfAbyB", inGeo.getLabel(), v.getLabel());
	}

	@Override
	protected void setTransformedObject(GeoElement g, GeoElement g2) {
		inGeo = g;
		outGeo = g2;
		if (!(outGeo instanceof GeoList)) {
			out = (Translateable) outGeo;
		}
	}

}
