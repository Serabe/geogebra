/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

 */

package geogebra.common.kernel.cas;

import geogebra.common.kernel.Kernel;
import geogebra.common.kernel.Construction;
import geogebra.common.kernel.algos.Algos;
import geogebra.common.kernel.geos.CasEvaluableFunction;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoFunction;
import geogebra.common.kernel.geos.GeoNumeric;

/**
 * Integral of a function
 * 
 * @author Markus Hohenwarter
 */
public class AlgoIntegral extends AlgoCasBase {

	private GeoNumeric var;

	public AlgoIntegral(Construction cons, String label,
			CasEvaluableFunction f, GeoNumeric var) {
		this(cons, f, var);
		g.toGeoElement().setLabel(label);
	}

	public AlgoIntegral(Construction cons, CasEvaluableFunction f,
			GeoNumeric var) {
		super(cons, f);
		this.var = var;

		setInputOutput(); // for AlgoElement
		compute();
	}

	@Override
	public Algos getClassName() {
		return Algos.AlgoIntegral;
	}

	@Override
	protected void setInputOutput() {
		int length = 1;
		if (var != null)
			length++;

		input = new GeoElement[length];
		length = 0;
		input[0] = (GeoElement) f.toGeoElement();
		if (var != null)
			input[++length] = var;

		setOutputLength(1);
		setOutput(0, (GeoElement) g.toGeoElement());
		setDependencies(); // done by AlgoElement
	}

	@Override
	protected void applyCasCommand() {

		// var.getLabel() can return a number in wrong alphabet (need ASCII)
		boolean internationalizeDigits = Kernel.internationalizeDigits;
		Kernel.internationalizeDigits = false;

		// get variable string with tmp prefix,
		// e.g. "x" becomes "ggbtmpvarx" here
		boolean isUseTempVariablePrefix = kernel.isUseTempVariablePrefix();
		kernel.setUseTempVariablePrefix(true);
		String varStr = var != null ? var.getLabel() : f.getVarString();
		kernel.setUseTempVariablePrefix(isUseTempVariablePrefix);
		Kernel.internationalizeDigits = internationalizeDigits;

		sbAE.setLength(0);
		sbAE.append("Integral(%");
		sbAE.append(",");
		sbAE.append(varStr);
		sbAE.append(")");

		// find symbolic derivative of f
		g.setUsingCasCommand(sbAE.toString(), f, true);
	}

	@Override
	final public String toString() {
		StringBuilder sb = new StringBuilder();

		if (var != null) {
			// Integral[ a x^2, x ]
			sb.append(super.toString());
		} else {
			// Michael Borcherds 2008-03-30
			// simplified to allow better Chinese translation
			sb.append(app.getPlain("IntegralOfA", f.toGeoElement().getLabel()));
		}

		if (!f.toGeoElement().isIndependent()) { // show the symbolic
													// representation too
			sb.append(": ");
			sb.append(g.toGeoElement().getLabel());
			if (g.toGeoElement() instanceof GeoFunction) {
				sb.append('(');
				sb.append(((GeoFunction) g.toGeoElement()).getVarString());
				sb.append(')');
			}
			sb.append(" = ");
			sb.append(g.toSymbolicString());
		}

		return sb.toString();
	}

}
