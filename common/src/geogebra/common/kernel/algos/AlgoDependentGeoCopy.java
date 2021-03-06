/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

*/

package geogebra.common.kernel.algos;

import geogebra.common.kernel.Construction;
import geogebra.common.kernel.arithmetic.ExpressionNode;
import geogebra.common.kernel.geos.GeoElement;


/**
 * Creates a dependent copy of the given GeoElement.
 */
public class AlgoDependentGeoCopy extends AlgoElement {

	private ExpressionNode origGeoNode;
    private GeoElement origGeo, copyGeo;     // input, ouput              
    
    public AlgoDependentGeoCopy(Construction cons, String label, GeoElement origGeoNode) {
    	super(cons);
    	origGeo = origGeoNode;
    	
    	// just for the toString() method
    	this.origGeoNode = new ExpressionNode(kernel, origGeo.evaluate());
    	
        copyGeo = origGeo.copy();
        setInputOutput(); // for AlgoElement
        
        compute();      
        copyGeo.setLabel(label);
    }
    
    public AlgoDependentGeoCopy(Construction cons, String label, ExpressionNode origGeoNode) {
    	super(cons);
    	this.origGeoNode = origGeoNode;
        origGeo = (GeoElement) origGeoNode.evaluate();
        
        copyGeo = origGeo.copy();
        setInputOutput(); // for AlgoElement
        
        compute();      
        copyGeo.setLabel(label);
    }   
    
	@Override
	public Algos getClassName() {
		return Algos.AlgoDependentGeoCopy;
	}
    
    // for AlgoElement
	@Override
	protected void setInputOutput() {
        input = new GeoElement[1];
        input[0] = origGeo;
        
        setOutputLength(1);        
        setOutput(0,copyGeo);        
        setDependencies(); // done by AlgoElement
    }    
    
    public GeoElement getGeo() { return copyGeo; }
    
    // copy geo
    @Override
	public final void compute() {	
    	try {
    		copyGeo.set(origGeo);
    	} catch (Exception e) {
    		copyGeo.setUndefined();
    	}
    }   
    
    @Override
	final public String toString() {
    	// we use the expression as it may add $ signs 
    	// to the label like $A$1
    	return origGeoNode.toString();
    }
}
