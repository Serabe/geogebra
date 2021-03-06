package geogebra.common.kernel.commands;

import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoList;
import geogebra.common.kernel.Kernel;

/*
 * ReducedRowEchelonForm[ <List> ]
 * Michael Borcherds 
 */
public class CmdReducedRowEchelonForm extends CmdOneListFunction {

	public CmdReducedRowEchelonForm(Kernel kernel) {
		super(kernel);
	}

	final protected GeoElement doCommand(String a, GeoList b)
	{
		return kernelA.ReducedRowEchelonForm(a, b);
	}

}
