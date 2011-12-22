package geogebra.common.kernel.commands;

import geogebra.common.kernel.AbstractKernel;
import geogebra.common.kernel.arithmetic.Command;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoList;
import geogebra.common.main.MyError;

public class CmdBarycenter extends CommandProcessor 
{

	public CmdBarycenter(AbstractKernel kernel) 
	{
		super(kernel);
	}

	final public GeoElement[] process(Command c) throws MyError {
		int n = c.getArgumentNumber();
		boolean[] ok = new boolean[n];
		GeoElement[] arg;

		switch (n) {
		case 2:
			arg = resArgs(c);
			if ((ok[0] = arg[0].isGeoList()) &&
					(ok[1] = arg[1].isGeoList())) {
				GeoElement[] ret = { kernelA.Barycenter(c.getLabel(),
						(GeoList)arg[0], (GeoList)arg[1])} ;
				return ret;
				
			} else{
				if(!ok[0])
					throw argErr(app, c.getName(), arg[0]);
				throw argErr(app, c.getName(), arg[1]);
			}
		default:
			throw argNumErr(app, c.getName(), n);
		}
	}
}