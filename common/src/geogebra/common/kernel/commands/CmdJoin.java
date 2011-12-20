package geogebra.common.kernel.commands;

import geogebra.common.kernel.arithmetic.Command;
import geogebra.common.kernel.geos.GeoClass;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoList;
import geogebra.common.main.MyError;
import geogebra.common.kernel.AbstractKernel;

/**
 *Join
 */
public class CmdJoin extends CommandProcessor {

	/**
	 * Create new command processor
	 * 
	 * @param kernel
	 *            kernel
	 */
	public CmdJoin(AbstractKernel kernel) {
		super(kernel);
	}

	public GeoElement[] process(Command c) throws MyError {
		int n = c.getArgumentNumber();
		boolean[] ok = new boolean[n];
		GeoElement[] arg;
		arg = resArgs(c);

		switch (n) {
		case 1:

			ok[0] = arg[0].isGeoList();

			if (ok[0]) {
				GeoElement[] ret = { kernelA
						.Join(c.getLabel(), (GeoList) arg[0]) };
				return ret;
			} else

			if (!ok[0])
				throw argErr(app, c.getName(), arg[0]);
			else
				throw argErr(app, c.getName(), arg[1]);

		default:
			// try to create list of numbers
			GeoList list = wrapInList(kernelA, arg, arg.length,
					GeoClass.LIST);
			if (list != null) {
				GeoElement[] ret = { kernelA.Join(c.getLabel(), list) };
				return ret;
			}
			throw argNumErr(app, c.getName(), n);
		}
	}
}