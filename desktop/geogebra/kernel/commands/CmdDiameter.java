package geogebra.kernel.commands;

import geogebra.kernel.Kernel;
import geogebra.kernel.arithmetic.Command;
import geogebra.kernel.geos.GeoConic;
import geogebra.kernel.geos.GeoElement;
import geogebra.kernel.geos.GeoLine;
import geogebra.kernel.geos.GeoVector;
import geogebra.main.MyError;

/**
 * Diameter[ <GeoVector>, <GeoConic> ] Diameter[ <GeoLine>, <GeoConic> ]
 */
class CmdDiameter extends CommandProcessor {

	/**
	 * Create new command processor
	 * 
	 * @param kernel
	 *            kernel
	 */
	public CmdDiameter(Kernel kernel) {
		super(kernel);
	}

	final public GeoElement[] process(Command c) throws MyError {
		int n = c.getArgumentNumber();
		boolean[] ok = new boolean[n];
		GeoElement[] arg;

		switch (n) {
		case 2:
			arg = resArgs(c);

			// diameter line conjugate to vector relative to conic
			if ((ok[0] = (arg[0].isGeoVector()))
					&& (ok[1] = (arg[1].isGeoConic()))) {
				GeoElement[] ret = { kernel.DiameterLine(c.getLabel(),
						(GeoVector) arg[0], (GeoConic) arg[1]) };
				return ret;
			}

			// diameter line conjugate to line relative to conic
			if ((ok[0] = (arg[0].isGeoLine()))
					&& (ok[1] = (arg[1].isGeoConic()))) {
				GeoElement[] ret = { kernel.DiameterLine(c.getLabel(),
						(GeoLine) arg[0], (GeoConic) arg[1]) };
				return ret;
			}

			// syntax error
			else {
				if (!ok[0])
					throw argErr(app, "Diameter", arg[0]);
				else
					throw argErr(app, "Diameter", arg[1]);
			}

		default:
			throw argNumErr(app, "Diameter", n);
		}
	}
}