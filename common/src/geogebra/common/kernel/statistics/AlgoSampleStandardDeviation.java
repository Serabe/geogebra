package geogebra.common.kernel.statistics;
import geogebra.common.kernel.Construction;
import geogebra.common.kernel.algos.Algos;
import geogebra.common.kernel.geos.GeoList;

public class AlgoSampleStandardDeviation extends AlgoStats1D {

	private static final long serialVersionUID = 1L;

	public AlgoSampleStandardDeviation(Construction cons, String label, GeoList geoList) {
        super(cons,label,geoList,AlgoStats1D.STATS_SAMPLE_SD);
    }

	public AlgoSampleStandardDeviation(Construction cons, GeoList geoList) {
        super(cons,geoList,AlgoStats1D.STATS_SAMPLE_SD);
    }

    public Algos getClassName() {
        return Algos.AlgoSampleStandardDeviation;
    }
}

