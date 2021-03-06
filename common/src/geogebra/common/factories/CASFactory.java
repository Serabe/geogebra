package geogebra.common.factories;

import geogebra.common.cas.CASparser;
import geogebra.common.cas.CasParserTools;
import geogebra.common.cas.mpreduce.AbstractCASmpreduce;

public abstract class CASFactory {
	public static CASFactory prototype;
	public abstract AbstractCASmpreduce newMPReduce(CASparser p,CasParserTools t);
}
