package geogebra.common.kernel.commands;

import geogebra.common.euclidian.AbstractEuclidianView;
import geogebra.common.euclidian.EuclidianViewInterfaceCommon;
import geogebra.common.euclidian.EuclidianViewInterfaceSlim;
import geogebra.common.kernel.arithmetic.Command;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoNumeric;
import geogebra.common.main.MyError;
import geogebra.common.kernel.Kernel;

public class CmdSetAxesRatio extends CmdScripting {

	/**
	 * Create new command processor
	 * 
	 * @param kernel
	 *            kernel
	 */
	public CmdSetAxesRatio(Kernel kernel) {
		super(kernel);
	}

	final public void perform(Command c) throws MyError {
		int n = c.getArgumentNumber();
		GeoElement[] arg;
		boolean ok0;
		switch (n) {
	
		case 2:
			arg = resArgs(c);
			
			if ((ok0 = arg[0].isGeoNumeric()) && arg[1].isGeoNumeric()) {
				
				GeoNumeric numGeo = (GeoNumeric) arg[0];
				GeoNumeric numGeo2 = (GeoNumeric) arg[1];
				EuclidianViewInterfaceCommon ev = app.getActiveEuclidianView();
				if(ev instanceof AbstractEuclidianView)
					((AbstractEuclidianView)ev).zoomAxesRatio(numGeo.getDouble()/numGeo2.getDouble(), true);
				
				return;

			}
			throw argErr(app, c.getName(), ok0 ? arg[1] : arg[0]);
		case 3:
			arg = resArgs(c);			
			if ((ok0 = arg[0].isGeoNumeric()) && arg[1].isGeoNumeric()) {
				
				GeoNumeric numGeo = (GeoNumeric) arg[0];
				GeoNumeric numGeo2 = (GeoNumeric) arg[1];
				GeoNumeric numGeo3 = (GeoNumeric) arg[2];
				EuclidianViewInterfaceSlim ev = app.getActiveEuclidianView();
				//TODO: Fix this once 3D view supports zoom
				if(!ev.isDefault2D()){
					ev.zoom(numGeo.getDouble()/numGeo3.getDouble(),
							numGeo2.getDouble()/numGeo3.getDouble(),  1, 3, true);
				}
				
				return;

			}
			throw argErr(app, c.getName(), ok0 ? arg[1] : arg[0]);
		default:
			throw argNumErr(app, c.getName(), n);
		}
	}
}
