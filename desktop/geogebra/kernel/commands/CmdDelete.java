package geogebra.kernel.commands;

import geogebra.common.main.MyError;
import geogebra.kernel.Kernel;
import geogebra.kernel.arithmetic.Command;
import geogebra.kernel.geos.GeoElement;

/**
 * Delete[ <GeoElement> ]
 */
class CmdDelete extends CmdScripting {

	/**
	 * Create new command processor
	 * 
	 * @param kernel
	 *            kernel
	 */
	public CmdDelete(Kernel kernel) {
		super(kernel);
	}

	final public void perform(Command c) throws MyError {
		int n = c.getArgumentNumber();
		boolean[] ok = new boolean[n];
		GeoElement[] arg;

		switch (n) {
		case 1:
			arg = resArgs(c);
			if (ok[0] = (arg[0].isGeoElement())) {
				GeoElement geo = (GeoElement) arg[0];
				
				// delete object
				geo.removeOrSetUndefinedIfHasFixedDescendent();
				return;
			} else
				throw argErr(app, "Delete", arg[0]);

		default:
			throw argNumErr(app, "Delete", n);
		}
	}
}