package geogebra.common.kernel.commands;

import geogebra.common.kernel.arithmetic.Command;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.main.MyError;
import geogebra.common.kernel.Kernel;

/*
 * Column[ <GeoElement> ]
 */
public class CmdColumn extends CommandProcessor {

	public CmdColumn(Kernel kernel) {
		super(kernel);
	}

	public GeoElement[] process(Command c) throws MyError {
		int n = c.getArgumentNumber();
		GeoElement[] arg;

		switch (n) {
		case 1:
			arg = resArgs(c);			
			if (arg[0].getLabel() != null && kernelA.getGeoElementSpreadsheet().isSpreadsheetLabel(arg[0].getLabel())) {

				GeoElement[] ret = { kernelA.Column(c.getLabel(),
						arg[0]) };
				return ret;
			}
			else
			{
				throw argErr(app, c.getName(), arg[0]);
			}



		default:
			throw argNumErr(app, c.getName(), n);
		}
	}

}
