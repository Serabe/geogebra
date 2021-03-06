package geogebra.common.kernel.statistics;
import geogebra.common.kernel.Construction;
import geogebra.common.kernel.algos.Algos;
import geogebra.common.kernel.geos.GeoList;

public class AlgoSampleVariance extends AlgoStats1D {

	private static final long serialVersionUID = 1L;

	public AlgoSampleVariance(Construction cons, String label, GeoList geoList) {
        super(cons,label,geoList,AlgoStats1D.STATS_SAMPLE_VARIANCE);
    }

    public Algos getClassName() {
        return Algos.AlgoSampleVariance;
    }
}
