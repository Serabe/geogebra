package geogebra.common.kernel.commands;

import geogebra.common.kernel.AbstractKernel;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoList;

public class CmdSYY extends CmdOneListFunction {

	public CmdSYY(AbstractKernel kernel) {
		super(kernel);
	}

	final protected GeoElement doCommand(String a, GeoList b)
	{
		return kernelA.SYY(a, b);
	}
}