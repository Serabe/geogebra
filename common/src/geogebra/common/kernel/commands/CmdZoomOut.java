package geogebra.common.kernel.commands;

import geogebra.common.kernel.AbstractKernel;
import geogebra.common.kernel.arithmetic.Command;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoNumeric;
import geogebra.common.kernel.geos.GeoPoint2;
import geogebra.common.main.MyError;
import geogebra.common.euclidian.EuclidianViewInterface2D;
import geogebra.common.euclidian.EuclidianViewInterfaceSlim;

/**
 *ZoomOut
 */
public class CmdZoomOut extends CmdScripting {

	/**
	 * Create new command processor
	 * 
	 * @param kernel
	 *            kernel
	 */
	public CmdZoomOut(AbstractKernel kernel) {
		super(kernel);
	}

	final public void perform(Command c) throws MyError {
		int n = c.getArgumentNumber();
		GeoElement[] arg;

		switch (n) {
		case 1:
			arg = resArgs(c);
			if (arg[0].isGeoNumeric()) {
				GeoNumeric numGeo = (GeoNumeric) arg[0];

				EuclidianViewInterfaceSlim ev = app.getEuclidianView();
				double px = ev.getWidth() / 2.0; // mouseLoc.x;
				double py = ev.getHeight() / 2.0; // mouseLoc.y;

				double factor = numGeo.getDouble();

				if (AbstractKernel.isZero(factor))
					throw argErr(app, c.getName(), arg[0]);

				ev.zoom(px, py, 1 / factor, 4, true);

				app.setUnsaved();

				
				return;

			} else
				throw argErr(app, c.getName(), arg[0]);

		case 2:
			arg = resArgs(c);
			boolean ok0;
			if ((ok0 = arg[0].isGeoNumeric()) && arg[1].isGeoPoint()) {
				GeoNumeric numGeo = (GeoNumeric) arg[0];
				GeoPoint2 p = (GeoPoint2) arg[1];

				EuclidianViewInterface2D ev = (EuclidianViewInterface2D)app.getEuclidianView();
				double px = ev.toScreenCoordXd(p.inhomX); // mouseLoc.x;
				double py = ev.toScreenCoordYd(p.inhomY); // mouseLoc.y;

				double factor = numGeo.getDouble();
				if (AbstractKernel.isZero(factor))
					throw argErr(app, c.getName(), arg[0]);

				ev.zoom(px, py, 1 / factor, 4, true);

				app.setUnsaved();

				
				return;

			} else
				throw argErr(app, c.getName(), ok0 ? arg[1] : arg[0]);

		default:
			throw argNumErr(app, c.getName(), n);
		}
	}
}