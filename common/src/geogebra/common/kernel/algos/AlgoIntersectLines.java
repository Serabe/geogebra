/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

*/

/*
 * AlgoIntersectLines.java
 *
 * Created on 30. August 2001, 21:37
 */

package geogebra.common.kernel.algos;

import geogebra.common.euclidian.EuclidianConstants;
import geogebra.common.kernel.Kernel;
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
public class AlgoIntersectLines extends AlgoIntersectAbstract {

    private GeoLine g, h; // input
    private GeoPoint2 S; // output       

    /** Creates new AlgoJoinPoints */
    public AlgoIntersectLines(Construction cons, String label, GeoLine g, GeoLine h) {
        super(cons);
        this.g = g;
        this.h = h;
        S = new GeoPoint2(cons);
        setInputOutput(); // for AlgoElement

        // compute line through P, Q
        compute();
        setIncidence();
        
        S.setLabel(label);
    }

    private void setIncidence() {
		S.addIncidence(g);
		S.addIncidence(h);
	}

	@Override
	public Algos getClassName() {
        return Algos.AlgoIntersectLines;
    }

    @Override
	public int getRelatedModeID() {
    	return EuclidianConstants.MODE_INTERSECT;
    }
    
    // for AlgoElement
    @Override
	protected void setInputOutput() {
        input = new GeoElement[2];
        input[0] = g;
        input[1] = h;

        super.setOutputLength(1);
        super.setOutput(0, S);
        setDependencies(); // done by AlgoElement
    }

    public GeoPoint2 getPoint() {
        return S;
    }
    
    GeoLine geth() {
        return g;
    }
    
    GeoLine getg() {
        return h;
    }

    // calc intersection S of lines g, h
    @Override
	public final void compute() {   	
        GeoVec3D.cross(g, h, S); 
              
        // test the intersection point
        // this is needed for the intersection of segments
        if (S.isDefined()) {
        	if (!(g.isIntersectionPointIncident(S, Kernel.MIN_PRECISION) &&
			      h.isIntersectionPointIncident(S, Kernel.MIN_PRECISION)) )
				S.setUndefined();
        }
    }

    @Override
	final public String toString() {
        // Michael Borcherds 2008-03-30
        // simplified to allow better Chinese translation
        return app.getPlain("IntersectionPointOfAB",g.getLabel(),h.getLabel());

    }
}
