package geogebra.common.kernel.commands;

import geogebra.common.kernel.Kernel;
import geogebra.common.kernel.arithmetic.Command;
import geogebra.common.kernel.geos.GeoConic;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoPoint2;
import geogebra.common.kernel.geos.GeoPolygon;
import geogebra.common.main.MyError;

/**
 * Area[ <GeoPoint>, ..., <GeoPoint> ] Area[ <GeoConic> ] Area[ <Polygon> ]
 * (returns Polygon directly)
 */
public class CmdArea extends CommandProcessor {

	/**
	 * Create new command processor
	 * 
	 * @param kernel
	 *            kernel
	 */
	public CmdArea(Kernel kernel) {
		super(kernel);
	}

	@Override
	final public GeoElement[] process(Command c) throws MyError {
		int n = c.getArgumentNumber();
		GeoElement[] arg;

		if (n == 1) {
			arg = resArgs(c);

			// area of conic
			if (arg[0].isGeoConic()) {
				GeoElement[] ret = { kernelA.Area(c.getLabel(),
						(GeoConic) arg[0]) };
				return ret;
			}
			// area of polygon = polygon variable
			else if (arg[0].isGeoPolygon()) {
				GeoElement[] ret = { kernelA.Area(c.getLabel(),
						(GeoPolygon) arg[0]) };
				return ret;
			} else {
				throw argErr(app, c.getName(), arg[0]);
			}
		}

		// area of points
		else if (n > 2) {
			arg = resArgs(c);
			GeoPoint2[] points = new GeoPoint2[n];
			// check arguments
			for (int i = 0; i < n; i++) {
				if (!(arg[i].isGeoPoint())) {
					throw argErr(app, c.getName(), arg[i]);
				} else {
					points[i] = (GeoPoint2) arg[i];
				}
			}
			// everything ok
			GeoElement[] ret = { kernelA.Area(c.getLabel(), points) };
			return ret;
		} else {
			throw argNumErr(app, c.getName(), n);
		}
	}
}
