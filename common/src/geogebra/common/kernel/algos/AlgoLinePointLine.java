/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

*/

/*
 * AlgoLinePointLine.java
 *
 * line through P parallel to l
 *
 * Created on 30. August 2001, 21:37
 */

package geogebra.common.kernel.algos;

import geogebra.common.euclidian.EuclidianConstants;
import geogebra.common.kernel.Construction;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoLine;
import geogebra.common.kernel.geos.GeoPoint2;
import geogebra.common.kernel.geos.GeoVec3D;

/**
 *
 * @author  Markus
 * @version 
 */
public class AlgoLinePointLine extends AlgoElement {

    private GeoPoint2 P; // input
    private GeoLine l; // input
    private GeoLine g; // output       

    /** Creates new AlgoLinePointLine */
    public AlgoLinePointLine(Construction cons, String label, GeoPoint2 P, GeoLine l) {
        super(cons);
        this.P = P;
        this.l = l;
        g = new GeoLine(cons);
        g.setStartPoint(P);
        setInputOutput(); // for AlgoElement

        // compute line 
        compute();
        setIncidence();
        g.setLabel(label);
    }

    private void setIncidence() {
    	P.addIncidence(g);
	}

    
    @Override
	public Algos getClassName() {
        return Algos.AlgoLinePointLine;
    }

	@Override
	public int getRelatedModeID() {
		return EuclidianConstants.MODE_PARALLEL;
	}
    
    // for AlgoElement
    @Override
	protected void setInputOutput() {
        input = new GeoElement[2];
        input[0] = P;
        input[1] = l;

        super.setOutputLength(1);
        super.setOutput(0, g);
        setDependencies(); // done by AlgoElement
    }

    public GeoLine getLine() {
        return g;
    }
    
    GeoPoint2 getP() {
        return P;
    }
    
    GeoLine getl() {
        return l;
    }

    // calc the line g through P and parallel to l   
    @Override
	public final void compute() {
        // homogenous:
        GeoVec3D.cross(P, l.y, -l.x, 0.0, g);
    }

    @Override
	final public String toString() {
        // Michael Borcherds 2008-03-30
        // simplified to allow better Chinese translation
    	return app.getPlain("LineThroughAParallelToB",P.getLabel(),l.getLabel());

    }
}
