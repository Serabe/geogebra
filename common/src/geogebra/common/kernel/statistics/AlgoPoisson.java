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
import geogebra.common.kernel.algos.Algos;
import geogebra.common.kernel.arithmetic.NumberValue;
import geogebra.common.kernel.geos.GeoBoolean;
import geogebra.common.main.AbstractApplication;

import org.apache.commons.math.distribution.PoissonDistribution;



/**
 * 
 * @author G. Sturr
 */

public class AlgoPoisson extends AlgoDistribution {
	
	private static final long serialVersionUID = 1L;

	
	public AlgoPoisson(Construction cons, String label, NumberValue a,NumberValue b, GeoBoolean isCumulative) {
		super(cons, label, a, b, null, isCumulative); 
	}

	public AlgoPoisson(Construction cons, NumberValue a,NumberValue b, GeoBoolean isCumulative) {
		super(cons, a, b, null, isCumulative); 
	}

	public Algos getClassName() {
		return Algos.AlgoPoisson;
	}

	@SuppressWarnings("deprecation")
	public final void compute() {


		if (input[0].isDefined() && input[1].isDefined() && input[2].isDefined()) {
			double param = a.getDouble();
			double val = b.getDouble();	
			try {
				PoissonDistribution dist = getPoissonDistribution(param);
				if(isCumulative.getBoolean())
					num.setValue(dist.cumulativeProbability(val));  // P(X <= val)
				else
					num.setValue(dist.probability(val));   // P(X = val)
			}
			catch (Exception e) {
				AbstractApplication.debug(e.getMessage());
				num.setUndefined();        			
			}
		} else
			num.setUndefined();
	}       


}



