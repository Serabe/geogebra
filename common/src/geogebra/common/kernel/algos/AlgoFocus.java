/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

 */

/*
 * AlgoFoci.java
 *
 * Created on 11. November 2001, 21:37
 */

package geogebra.common.kernel.algos;

import geogebra.common.kernel.Construction;
import geogebra.common.kernel.geos.GeoConic;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoPoint2;
import geogebra.common.kernel.geos.GeoVec2D;
import geogebra.common.kernel.kernelND.GeoConicNDConstants;

/**
 * 
 * @author Markus
 * @version
 */
public class AlgoFocus extends AlgoElement {

	private GeoConic c; // input
	private GeoPoint2[] focus; // output

	transient private double temp1, temp2;
	GeoVec2D b;
	GeoVec2D[] eigenvec;

	AlgoFocus(Construction cons, String label, GeoConic c) {
		this(cons, c);
		GeoElement.setLabels(label, focus,kernel.getGeoElementSpreadsheet());
	}

	public AlgoFocus(Construction cons, String[] labels, GeoConic c) {
		this(cons, c);
		GeoElement.setLabels(labels, focus,kernel.getGeoElementSpreadsheet());
	}

	AlgoFocus(Construction cons, GeoConic c) {
		super(cons);
		this.c = c;
		focus = new GeoPoint2[2];
		for (int i = 0; i < focus.length; i++) {
			focus[i] = new GeoPoint2(cons);
			// only first undefined point should be shown in algebra window
			focus[i].showUndefinedInAlgebraView(i == 0);
		}

		setInputOutput(); // for AlgoElement

		b = c.b;
		eigenvec = c.eigenvec;

		compute();
	}

	@Override
	public Algos getClassName() {
		return Algos.AlgoFocus;
	}

	// for AlgoElement
	@Override
	protected void setInputOutput() {
		input = new GeoElement[1];
		input[0] = c;

		super.setOutput(focus);
		setDependencies(); // done by AlgoElement
	}

	GeoConic getConic() {
		return c;
	}

	public GeoPoint2[] getFocus() {
		return focus;
	}

	@Override
	public final void compute() {
		switch (c.type) {
		case GeoConicNDConstants.CONIC_CIRCLE:
			focus[0].setCoords(b.x, b.y, 1.0);
			focus[1].setCoords(b.x, b.y, 1.0);
			break;

		case GeoConicNDConstants.CONIC_ELLIPSE:
		case GeoConicNDConstants.CONIC_HYPERBOLA:
			temp1 = c.linearEccentricity * eigenvec[0].x;
			temp2 = c.linearEccentricity * eigenvec[0].y;
			focus[0].setCoords(b.x - temp1, b.y - temp2, 1.0d);
			focus[1].setCoords(b.x + temp1, b.y + temp2, 1.0d);
			break;

		case GeoConicNDConstants.CONIC_PARABOLA:
			temp1 = c.p / 2;
			focus[0].setCoords(b.x + temp1 * eigenvec[0].x, b.y + temp1
					* eigenvec[0].y, 1.0);
			// second focus undefined
			focus[1].setUndefined();
			break;

		default:
			// both focus undefined
			focus[0].setUndefined();
			focus[1].setUndefined();
		}
	}

	@Override
	public final String toString() {
		// Michael Borcherds 2008-03-30
		// simplified to allow better Chinese translation
		return app.getPlain("FocusOfA", c.getLabel());

	}
}
