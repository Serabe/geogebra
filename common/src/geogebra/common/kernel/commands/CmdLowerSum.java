package geogebra.common.kernel.commands;

import geogebra.common.kernel.arithmetic.Command;
import geogebra.common.kernel.arithmetic.NumberValue;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoFunctionable;
import geogebra.common.main.MyError;
import geogebra.common.kernel.AbstractKernel;

/**
 * LowerSum[ <GeoFunction>, <Number>, <Number>, <Number> ]
 */
public class CmdLowerSum extends CommandProcessor {

	/**
	 * Create new command processor
	 * 
	 * @param kernel
	 *            kernel
	 */
	public CmdLowerSum(AbstractKernel kernel) {
		super(kernel);
	}

	final public GeoElement[] process(Command c) throws MyError {
		int n = c.getArgumentNumber();
		boolean[] ok = new boolean[n];
		GeoElement[] arg;

		switch (n) {
		case 4:
			arg = resArgs(c);
			if ((ok[0] = (arg[0].isGeoFunctionable()))
					&& (ok[1] = (arg[1].isNumberValue()))
					&& (ok[2] = (arg[2].isNumberValue()))
					&& (ok[3] = (arg[3].isNumberValue()))) {
				GeoElement[] ret = { kernelA.LowerSum(c.getLabel(),
						((GeoFunctionable) arg[0]).getGeoFunction(),
						(NumberValue) arg[1], (NumberValue) arg[2],
						(NumberValue) arg[3]) };
				return ret;
			} else
				throw argErr(app, c.getName(), null);

		default:
			throw argNumErr(app, c.getName(), n);
		}
	}
}