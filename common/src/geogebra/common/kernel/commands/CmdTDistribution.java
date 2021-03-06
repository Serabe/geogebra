package geogebra.common.kernel.commands;

import geogebra.common.kernel.arithmetic.Command;
import geogebra.common.kernel.arithmetic.NumberValue;
import geogebra.common.kernel.geos.GeoBoolean;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoFunction;
import geogebra.common.main.MyError;
import geogebra.common.kernel.Kernel;

/**
 *TDistribution
 */
public class CmdTDistribution extends CommandProcessor {

	/**
	 * Create new command processor
	 * 
	 * @param kernel
	 *            kernel
	 */
	public CmdTDistribution(Kernel kernel) {
		super(kernel);
	}

	public GeoElement[] process(Command c) throws MyError {
		int n = c.getArgumentNumber();
		boolean[] ok = new boolean[n];
		GeoElement[] arg;
		
		arg = resArgs(c);

		boolean cumulative = false; // default for n=2
		switch (n) {
		case 3:
			if (!arg[1].isGeoFunction() || !((GeoFunction)arg[1]).toString().equals("x")) {
				throw argErr(app, c.getName(), arg[1]);
			}
			
			if (arg[2].isGeoBoolean()) {
				cumulative = ((GeoBoolean)arg[2]).getBoolean();
			} else
				throw argErr(app, c.getName(), arg[2]);

			// fall through
		case 2:			
			if ((ok[0] = arg[0].isNumberValue()) ) {
				if (arg[1].isGeoFunction() && ((GeoFunction)arg[1]).toString().equals("x")) {

					// needed for eg Normal[1, 0.001, x] 
					kernelA.setTemporaryPrintFigures(15);
					String v = arg[0].getLabel();
					kernelA.restorePrintAccuracy();
					String command;
					
					if (cumulative) {
						command = "0.5+sign(x)/2*(betaRegularized(("+v+")/2,0.5,1)-betaRegularized(("+v+")/2,0.5,("+v+")/("+v+"+x^2)))";
					} else {
						command = "gamma(("+v+"+1)/2)*(1+x^2/("+v+"))^(-(("+v+"+1)/2))/(gamma(("+v+")/2)*sqrt(pi*("+v+")))";
					}
					
					
					GeoElement[] ret = (GeoElement[])kernelA.getAlgebraProcessor().processAlgebraCommand(command, true);
					return ret;


				} else if (arg[1].isNumberValue()) {
					GeoElement[] ret = { kernelA.TDistribution(c.getLabel(),
							(NumberValue) arg[0], (NumberValue) arg[1]) };
					return ret;
				} else
					throw argErr(app, c.getName(), arg[1]);

				} else 
					throw argErr(app, c.getName(), arg[0]);
				

			default:
				throw argNumErr(app, c.getName(), n);
			}
		}
	}
