package geogebra3D.kernel3D;

import geogebra.common.kernel.Construction;
import geogebra.common.kernel.Matrix.Coords;
import geogebra.common.kernel.algos.Algos;
import geogebra.common.kernel.arithmetic.NumberValue;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.kernelND.GeoPointND;


/**
 * Algo for cylinder between two end points and given radius.
 * @author mathieu
 *
 */
public class AlgoQuadricLimitedPointPointRadiusCylinder extends AlgoQuadricLimitedPointPointRadius {


	/**
	 * 
	 * @param c
	 * @param labels
	 * @param origin
	 * @param secondPoint
	 * @param r
	 */
	public AlgoQuadricLimitedPointPointRadiusCylinder(Construction c, String[] labels, GeoPointND origin, GeoPointND secondPoint, NumberValue r) {
		super(c, labels, origin, secondPoint, r, GeoQuadric3DLimited.QUADRIC_CYLINDER);
		
	}
	
	protected void createEnds(){
		AlgoQuadricEnds algo2 = new AlgoQuadricEnds((Construction)cons, getQuadric());
		((Construction) cons).removeFromConstructionList(algo2);
		bottom = algo2.getSection1();
		top = algo2.getSection2();

	}
	
	protected void setOutput(){
		output = new GeoElement[] {getQuadric(),getQuadric().getBottom(),getQuadric().getTop(),getQuadric().getSide()};
	}
	

	protected void setQuadric(Coords o1, Coords o2, Coords d, double r, double min, double max){
		getQuadric().setCylinder(o1,d,r, min, max);
	}

	

	public Algos getClassName() {
		return Algos.AlgoLimitedCylinder;
	}
	
}
