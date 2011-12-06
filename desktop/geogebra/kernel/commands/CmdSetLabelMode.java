package geogebra.kernel.commands;

import geogebra.common.main.MyError;
import geogebra.kernel.Kernel;
import geogebra.kernel.arithmetic.Command;
import geogebra.kernel.geos.GeoElement;
import geogebra.kernel.geos.GeoNumeric;

/**
 *SetLabelMode
 */
class CmdSetLabelMode extends CmdScripting {

	/**
	 * Create new command processor
	 * 
	 * @param kernel
	 *            kernel
	 */
	public CmdSetLabelMode(Kernel kernel) {
		super(kernel);
	}

	final public void perform(Command c) throws MyError {
		int n = c.getArgumentNumber();
		GeoElement[] arg;

		switch (n) {
		case 2:
			arg = resArgs(c);
			if (arg[1].isGeoNumeric()) {

				GeoElement geo = (GeoElement) arg[0];

				geo.setLabelMode((int) ((GeoNumeric) arg[1]).getDouble());
				geo.updateRepaint();

				
				return;
			} else
				throw argErr(app, c.getName(), arg[1]);

		default:
			throw argNumErr(app, c.getName(), n);
		}
	}
}