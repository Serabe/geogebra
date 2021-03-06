package geogebra.common.kernel.algos;

import geogebra.common.kernel.Construction;
import geogebra.common.kernel.arithmetic.Function;
import geogebra.common.kernel.geos.GeoInterval;

public class AlgoDependentInterval extends AlgoDependentFunction {


	AlgoDependentInterval(Construction cons, Function fun) {
        super(cons);
        this.fun = fun;
        f = new GeoInterval(cons);
        f.setFunction(fun);
        
  
        setInputOutput(); // for AlgoElement
        
        compute();
    }

	public AlgoDependentInterval(Construction cons, String label,
			Function fun) {
        this(cons, fun);
            
       	f.setLabel(label);
	}

	@Override
	public Algos getClassName() {
        return Algos.AlgoDependentInterval;
    }
    
    @Override
	final public String toString() {
    	return f.toSymbolicString();
    }

}
