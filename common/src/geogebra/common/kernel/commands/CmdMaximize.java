package geogebra.common.kernel.commands;

import geogebra.common.kernel.arithmetic.Command;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoNumeric;
import geogebra.common.main.MyError;
import geogebra.common.kernel.AbstractKernel;

/**
 * Maximize[ <dependent variable>, <independent variable> ]
 */
public class CmdMaximize extends CommandProcessor {

	public CmdMaximize(AbstractKernel kernel) {
		super(kernel);
	}

	public GeoElement[] process(Command c) throws MyError {
		int n = c.getArgumentNumber();
		boolean[] ok = new boolean[n];
		GeoElement[] arg;

		switch (n) {
        case 2 :
            arg = resArgs(c);
            if ((ok[0] = (arg[0] .isGeoElement()))
                && (ok[1] = (arg[1] .isGeoNumeric()))) {
            	GeoElement[] ret=new GeoElement[1];
                ret[0] =
                         kernelA.Maximize(
                            c.getLabel(),
                            (GeoElement) arg[0],
                            (GeoNumeric) arg[1]);

                return ret;
            }

		default:
			throw argNumErr(app, c.getName(), n);
		}
	}//process(command)

}//CmdMaximize(kernel)