/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

 */

// adapted from AlgoTextCorner by Michael Borcherds 2008-05-10

package geogebra.common.kernel.algos;

import geogebra.common.euclidian.EuclidianViewInterfaceSlim;
import geogebra.common.kernel.Construction;
import geogebra.common.kernel.arithmetic.NumberValue;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoPoint2;
import geogebra.common.main.AbstractApplication;

public class AlgoDrawingPadCorner extends AlgoElement {

	private GeoPoint2 corner; // output
	private NumberValue number, evNum;

	public AlgoDrawingPadCorner(Construction cons, String label, NumberValue number,
			NumberValue evNum) {
		super(cons);
		this.number = number;
		this.evNum = evNum; // can be null

		corner = new GeoPoint2(cons);
		setInputOutput(); // for AlgoElement
		compute();
		corner.setEuclidianVisible(false); // hidden by default
		corner.setLabel(label);

		cons.registerEuclidianViewCE(this);

	}

	@Override
	public Algos getClassName() {
		return Algos.AlgoDrawingPadCorner;
	}

	// for AlgoElement
	@Override
	protected void setInputOutput() {
		if (evNum == null) {
			input = new GeoElement[1];
			input[0] = number.toGeoElement();
		} else {
			input = new GeoElement[2];
			input[0] = evNum.toGeoElement();
			input[1] = number.toGeoElement();

		}

		super.setOutputLength(1);
		super.setOutput(0, corner);
		setDependencies(); // done by AlgoElement
	}

	public GeoPoint2 getCorner() {
		return corner;
	}

	@Override
	public final void compute() {

		// x1 = x1 / invXscale + xZero;
		// x2 = x2 / invXscale + xZero;

		EuclidianViewInterfaceSlim ev;

		AbstractApplication app = cons.getApplication();

		if (evNum == null || evNum.getDouble() == 1.0)
			ev = app.getEuclidianView();
		else {
			if (!app.hasEuclidianView2()) {
				corner.setUndefined();
				return;
			} 
			ev = app.getEuclidianView2();
		}

		double width = ev.toRealWorldCoordX((double) (ev.getWidth()) + 1);
		double height = ev.toRealWorldCoordY((double) (ev.getHeight()) + 1);
		double zeroX = ev.toRealWorldCoordX(-1);
		double zeroY = ev.toRealWorldCoordY(0 - 1);

		switch ((int) number.getDouble()) {
		case 1:
			corner.setCoords(zeroX, height, 1.0);
			break;
		case 2:
			corner.setCoords(width, height, 1.0);
			break;
		case 3:
			corner.setCoords(width, zeroY, 1.0);
			break;
		case 4:
			corner.setCoords(zeroX, zeroY, 1.0);
			break;
		case 5: // return size of Graphics View in pixels
			corner.setCoords(ev.getWidth(), ev.getHeight(), 1.0);
			break;
		case 6: // return size of Window in pixels
			// (to help with sizing for export to applet)
			// doesn't work very well as it receives updates only when
			// EuclidianView is changed
			corner.setCoords(app.getWidth(), app.getHeight(), 1.0);
			
			break;
		default:
			corner.setUndefined();
			break;
		}
	}

	final public static boolean wantsEuclidianViewUpdate() {
		return true;
	}

	@Override
	public final boolean euclidianViewUpdate() {
		compute();

		// update output:
		corner.updateCascade();
		return false;
	}

	@Override
	final public String toString() {
		return getCommandDescription();
	}

}
