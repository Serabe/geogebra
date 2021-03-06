package geogebra.common.kernel.commands;

import geogebra.common.kernel.Kernel;
import geogebra.common.kernel.arithmetic.NumberValue;
import geogebra.common.kernel.geos.GeoElement;

/*
 * Random[ <Number>, <Number> ]
 */
public class CmdRandom extends CmdTwoNumFunction {

	public CmdRandom(Kernel kernel) {
		super(kernel);
	}

	protected GeoElement doCommand(String a, NumberValue b, NumberValue c)
	{
		return kernelA.Random(a, b, c);
	}

}
