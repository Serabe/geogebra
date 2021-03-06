package geogebra3D.kernel3D;

import geogebra.common.kernel.Construction;
import geogebra.common.kernel.Matrix.Coords;
import geogebra.common.kernel.arithmetic.NumberValue;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoNumeric;
import geogebra.common.kernel.geos.GeoPolygon;
import geogebra.common.kernel.kernelND.GeoPointND;

/**
 * @author ggb3D
 * 
 * Creates a new GeoPolyhedron
 *
 */
public abstract class AlgoPolyhedronPoints extends AlgoPolyhedron{

	
	private GeoPointND[] bottomPoints;
	protected GeoPointND[] points;
	private GeoPointND topPoint;
	protected GeoPolygon bottom;
	protected NumberValue height;
	
	protected boolean bottomAsInput = false;
	protected int bottomPointsLength = -1;
	
	protected OutputHandler<GeoSegment3D> outputSegmentsBottom, outputSegmentsSide, outputSegmentsTop;
	protected OutputHandler<GeoPolygon3D> outputPolygonsBottom, outputPolygonsSide, outputPolygonsTop;
	
	/////////////////////////////////////////////
	// POLYHEDRON OF DETERMINED TYPE
	////////////////////////////////////////////
	
	
	
	/** creates a polyhedron regarding vertices 
	 * @param c construction 
	 * @param labels 
	 * @param points 
	 */
	public AlgoPolyhedronPoints(Construction c, String[] labels, GeoPointND[] points) {
		super(c);

		bottomPoints = new GeoPointND[points.length-1];
		for (int i=0; i<points.length-1; i++)
			bottomPoints[i] = points[i];
		topPoint = points[points.length-1];
		shift=1; //output points are shifted of 1 to input points (one less)
			
		
		createPolyhedron();
		
		
		update();
		
		// input : inputPoints or list of faces
		input = new GeoElement[points.length];
		for (int i=0; i<points.length; i++)
			input[i] = (GeoElement) points[i];		
		addAlgoToInput();
		
		createFaces();
		setOutput();
		
		
        setLabels(labels);
	}
	
	
	/** creates a polyhedron regarding bottom face and top vertex 
	 * @param c construction 
	 * @param labels 
	 * @param polygon 
	 * @param point 
	 */
	public AlgoPolyhedronPoints(Construction c, String[] labels, GeoPolygon polygon, GeoPointND point) {
		super(c);
		
		bottom = polygon;
		bottomAsInput = true;
		topPoint = point;
		shift=1; //output points are shifted of 1 to input points (one less)
		
		createPolyhedron();

		update();
		
		// input : inputPoints or list of faces
		input = new GeoElement[2];
		input[0]=bottom;
		input[1]=(GeoElement) topPoint;
		addAlgoToInput();
		

		
		createFaces();
		setOutput(); 
        
        setLabels(labels);
        
	}
	
	
	
	@Override
	protected void createOutputSegments(){
		outputSegmentsBottom=createOutputSegmentsHandler();
		outputSegmentsSide=createOutputSegmentsHandler();
		outputSegmentsTop=createOutputSegmentsHandler();
	}
	
	

	@Override
	protected void createOutputPolygons(){
		outputPolygonsBottom=createOutputPolygonsHandler();
		outputPolygonsSide=createOutputPolygonsHandler();
		outputPolygonsTop=createOutputPolygonsHandler();
	}
	

	/**
	 * 
	 * @param labels labels
	 */
	protected void setLabels(String[] labels){
		if (labels==null || labels.length <= 1 || isOldFileVersion())
			polyhedron.initLabels(labels);
		else{
			augmentOutputSize(labels.length);
			for (int i=0; i<labels.length; i++)
				getOutput(i).setLabel(labels[i]);
		}
		
	}

	/**
	 * augment the output size if needed (in case of undefined but labelled outputs)
	 * @param length labels length
	 */
	abstract protected void augmentOutputSize(int length);
	

	
	/** creates a polyhedron regarding bottom face and top vertex 
	 * @param c construction 
	 * @param labels 
	 * @param polygon 
	 * @param height 
	 */
	public AlgoPolyhedronPoints(Construction c, String[] labels, GeoPolygon polygon, NumberValue height) {
		super(c);
		
		bottom = polygon;
		bottomAsInput = true;
		this.height = height;
		shift=0; //output points correspond to input points
		
		outputPoints.augmentOutputSize(1);
		topPoint=outputPoints.getElement(0);
		polyhedron.addPointCreated((GeoPoint3D) topPoint);
		createPolyhedron();
		
		update();
		
		// input : inputPoints or list of faces
		input = new GeoElement[2];
		input[0]=bottom;
		input[1]=(GeoElement) height;
		addAlgoToInput();
		
		createFaces();
		setOutput(); 
		
		if (height instanceof GeoNumeric){
			if (((GeoNumeric) height).isIndependent()){

				for (GeoPolygon p : polyhedron.getPolygons()){
					p.setCoordParentNumber((GeoNumeric) height);
					p.setCoordParentDirector(bottom);
				}
				
				//getTopFace().setCoordParentNumber((GeoNumeric) height);
				//getTopFace().setCoordParentDirector(bottom);
			}
		}
      
		setLabels(labels);
	}

    
	
	
	/**
	 * create the polyhedron (faces and edges)
	 * @param polyhedron
	 */
	protected abstract void createPolyhedron();
	
	protected abstract void updateOutput(int n, GeoPointND[] bottomPoints);
	
	/**
	 * sets the bottom of the polyhedron
	 * @param polyhedron
	 * @return bottom key (if one)
	 */
	protected void setBottom(GeoPolyhedron polyhedron){
		if (bottom!=null){
			polyhedron.addPolygonLinked(bottom);
		}else{
			GeoPointND[] bottomPoints = getBottomPoints();
			
			polyhedron.startNewFace();
			for (int i=0; i<bottomPoints.length; i++){
				polyhedron.addPointToCurrentFace(bottomPoints[i]);
			}
			polyhedron.endCurrentFace();
		}
	}
	
	protected GeoPolygon getBottom(){
		if (bottom!=null)
			return bottom;
		else
			return outputPolygonsBottom.getElement(0);
	}
	
	
	
	
	/////////////////////////////////////////////
	// END OF THE CONSTRUCTION
	////////////////////////////////////////////

	private Coords uptranslation;
	
	/**
	 * 
	 * @return translation vector from bottom to top
	 */
	protected Coords getUpTranslation(){
		return uptranslation;
	}
	
	private int shift;
	
	/**
	 * shift used when first top point is input
	 * @return 1 when first top point is input, 0 else
	 */
	protected int getShift(){
		return shift;
	}
	
	
	/**
	 * pre computation
	 * @return true if the polyhedron is defined
	 */
	public boolean preCompute() {
		
		//check if bottom points length has changed (e.g. with regular polygon)
		if (bottomAsInput){
			if (!getBottom().isDefined()){
				polyhedron.setUndefined();
				return false;
			}
			polyhedron.setDefined();
			updateOutput(bottom.getPointsLength(),getBottomPoints());
		}
		
		
		
		//recompute the translation from bottom to top
		if (height==null)
			uptranslation = getTopPoint().getInhomCoordsInD(3).sub(getBottomPoints()[0].getInhomCoordsInD(3));
		else
			uptranslation=bottom.getMainDirection().normalized().mul(height.getDouble());		
		

		return true;
	}
	
	
	
	
	
	

	
	
	
	/**
	 * 
	 * @return bottom points
	 */
	protected GeoPointND[] getBottomPoints(){
		if (bottom!=null)
			return bottom.getPointsND();
		else
			return bottomPoints;
	}
	

	/**
	 * 
	 * @return top point
	 */
	protected GeoPointND getTopPoint(){
		return topPoint;
	}



    
    
    
    ///////////////////////////////////////////////////////
    // FOR PREVIEWABLE   
    ///////////////////////////////////////////////////////
    

	/**
	 * set visibility of output segments and polygons
	 * @param visible
	 */
	public void setOutputSegmentsAndPolygonsEuclidianVisible(boolean visible){
		for (int i=0; i<outputSegmentsBottom.size(); i++)
			outputSegmentsBottom.getElement(i).setEuclidianVisible(visible);
		for (int i=0; i<outputSegmentsSide.size(); i++)
			outputSegmentsSide.getElement(i).setEuclidianVisible(visible);
		for (int i=0; i<outputSegmentsTop.size(); i++)
			outputSegmentsTop.getElement(i).setEuclidianVisible(visible);
		for (int i=0; i<outputPolygonsBottom.size(); i++)
			outputPolygonsBottom.getElement(i).setEuclidianVisible(visible, false);
		for (int i=0; i<outputPolygonsSide.size(); i++)
			outputPolygonsSide.getElement(i).setEuclidianVisible(visible, false);
		for (int i=0; i<outputPolygonsTop.size(); i++)
			outputPolygonsTop.getElement(i).setEuclidianVisible(visible, false);
	}
	
	/**
	 * notify kernel update of output segments and polygons
	 */
	public void notifyUpdateOutputSegmentsAndPolygons(){
		for (int i=0; i<outputSegmentsBottom.size(); i++)
			getKernel().notifyUpdate(outputSegmentsBottom.getElement(i));
		for (int i=0; i<outputSegmentsSide.size(); i++)
			getKernel().notifyUpdate(outputSegmentsSide.getElement(i));
		for (int i=0; i<outputSegmentsTop.size(); i++)
			getKernel().notifyUpdate(outputSegmentsTop.getElement(i));
		for (int i=0; i<outputPolygonsBottom.size(); i++)
			getKernel().notifyUpdate(outputPolygonsBottom.getElement(i));
		for (int i=0; i<outputPolygonsSide.size(); i++)
			getKernel().notifyUpdate(outputPolygonsSide.getElement(i));
		for (int i=0; i<outputPolygonsTop.size(); i++)
			getKernel().notifyUpdate(outputPolygonsTop.getElement(i));
	}


	/**
	 * set output points invisible (use for previewable)
	 * @param visible 
	 */
	public void setOutputPointsEuclidianVisible(boolean visible){
		for (int i=0; i<outputPoints.size(); i++)
			outputPoints.getElement(i).setEuclidianVisible(visible);
	}
	
	
	/**
	 * notify kernel update of output points
	 */
	public void notifyUpdateOutputPoints(){
		for (int i=0; i<outputPoints.size(); i++)
			getKernel().notifyUpdate(outputPoints.getElement(i));
	}
	

	
	/**
	 * used for previewable of prism
	 * @return the middle point of the bottom face (for prism)
	 */
	public Coords getBottomMiddlePoint(){
		Coords ret = new Coords(4);
		
		GeoPointND[] points = getBottomPoints();
			
		
		for (int i=0; i<points.length; i++)
			ret = ret.add(points[i].getCoordsInD(3));
		
		return ret.mul((double) 1/points.length);
	}

	/**
	 * used for previewable of prism
	 * @return the middle point of the top face (for prism)
	 */
	public Coords getTopMiddlePoint(){
		Coords ret = new Coords(4);
		for (int i=0; i<outputPoints.size(); i++)
			ret = ret.add(outputPoints.getElement(i).getCoordsInD(3));
		
		return ret.mul((double) 1/outputPoints.size());
	}
	
	
	public GeoPolygon getTopFace(){
		return outputPolygonsTop.getElement(0);
		
	}
	
	public NumberValue getHeight(){
		return height;
	}
    
	
	
	

	
}
