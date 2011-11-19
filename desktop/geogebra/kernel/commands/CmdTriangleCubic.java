package geogebra.kernel.commands;

import geogebra.kernel.Kernel;
import geogebra.kernel.arithmetic.Command;
import geogebra.kernel.arithmetic.NumberValue;
import geogebra.kernel.geos.GeoElement;
import geogebra.kernel.geos.GeoPoint;
import geogebra.main.MyError;

class CmdTriangleCubic extends CommandProcessor {

	/**
	 * Create new command processor
	 * 
	 * @param kernel
	 *            kernel
	 */
	public CmdTriangleCubic(Kernel kernel) {
		super(kernel);
	}

	final public GeoElement[] process(Command c) throws MyError {
		int n = c.getArgumentNumber();
		boolean[] ok = new boolean[n];
		GeoElement[] arg;

		switch (n) {
		case 4:						
			arg = resArgs(c);
			
			if ((ok[0] = arg[0].isGeoPoint()) &&
					(ok[1] = arg[1].isGeoPoint()) &&
					(ok[2] = arg[2].isGeoPoint()) &&
					(ok[3] = arg[3].isNumberValue())) {
				GeoElement[] ret = { kernel.TriangleCubic(c.getLabel(),
						(GeoPoint)arg[0], (GeoPoint)arg[1], (GeoPoint)arg[2],
						(NumberValue) arg[3])} ;
				return ret;
				
			}
				
			else{
				if(!ok[0])
					throw argErr(app, c.getName(), arg[0]);
				if(!ok[1])
					throw argErr(app, c.getName(), arg[1]);
				if(!ok[2])
					throw argErr(app, c.getName(), arg[2]);
				throw argErr(app, c.getName(), arg[3]);
			}
		default:
			throw argNumErr(app, "TriangleCubic", n);
		}
	}
}