package geogebra.common.kernel.commands;

import geogebra.common.kernel.arithmetic.Command;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoList;
import geogebra.common.kernel.geos.GeoLocus;
import geogebra.common.kernel.geos.GeoNumeric;
import geogebra.common.kernel.geos.GeoText;
import geogebra.common.main.MyError;
import geogebra.common.kernel.Kernel;

/*
 * First[ <List>,n ]
 * Michael Borcherds
 * 2008-03-04
 */
public class CmdFirst extends CommandProcessor {

	public CmdFirst(Kernel kernel) {
		super(kernel);
	}

	public GeoElement[] process(Command c) throws MyError {
		int n = c.getArgumentNumber();
		GeoElement[] arg;
		arg = resArgs(c);
		
		switch (n) {
		case 1:

			if (arg[0].isGeoList()) {
				GeoElement[] ret = { 
						kernelA.First(c.getLabel(),
						(GeoList) arg[0], null ) };
				return ret;
			} else if (arg[0].isGeoText()) {
				GeoElement[] ret = { 
						kernelA.First(c.getLabel(),
						(GeoText) arg[0], null ) };
				return ret;
			} else
				throw argErr(app, c.getName(), arg[0]);
		case 2:
			boolean list = arg[0].isGeoList();
			boolean string = arg[0].isGeoText();
			boolean locus = arg[0].isGeoLocus();
			if ( list && arg[1].isGeoNumeric() ) {
				GeoElement[] ret = { 
						kernelA.First(c.getLabel(),
						(GeoList) arg[0], (GeoNumeric) arg[1] ) };
				return ret;
			} else if ( string && arg[1].isGeoNumeric() ) {
				GeoElement[] ret = { 
						kernelA.First(c.getLabel(),
						(GeoText) arg[0], (GeoNumeric) arg[1] ) };
				return ret;
			} else if ( locus && arg[1].isGeoNumeric() ) {
				GeoElement[] ret = { 
						kernelA.FirstLocus(c.getLabel(),
						(GeoLocus) arg[0], (GeoNumeric) arg[1] ) };
				return ret;
			} else
				throw argErr(app, c.getName(), (list && string) ? arg[1] : arg[0]);
			
		
		default:
			throw argNumErr(app, c.getName(), n);
		}
	}

}
