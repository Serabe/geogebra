package geogebra.common.kernel.commands;

import geogebra.common.kernel.Kernel;
import geogebra.common.kernel.arithmetic.NumberValue;
import geogebra.common.kernel.geos.GeoElement;

/*
 * RandomUniform[ <Number>, <Number> ]
 */
public class CmdRandomUniform extends CmdTwoNumFunction {

	public CmdRandomUniform(Kernel kernel) {
		super(kernel);
	}

	protected GeoElement doCommand(String a, NumberValue b, NumberValue c)
	{
		return kernelA.RandomUniform(a, b, c);
	}

}
