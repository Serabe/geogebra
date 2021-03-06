/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

*/

package geogebra.common.kernel.geos;

import geogebra.common.kernel.Construction;
import geogebra.common.kernel.Kernel;
import geogebra.common.kernel.arithmetic.ExpressionNode;
import geogebra.common.kernel.arithmetic.Function;
import geogebra.common.kernel.arithmetic.FunctionVariable;
import geogebra.common.kernel.arithmetic.MyDouble;
import geogebra.common.kernel.arithmetic.NumberValue;
import geogebra.common.kernel.arithmetic.ExpressionNodeConstants.StringType;
import geogebra.common.kernel.cas.CASGenericInterface;
import geogebra.common.plugin.GeoClass;
import geogebra.common.plugin.Operation;
import geogebra.common.util.Unicode;

import java.util.ArrayList;


/**
 * Explicit function in one variable ("x") in the 
 * form of an If-Then-Else statement
 * 
 * example:  If[ x < 2, x^2, x + 2 ]
 * where "x < 2" is a boolean function
 * 
 * @author Markus Hohenwarter
 */
public class GeoFunctionConditional extends GeoFunction {
	
	private boolean isDefined = true;

	private GeoFunction condFun, ifFun, elseFun;

	private Function uncondFun;	

	public GeoFunctionConditional(Construction c) {
		super(c);
	}	

	/**
	 * Creates a new GeoFunctionConditional object.
	 * 
	 * @param c
	 * @param condFun a GeoFunction that evaluates
	 * 	to a boolean value (i.e. isBooleanFunction() returns true)
	 * @param ifFun
	 * @param elseFun may be null
	 */
	public GeoFunctionConditional(Construction c, 
			GeoFunction condFun, GeoFunction ifFun, GeoFunction elseFun) {
		super(c);
		this.condFun 	= condFun;		
		this.ifFun 		= ifFun;		
		this.elseFun 	= elseFun;					
	}	
	
	/**
	 * Copy constructor
	 * @param geo
	 */
	public GeoFunctionConditional(GeoFunctionConditional geo) {
		super(geo.cons);		
		set(geo);					
	}	
	
	@Override
	public GeoElement copy() {
		return new GeoFunctionConditional(this);
	}		
	
	@Override
	public void set(GeoElement geo) {		
		GeoFunctionConditional geoFunCond = (GeoFunctionConditional) geo;
		isDefined = geoFunCond.isDefined;
			
		if (condFun == null) { 
			condFun = (GeoFunction) geoFunCond.condFun.copyInternal(cons);			
		}	
		
		if (isAlgoMacroOutput()) {
			condFun.setAlgoMacroOutput(true);
			condFun.setParentAlgorithm(getParentAlgorithm());
			condFun.setConstruction(cons);
		}		
		condFun.set(geoFunCond.condFun);
		
		if (ifFun == null) {
			ifFun = (GeoFunction) geoFunCond.ifFun.copyInternal(cons);				
		}
		if (isAlgoMacroOutput()) {
			ifFun.setAlgoMacroOutput(true);
			ifFun.setParentAlgorithm(getParentAlgorithm());
			ifFun.setConstruction(cons);
		}
		ifFun.set(geoFunCond.ifFun);
		
		if (geoFunCond.elseFun == null) {
			elseFun = null;
		} else {
			if (elseFun == null) {
				elseFun = (GeoFunction) geoFunCond.elseFun.copyInternal(cons);					
			}			
			if (isAlgoMacroOutput()) {
				elseFun.setAlgoMacroOutput(true);
				elseFun.setParentAlgorithm(getParentAlgorithm());	
				elseFun.setConstruction(cons);
			}
			elseFun.set(geoFunCond.elseFun);
		}		
		uncondFun = null; //will be evaluated in getFunction()
	}	
	
	
	@Override
	public String getClassName() {
		return "GeoFunctionConditional";
	}
	
	@Override
	public String getTypeString() {
		return "Function";
	}		
	
    @Override
	public GeoClass getGeoClassType() {
    	return GeoClass.FUNCTIONCONDITIONAL;
    }
    
    @Override
	public boolean isDefined() {
		return isDefined;
	}    
      
    /**
     * Returns the function which is used, if condition is satisfied
     * @return if branch of function
     */
    final public GeoFunction getIfFunction() {
    	return ifFun;
    }
    
    /**
     * Returns condition which determines if "ifFun" or "elseFun" is used
     * @return condition of function
     */
    final public GeoFunction getCondFunction() {
    	return condFun;
    }
    
    /**
     * Returns the function which is used, if condition is not satisfied
     * @return else branch of function
     */
    final public GeoFunction getElseFunction() {
    	return elseFun;
    }
            
    
	 /**
     * Replaces geo and all its dependent geos in this function's
     * expressions by copies of their values.
     */
    @Override
	public void replaceChildrenByValues(GeoElement geo) {     	
    	if (condFun != null) {
    		condFun.replaceChildrenByValues(geo);
    	}
    	if (ifFun != null) {
    		ifFun.replaceChildrenByValues(geo);
    	}
    	if (elseFun != null) {
    		elseFun.replaceChildrenByValues(geo);
    	}
    	uncondFun = null;
    }
      
    /**
	 * Set this function to the n-th derivative of f
	 * @param f
	 * @param n order of derivative
	 */
	@Override
	public void setDerivative(CasEvaluableFunction f, int n) {		
		GeoFunctionConditional fcond = (GeoFunctionConditional) f;
		ifFun.setDerivative(fcond.ifFun, n);
		if (elseFun != null)
			elseFun.setDerivative(fcond.elseFun, n);		
	}
	
				
	/**
	 * Returns this function's value at position x.
	 *
	 * @param x 
	 * @return f(x) = condition(x) ? ifFun(x) : elseFun(x)
	 */
	@Override
	final public double evaluate(double x) {	
        if (interval) {
            // check if x is in interval [a, b]
            if (x < intervalMin || x > intervalMax) 
            	return Double.NaN;           
        }
		
		if (condFun.evaluateBoolean(x))
			return ifFun.evaluate(x);
		else {
			if (elseFun == null)
				return Double.NaN;
			else
				return elseFun.evaluate(x);
		}		
	}	
	
	@Override
	public void translate(double vx, double vy) {	
		// translate condition by vx, thus
		// changing every x into (x - vx)
		condFun.translate(vx, 0);
		
		// translate if and else parts too
		ifFun.translate(vx, vy);	
		if (elseFun != null)
			elseFun.translate(vx, vy);			
		uncondFun = null;
	}
	
	@Override
	public void dilate(NumberValue r, GeoPoint2 S) {
		condFun.dilate(r, S);
		
		// translate if and else parts too
		ifFun.dilate(r, S);	
		if (elseFun != null)
			elseFun.dilate(r, S);			
		uncondFun = null;
	}
	
	/**
	 * Returns non-conditional function f which satisfies f(x)=this(x) if x
	 * satisfies conditional function and f(x)=0 otherwise
	 */
	@Override
	public Function getFunction() {
		if (uncondFun == null) {
			ExpressionNode en = new ExpressionNode(kernel, condFun
					.getFunctionExpression(), Operation.MULTIPLY, ifFun
					.getFunctionExpression());
			if (elseFun != null)
				en = new ExpressionNode(kernel, en, Operation.PLUS,
						new ExpressionNode(kernel, new ExpressionNode(kernel,
								condFun.getFunctionExpression(),
								Operation.NOT, null),
								Operation.MULTIPLY, elseFun
										.getFunctionExpression()));
			ExpressionNode en2 = en.getCopy(kernel);
			en2.replaceAndWrap(condFun.getFunction().getFunctionVariable(), ifFun
					.getFunction().getFunctionVariable());
			if (elseFun != null)
				en2.replaceAndWrap(elseFun.getFunction().getFunctionVariable(), ifFun
						.getFunction().getFunctionVariable());
			uncondFun = new Function(en2, ifFun.getFunction().getFunctionVariable());
		}
		return uncondFun;
	}
	
	/**
	 * Returns the corresponding Function for the given x-value.
	 * This is important for conditional functions where we have
	 * two different Function objects.
	 */
	@Override
	public Function getFunction(double x) {
		if (elseFun == null) { 
			return ifFun.getFunction(x);
		} else {
			if (condFun.evaluateBoolean(x))
				return ifFun.getFunction(x);
			else 
				return elseFun.getFunction(x);
		}
	}		
		
	@Override
	public GeoFunction getGeoDerivative(int order){	
		if (derivGeoFun == null) {
			derivGeoFun = new GeoFunctionConditional(this);
		}
		
		derivGeoFun.setDerivative(this, order);
		return derivGeoFun;				
	}
	private GeoFunctionConditional derivGeoFun;
			
	@Override
	public boolean isPolynomialFunction(boolean forRootFinding, boolean symbolic) {		
		return false;   			
	}		
	

	
	@Override
	public final String toString() {
		sbToString.setLength(0);
		if (isLabelSet()) {
			sbToString.append(label);
			sbToString.append("(x) = ");
		}		
		sbToString.append(toValueString());
		return sbToString.toString();
	}
	private StringBuilder sbToString = new StringBuilder(80);
	
	@Override
	final public String toValueString() {					
		return toString(false);
	}	
	
	@Override
	final public String toSymbolicString() {					
		return toString(true);
	}
	
	@Override
	public String getCASString(boolean symbolic) {
		return toString(symbolic);
	}
	
	private String toString(boolean symbolic) {		
		if (!isDefined())
			return app.getPlain("undefined");
		
		// for CAS, translate to CAS format :)
		if (kernel.getCASPrintForm() .equals(StringType.MATH_PIPER)
				|| kernel.getCASPrintForm() .equals(StringType.MAXIMA)
				|| kernel.getCASPrintForm() .equals(StringType.MPREDUCE)) {
			//TODO: implement if in mpreduce
			CASGenericInterface cas = kernel.getGeoGebraCAS().getCurrentCAS();
			String cmd = cas.getTranslatedCASCommand(elseFun == null ? "If.2" : "If.3");
			if (symbolic) {
				cmd = cmd.replace("%0", condFun.toSymbolicString());
				cmd = cmd.replace("%1", ifFun.toSymbolicString());
				if (elseFun != null)
					cmd = cmd.replace("%2", elseFun.toSymbolicString());
			} else {
				cmd = cmd.replace("%0", condFun.toValueString());
				cmd = cmd.replace("%1", ifFun.toValueString());
				if (elseFun != null)
					cmd = cmd.replace("%2", elseFun.toValueString());
			}
				
			return cmd;
		}
		
		StringBuilder sb = new StringBuilder(80);
		sb.append(app.getCommand("If"));
		sb.append('[');
		
		if (symbolic) {
			sb.append(condFun.toSymbolicString());
			sb.append(", ");	
			sb.append(ifFun.toSymbolicString());
		} else {
			sb.append(condFun.toValueString());
			sb.append(", ");
			sb.append(ifFun.toValueString());
		}
			
		if (elseFun != null) {
			sb.append(", ");
			if (symbolic)
				sb.append(elseFun.toSymbolicString());
			else
				sb.append(elseFun.toValueString());
		}
		sb.append(']');
		
		return sb.toString();
	}	

	
	@Override
	final public String toLaTeXString(boolean symbolic) {	
		return toString(symbolic);
	}

	@Override
	public boolean isGeoFunction() {
		return true;
	}		
	
	@Override
	public boolean isGeoFunctionConditional() {		
		return true;
	}
	
	@Override
	public boolean isBooleanFunction() {
		return false;
	}

	@Override
	final public boolean isEqual(GeoElement geo) {

		if (!geo.getGeoClassType().equals(GeoClass.FUNCTIONCONDITIONAL))
			return false;
		
		GeoFunctionConditional geoFun = (GeoFunctionConditional)geo;
		
		// TODO better CAS checking for condFun
		
		return 		condFun.toValueString().equals(geoFun.condFun.toValueString())
					&& ifFun.isEqual(geoFun.ifFun)
					&& ( elseFun != null && elseFun.isEqual(geoFun.elseFun));		

		
	}
	
	@Override
	final public boolean evaluateCondition(double x) {
		return condFun.evaluateBoolean(x);
	}
	
	@Override
	public String getLimit(double x, int direction) {
		if (evaluateCondition(x-2*direction*Kernel.getEpsilon()))
			return ifFun.getLimit(x, direction);
			else if (elseFun != null) return elseFun.getLimit(x, direction);
		return null;
	}

	@Override
	public void getVerticalAsymptotes(GeoFunction f, StringBuilder verticalSB, boolean reverse) {
		ifFun.getVerticalAsymptotes((GeoFunction)this, verticalSB, false);
		if (elseFun != null) elseFun.getVerticalAsymptotes((GeoFunction)this, verticalSB, true);
	}

	@Override
	public void getDiagonalPositiveAsymptote(GeoFunction f, StringBuilder verticalSB) {
		if (evaluateCondition(Double.POSITIVE_INFINITY))
			ifFun.getDiagonalPositiveAsymptote((GeoFunction)this, verticalSB);
			else if (elseFun != null) elseFun.getDiagonalPositiveAsymptote((GeoFunction)this, verticalSB);
	}

	@Override
	public void getDiagonalNegativeAsymptote(GeoFunction f, StringBuilder verticalSB) {
		if (evaluateCondition(Double.NEGATIVE_INFINITY))
			ifFun.getDiagonalNegativeAsymptote((GeoFunction)this, verticalSB);
			else if (elseFun != null) elseFun.getDiagonalNegativeAsymptote((GeoFunction)this, verticalSB);
	}

	@Override
	public void getHorizontalPositiveAsymptote(GeoFunction f, StringBuilder verticalSB) {
		if (evaluateCondition(Double.POSITIVE_INFINITY))
		ifFun.getHorizontalPositiveAsymptote((GeoFunction)this, verticalSB);
		else if (elseFun != null) elseFun.getHorizontalPositiveAsymptote((GeoFunction)this, verticalSB);

	}

	@Override
	public void getHorizontalNegativeAsymptote(GeoFunction f, StringBuilder verticalSB) {
		if (evaluateCondition(Double.NEGATIVE_INFINITY))
		ifFun.getHorizontalNegativeAsymptote((GeoFunction)this, verticalSB);
		else if (elseFun != null) elseFun.getHorizontalNegativeAsymptote((GeoFunction)this, verticalSB);

	}
	
	/**
	 * Sets this function by applying a GeoGebraCAS command to a function.
	 * 
	 * @param ggbCasCmd the GeoGebraCAS command needs to include % in all places
	 * where the function f should be substituted, e.g. "Derivative(%,x)"
	 * @param f the function that the CAS command is applied to
	 */
	@Override
	public void setUsingCasCommand(String ggbCasCmd, CasEvaluableFunction f, boolean symbolic){
		GeoFunctionConditional ff = (GeoFunctionConditional) f;
		
		if (ff.ifFun != null) {
			ifFun.setUsingCasCommand(ggbCasCmd, ff.ifFun, symbolic);
		} else {
			ifFun = null;
		}
		
		if (ff.elseFun != null) {
			elseFun.setUsingCasCommand(ggbCasCmd, ff.elseFun, symbolic);
		} else {
			elseFun = null;
		}
	}

	public String conditionalLaTeX(boolean substituteNumbers) {		
		StringBuilder sb = new StringBuilder();
		
		if (getElseFunction() == null && !ifFun.isGeoFunctionConditional()) {
			sb.append(getIfFunction().getFormulaString(StringType.LATEX, substituteNumbers));
			sb.append(" \\;\\;\\;\\; \\left(");
			sb.append(getCondFunction().getFormulaString(StringType.LATEX, substituteNumbers));
			sb.append(" \\right)");
			
		} else {			
			ArrayList<ExpressionNode> cases = new ArrayList<ExpressionNode>();
			ArrayList<Bounds> conditions = new ArrayList<Bounds>();
			boolean complete = collectCases(cases,conditions,new Bounds());
			sb.append("\\left\\{\\begin{array}{ll} ");
			for(int i=0;i<cases.size();i++){
				sb.append(cases.get(i).toLaTeXString(!substituteNumbers));
				sb.append("& : ");				
				if(i==cases.size()-1 && complete){										
					sb.append("\\text{");
					sb.append(app.getPlain("otherwise"));
					sb.append("}");
				} else {
					sb.append(conditions.get(i).toLaTeXString(!substituteNumbers,getVarString()));
					if(i!=cases.size()-1)sb.append("\\\\ ");
				}
			}
			sb.append(" \\end{array}\\right. ");
		}

		return sb.toString();
	}

	private boolean collectCases(ArrayList<ExpressionNode> cases,
			ArrayList<Bounds> conditions,Bounds parentCond) {
		boolean complete = elseFun != null;
		Bounds positiveCond = parentCond.addRestriction(condFun.getFunctionExpression());
		Bounds negativeCond = parentCond.addRestriction(condFun.getFunctionExpression().negation());			
		if(ifFun instanceof GeoFunctionConditional){
			complete &= ((GeoFunctionConditional)ifFun).collectCases(cases, conditions, positiveCond);
		}else{
			cases.add(ifFun.getFunctionExpression());
			conditions.add(positiveCond);
		}
		
		if(elseFun instanceof GeoFunctionConditional){
			complete &= ((GeoFunctionConditional)elseFun).collectCases(cases, conditions, negativeCond);
		}else if(elseFun!=null){
			cases.add(elseFun.getFunctionExpression());
			conditions.add(negativeCond);
		}
		return complete;
	}	
	
	class Bounds{
		private boolean lowerSharp,upperSharp;
		private Double lower,upper;
		private ExpressionNode condition;
		
		public Bounds addRestriction(ExpressionNode e){
			if(e.getOperation().equals(Operation.AND)){
				return addRestriction(e.getLeftTree()).addRestriction(e.getRightTree());
			}
			Bounds b = new Bounds();
			b.lower = lower;
			b.upper = upper;
			b.lowerSharp = lowerSharp;
			b.upperSharp = upperSharp;
			b.condition = condition;
			boolean simple = e.getOperation()==Operation.GREATER||
			e.getOperation()==Operation.GREATER_EQUAL||
			e.getOperation()==Operation.LESS||
			e.getOperation()==Operation.LESS_EQUAL||
			e.getOperation()==Operation.EQUAL_BOOLEAN;
						
			if(simple && e.getLeft() instanceof FunctionVariable && e.getRight() instanceof MyDouble){
				double d = ((MyDouble)e.getRight()).getDouble();				
				if(e.getOperation()==Operation.GREATER && (lower == null || lower<=d))//x > d
					{b.lower = d; b.lowerSharp = true;}
				else if((e.getOperation()==Operation.GREATER_EQUAL || e.getOperation()==Operation.EQUAL_BOOLEAN)&& (lower == null || lower<d))//x > d
					{b.lower = d; b.lowerSharp = false;}
				else if(e.getOperation()==Operation.LESS && (upper == null || upper>=d))//x > d
					{b.upper = d; b.upperSharp = true;}
				if((e.getOperation()==Operation.LESS_EQUAL|| e.getOperation()==Operation.EQUAL_BOOLEAN) && (upper == null || upper>d))//x > d
					{b.upper = d; b.upperSharp = false;}
			}
			else if(simple && e.getRight() instanceof FunctionVariable && e.getLeft() instanceof MyDouble){
				double d = ((MyDouble)e.getLeft()).getDouble();
				if(e.getOperation()==Operation.LESS && (lower == null || lower<=d))//x > d
					{b.lower = d; b.lowerSharp = true;}
				else if((e.getOperation()==Operation.LESS_EQUAL || e.getOperation()==Operation.EQUAL_BOOLEAN) && (lower == null || lower<d))//x > d
					{b.lower = d; b.lowerSharp = false;}
				else if(e.getOperation()==Operation.GREATER && (upper == null || upper>=d))//x > d
					{b.upper = d; b.upperSharp = true;}
				if((e.getOperation()==Operation.GREATER_EQUAL|| e.getOperation()==Operation.EQUAL_BOOLEAN) && (upper == null || upper>d))//x > d
					{b.upper = d; b.upperSharp = false;}
			}else{
				if(condition==null)
					b.condition = e;
				else b.condition = condition.and(e);
			}
			return b;
		}

		public Object toLaTeXString(boolean b,String varString) {
			String ret = null;
			if(upper == null && lower!= null)
				ret = varString+" "+(lowerSharp?">":Unicode.GREATER_EQUAL)+" "+kernel.format(lower);
			else if(lower == null && upper != null)
				ret = varString+" "+(upperSharp?"<":Unicode.LESS_EQUAL)+" "+kernel.format(upper);
			else if(lower!=null && upper!=null){				
				if(Kernel.isEqual(lower,upper) && !lowerSharp && !upperSharp)
					ret=varString+" = "+kernel.format(lower);
				else
					ret = kernel.format(lower)+" "+(lowerSharp?"<":Unicode.LESS_EQUAL)+" "+varString+" "+(upperSharp?"<":Unicode.LESS_EQUAL)+" "+kernel.format(upper);
			}
			if(condition!=null && ret == null)
				return condition.toLaTeXString(b);
			else if(condition!=null)
				ret="("+ret+")\\wedge \\left("+condition.toLaTeXString(b)+"\\right)";			
			return ret;
		}
	}
	
	@Override
	public void toGeoCurveCartesian(GeoCurveCartesian curve) {
		FunctionVariable t = new FunctionVariable(kernel,"t");
		ExpressionNode en = new ExpressionNode(kernel,this,Operation.FUNCTION,t);
		Function fn = new Function(en,t);
		curve.setFunctionY(fn);
		Function varFun = new Function(new ExpressionNode(kernel,t),t);
		curve.setFunctionX(varFun);
		double min = app.getXminForFunctions();
		double max = app.getXmaxForFunctions();
		curve.setInterval(min, max);   
		curve.setHideRangeInFormula(true);
	}

	@Override
	public String toOutputValueString() {
		return toValueString();
		
	}	


}
