/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

*/

/*
 * AlgoCirclePointRadius.java
 *
 * Created on 15. November 2001, 21:37
 */

package geogebra3D.kernel3D;

import geogebra.common.kernel.Construction;
import geogebra.common.kernel.algos.AlgoSphereNDPointRadius;
import geogebra.common.kernel.algos.Algos;
import geogebra.common.kernel.arithmetic.NumberValue;
import geogebra.common.kernel.kernelND.GeoPointND;
import geogebra.common.kernel.kernelND.GeoQuadricND;

/**
 *
 * @author  Markus
 * added TYPE_SEGMENT Michael Borcherds 2008-03-14	
 * @version 
 */
public class AlgoSpherePointRadius extends AlgoSphereNDPointRadius {


	public AlgoSpherePointRadius(
            Construction cons,
            String label,
            GeoPointND M,
            NumberValue r) {
        	
            super(cons, label, M, r);
        }
        
    AlgoSpherePointRadius(
            Construction cons,
            String label,
            GeoPointND M,
            GeoSegment3D segment, boolean dummy) {
        	
            super(cons, label, M, segment, dummy);
        }

        
    public AlgoSpherePointRadius(
            Construction cons,
            GeoPoint3D M,
            NumberValue r) {
        	
    	super(cons, M, r);
                
        }
    
    
    AlgoSpherePointRadius(
            Construction cons,
            GeoPoint3D M,
            GeoSegment3D rgeo, boolean dummy) {
        	
            super(cons,M,rgeo,dummy);
        }
    
    protected GeoQuadricND createSphereND(Construction cons){
    	return new GeoQuadric3D(cons);
    }
    

    public Algos getClassName() {
        return Algos.AlgoSpherePointRadius;
    }

    public GeoQuadric3D getSphere() {
        return (GeoQuadric3D) getSphereND();
    }
 



    final public String toString() {
        return app.getPlain("SphereWithCenterAandRadiusB",getM().getLabel(),getRGeo().getLabel());
    }
}
