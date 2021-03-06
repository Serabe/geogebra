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

import org.apache.commons.math.distribution.BinomialDistribution;

/**
 * 
 * @author Michael Borcherds
 */

public class AlgoInverseBinomial extends AlgoDistribution {

	private static final long serialVersionUID = 1L;
    
    public AlgoInverseBinomial(Construction cons, String label, NumberValue a,NumberValue b, NumberValue c) {
        super(cons, label, a, b, c, null);
    }

    public Algos getClassName() {
        return Algos.AlgoInverseBinomial;
    }
    
	public final void compute() {
    	
    	
    	if (input[0].isDefined() && input[1].isDefined() && input[2].isDefined()) {
		    int param = (int)Math.round(a.getDouble());
		    double param2 = b.getDouble();
    		    double val = c.getDouble();
        		try {
        			BinomialDistribution dist = getBinomialDistribution(param, param2);
        			
        			double result = dist.inverseCumulativeProbability(val);
        			
        			// eg InversePascal[1,1,1] returns  2147483647 
        			if (result > param )
        				num.setValue(param);
        			else
        				num.setValue(result + 1);    
        			
        		}
        		catch (Exception e) {
        			num.setUndefined();        			
        		}
    	} else
    		num.setUndefined();
    }       
        
    
}



