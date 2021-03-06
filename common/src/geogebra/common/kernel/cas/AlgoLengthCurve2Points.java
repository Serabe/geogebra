package geogebra.common.kernel.cas;

import geogebra.common.kernel.Construction;
import geogebra.common.kernel.algos.Algos;
import geogebra.common.kernel.geos.GeoCurveCartesian;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoNumeric;
import geogebra.common.kernel.geos.GeoPoint2;
import geogebra.common.kernel.roots.RealRootFunction;

/**
 * @author Victor Franco Espino
 * @version 19-04-2007
 * 
 *          Calculate Curve Length between the points A and B: integral from t0
 *          to t1 on T = sqrt(a'(t)^2+b'(t)^2)
 */

public class AlgoLengthCurve2Points extends AlgoUsingTempCASalgo {

	private GeoPoint2 A, B; // input
	private GeoCurveCartesian c;
	GeoCurveCartesian derivative;
	private GeoNumeric length; // output
	private RealRootFunction lengthCurve; // is T = sqrt(a'(t)^2+b'(t)^2)

	public AlgoLengthCurve2Points(Construction cons, String label,
			GeoCurveCartesian c, GeoPoint2 A, GeoPoint2 B) {
		super(cons);
		this.A = A;
		this.B = B;
		this.c = c;
		length = new GeoNumeric(cons);

		// First derivative of curve f
		algoCAS = new AlgoDerivative(cons, c);
		derivative = (GeoCurveCartesian) ((AlgoDerivative) algoCAS).getResult();
		cons.removeFromConstructionList(algoCAS);

		lengthCurve = new LengthCurve();

		setInputOutput();
		compute();
		length.setLabel(label);
	}

	@Override
	public Algos getClassName() {
		return Algos.AlgoLengthCurve2Points;
	}

	@Override
	protected void setInputOutput() {
		input = new GeoElement[3];
		input[0] = c;
		input[1] = A;
		input[2] = B;

		setOutputLength(1);
		setOutput(0, length);
		setDependencies(); // done by AlgoElement
	}

	public GeoNumeric getLength() {
		return length;
	}

	@Override
	public final void compute() {
		if (!derivative.isDefined()) {
			length.setUndefined();
			return;
		}

		double a = c.getClosestParameter(A, c.getMinParameter());
		double b = c.getClosestParameter(B, c.getMinParameter());
		double lenVal = Math.abs(AlgoIntegralDefinite.numericIntegration(
				lengthCurve, a, b));
		length.setValue(lenVal);
	}

	/**
	 * T = sqrt(a'(t)^2+b'(t)^2)
	 */
	private class LengthCurve implements RealRootFunction {
		double f1eval[] = new double[2];

		public LengthCurve() {
			// TODO Auto-generated constructor stub
		}

		public double evaluate(double t) {
			derivative.evaluateCurve(t, f1eval);
			return (Math.sqrt(f1eval[0] * f1eval[0] + f1eval[1] * f1eval[1]));
		}
	}
}
