/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

 */

package geogebra.common.kernel.statistics;

import geogebra.common.kernel.Construction;
import geogebra.common.kernel.algos.AlgoTwoNumFunction;
import geogebra.common.kernel.algos.Algos;
import geogebra.common.kernel.arithmetic.NumberValue;

/**
 * Computes RandomNormal[a, b]
 * 
 * @author Michael Borcherds
 * @version
 */
public class AlgoRandomUniform extends AlgoTwoNumFunction implements SetRandomValue {

	public AlgoRandomUniform(Construction cons, String label, NumberValue a,
			NumberValue b) {
		super(cons, label, a, b);

		// output is random number
		cons.addRandomGeo(num);
}

	public Algos getClassName() {
		return Algos.AlgoRandomUniform;
	}

	public final void compute() {
		if (input[0].isDefined() && input[1].isDefined()) {
			num.setValue(a.getDouble() + Math.random() *( b.getDouble() - a.getDouble()));
		} else
			num.setUndefined();
		
	}
	public void setRandomValue(double d) {
		
		if (d >= a.getDouble() && d <= b.getDouble()) {
			num.setValue(d);
			num.updateRepaint();
		}
			
	}

}
