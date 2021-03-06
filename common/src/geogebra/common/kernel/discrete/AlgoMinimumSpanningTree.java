package geogebra.common.kernel.discrete;

import geogebra.common.kernel.Construction;
import geogebra.common.kernel.MyPoint;
import geogebra.common.kernel.algos.Algos;
import geogebra.common.kernel.discrete.signalprocessor.voronoi.VPoint;
import geogebra.common.kernel.discrete.signalprocessor.voronoi.VoronoiAlgorithm;
import geogebra.common.kernel.discrete.signalprocessor.voronoi.representation.AbstractRepresentation;
import geogebra.common.kernel.discrete.signalprocessor.voronoi.representation.RepresentationFactory;
import geogebra.common.kernel.discrete.signalprocessor.voronoi.representation.triangulation.TriangulationRepresentation;
import geogebra.common.kernel.discrete.signalprocessor.voronoi.representation.triangulation.VHalfEdge;
import geogebra.common.kernel.discrete.signalprocessor.voronoi.representation.triangulation.VVertex;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoList;
import geogebra.common.kernel.kernelND.GeoPointND;

import java.util.ArrayList;

public class AlgoMinimumSpanningTree extends AlgoHull{

	public AlgoMinimumSpanningTree(Construction cons, String label, GeoList inputList) {
		super(cons, label, inputList, null);
	}
	
    public Algos getClassName() {
        return Algos.AlgoMinimumSpanningTree;
    }
    
    public final void compute() {
    	
    	size = inputList.size();
    	if (!inputList.isDefined() ||  size == 0) {
    		locus.setUndefined();
    		return;
    	} 
    	
        if (vl == null) vl = new ArrayList<VPoint>();
        else vl.clear();
   	
		double inhom[] = new double[2];
   	
        
        AbstractRepresentation representation;
        representation = RepresentationFactory.createTriangulationRepresentation();

        for (int i = 0 ; i < size ; i++) {
			GeoElement geo = inputList.get(i);
			if (geo.isDefined() && geo.isGeoPoint()) {
				GeoPointND p = (GeoPointND)geo;
				p.getInhomCoords(inhom);
				vl.add( representation.createPoint(inhom[0], inhom[1]) );			
			}
		}

        
        representation = RepresentationFactory.createTriangulationRepresentation();
        
        TriangulationRepresentation trianglarrep = (TriangulationRepresentation) representation;
        
        trianglarrep.setDetermineMinSpanningTreeMode();
       
        TestRepresentationWrapper representationwrapper = new TestRepresentationWrapper();
        representationwrapper.innerrepresentation = representation;
        
        VoronoiAlgorithm.generateVoronoi(representationwrapper, vl);
        
        if (al == null) al = new ArrayList<MyPoint>();
        else al.clear();
        
        for ( VPoint point : trianglarrep.vertexpoints ) {
            VVertex vertex = (VVertex) point;
            
            // Check the vertex has edges
            if ( vertex.hasEdges()==false ) {
                continue;
            }
            

            // Paint each of those edges
            for ( VHalfEdge edge : vertex.getEdges() ) {
                // Simple addition to show MST
                if ( edge.shownonminimumspanningtree==false ) continue;
                
                VVertex vertex2 = edge.next.vertex;
                al.add(new MyPoint(vertex.x , vertex.y, false));
                al.add(new MyPoint(vertex2.x , vertex2.y, true));
                
                 }
            }

		locus.setPoints(al);
		locus.setDefined(true);
       
    }


}
