package geogebra.common.kernel.commands;

import geogebra.common.kernel.arithmetic.Command;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoLine;
import geogebra.common.main.MyError;
import geogebra.common.kernel.Kernel;

/**
 * Slope[ <GeoLine> ] Slope[ <GeoFunction> ]
 */
public class CmdSlope extends CommandProcessor {

	/**
	 * Create new command processor
	 * 
	 * @param kernel
	 *            kernel
	 */
	public CmdSlope(Kernel kernel) {
		super(kernel);
	}

	final public GeoElement[] process(Command c) throws MyError {
		int n = c.getArgumentNumber();
		GeoElement[] arg;

		switch (n) {
		case 1:
			arg = resArgs(c);
			if (arg[0].isGeoLine()) {
				GeoElement[] ret = { kernelA.Slope(c.getLabel(),
						(GeoLine) arg[0]) };
				return ret;
			} else
				throw argErr(app, c.getName(), arg[0]);

		default:
			throw argNumErr(app, c.getName(), n);
		}
	}
}
