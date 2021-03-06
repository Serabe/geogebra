package geogebra.common.kernel.commands;


import geogebra.common.kernel.arithmetic.Command;
import geogebra.common.kernel.geos.GeoConic;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoInterval;
import geogebra.common.kernel.geos.GeoPoint2;
import geogebra.common.kernel.geos.GeoSegment;
import geogebra.common.main.MyError;
import geogebra.common.kernel.Kernel;


/**
 * Midpoint[ <GeoConic> ] Midpoint[ <GeoPoint>, <GeoPoint> ]
 */
public class CmdMidpoint extends CommandProcessor {
	
	public CmdMidpoint(Kernel kernel) {
		super(kernel);
	}

	public GeoElement[] process(Command c) throws MyError {
		int n = c.getArgumentNumber();
		boolean[] ok = new boolean[n];
		GeoElement[] arg;

		switch (n) {
		case 1:
			arg = resArgs(c);
			if (ok[0] = (arg[0].isGeoConic())) {
				GeoElement[] ret = { kernelA.Center(c.getLabel(),
						(GeoConic) arg[0]) };
				return ret;
			} else if (arg[0].isGeoSegment()) {
				GeoElement[] ret = { kernelA.Midpoint(c.getLabel(),
						(GeoSegment) arg[0]) };
				return ret;
			} else if (arg[0].isGeoInterval()) {
				GeoElement[] ret = { kernelA.Midpoint(c.getLabel(),
						(GeoInterval) arg[0]) };
				return ret;
			} else
				throw argErr(app, c.getName(), arg[0]);

		case 2:
			arg = resArgs(c);
			if ((ok[0] = (arg[0].isGeoPoint()))
					&& (ok[1] = (arg[1].isGeoPoint()))) {
				GeoElement[] ret = { kernelA.Midpoint(c.getLabel(),
						(GeoPoint2) arg[0], (GeoPoint2) arg[1]) };
				return ret;
			} else {
				if (!ok[0])
					throw argErr(app, c.getName(), arg[0]);
				else
					throw argErr(app, c.getName(), arg[1]);
			}

		default:
			throw argNumErr(app, c.getName(), n);
		}
	}
}