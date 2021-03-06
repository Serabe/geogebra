package geogebra.common.euclidian;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.TreeSet;

import geogebra.common.awt.BasicStroke;
import geogebra.common.awt.BufferedImage;
import geogebra.common.awt.Color;
import geogebra.common.awt.Dimension;
import geogebra.common.awt.Ellipse2DDouble;
import geogebra.common.awt.Font;
import geogebra.common.awt.FontRenderContext;
import geogebra.common.awt.Graphics2D;
import geogebra.common.awt.Line2D;
import geogebra.common.awt.Point;
import geogebra.common.awt.Rectangle;
import geogebra.common.awt.font.TextLayout;
import geogebra.common.euclidian.DrawableList.DrawableIterator;
import geogebra.common.factories.AwtFactory;
import geogebra.common.factories.FormatFactory;
import geogebra.common.kernel.ConstructionDefaults;
import geogebra.common.kernel.Kernel;
import geogebra.common.kernel.Matrix.CoordMatrix;
import geogebra.common.kernel.Matrix.Coords;
import geogebra.common.kernel.algos.AlgoBoxPlot;
import geogebra.common.kernel.algos.AlgoElement;
import geogebra.common.kernel.algos.AlgoElementInterface;
import geogebra.common.kernel.algos.AlgoFunctionAreaSums;
import geogebra.common.kernel.algos.AlgoIntegralFunctions;
import geogebra.common.kernel.algos.AlgoSlope;
import geogebra.common.kernel.arithmetic.FunctionalNVar;
import geogebra.common.kernel.arithmetic.NumberValue;
import geogebra.common.kernel.cas.AlgoIntegralDefinite;
import geogebra.common.kernel.geos.GeoAngle;
import geogebra.common.kernel.geos.GeoBoolean;
import geogebra.common.kernel.geos.GeoButton;
import geogebra.common.kernel.geos.GeoConicPart;
import geogebra.common.kernel.geos.GeoCurveCartesian;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoFunction;
import geogebra.common.kernel.geos.GeoFunctionNVar;
import geogebra.common.kernel.geos.GeoImage;
import geogebra.common.kernel.geos.GeoList;
import geogebra.common.kernel.geos.GeoLocus;
import geogebra.common.kernel.geos.GeoNumeric;
import geogebra.common.kernel.geos.GeoPoint2;
import geogebra.common.kernel.geos.GeoPolyLine;
import geogebra.common.kernel.geos.GeoPolygon;
import geogebra.common.kernel.geos.GeoText;
import geogebra.common.kernel.geos.GeoTextField;
import geogebra.common.kernel.geos.ParametricCurve;
import geogebra.common.kernel.implicit.GeoImplicitPoly;
import geogebra.common.kernel.kernelND.GeoConicND;
import geogebra.common.kernel.kernelND.GeoDirectionND;
import geogebra.common.kernel.kernelND.GeoLineND;
import geogebra.common.kernel.kernelND.GeoPlaneND;
import geogebra.common.kernel.kernelND.GeoPointND;
import geogebra.common.kernel.kernelND.GeoRayND;
import geogebra.common.kernel.kernelND.GeoSegmentND;
import geogebra.common.kernel.kernelND.GeoVectorND;
import geogebra.common.main.AbstractApplication;
import geogebra.common.main.settings.AbstractSettings;
import geogebra.common.main.settings.EuclidianSettings;
import geogebra.common.plugin.EuclidianStyleConstants;
import geogebra.common.plugin.GeoClass;
import geogebra.common.util.MyMath;
import geogebra.common.util.NumberFormatAdapter;
import geogebra.common.util.StringUtil;
import geogebra.common.util.Unicode;

public abstract class AbstractEuclidianView implements EuclidianViewInterfaceCommon {
	
	/** View other than EV1 and EV2 **/
	public static int EVNO_GENERAL = 1001;
	protected int evNo = 1;
	double xZeroOld, yZeroOld;
	protected int mode = EuclidianConstants.MODE_MOVE;
	
	protected static final int MIN_WIDTH = 50;
	protected static final int MIN_HEIGHT = 50;

	protected static final String EXPORT1 = "Export_1"; // Points used to define
														// corners for export
														// (if they exist)
	protected static final String EXPORT2 = "Export_2";

	// pixel per centimeter (at 72dpi)
	protected static final double PRINTER_PIXEL_PER_CM = 72.0 / 2.54;

	public static final double MODE_ZOOM_FACTOR = 1.5;

	public static final double MOUSE_WHEEL_ZOOM_FACTOR = 1.1;

	public static final double SCALE_STANDARD = 50;
	protected static int SCREEN_BORDER = 10;

	// public static final double SCALE_MAX = 10000;
	// public static final double SCALE_MIN = 0.1;
	public static final double XZERO_STANDARD = 215;

	public static final double YZERO_STANDARD = 315;
	// or use volatile image
		// protected int drawMode = DRAW_MODE_BACKGROUND_IMAGE;
		protected BufferedImage bgImage;
		protected geogebra.common.awt.Graphics2D bgGraphics; // g2d of bgImage
		// zoom rectangle colors
		protected static final geogebra.common.awt.Color colZoomRectangle = 
				geogebra.common.factories.AwtFactory.prototype.newColor(200, 200, 230);
		protected static final geogebra.common.awt.Color colZoomRectangleFill = 
				geogebra.common.factories.AwtFactory.prototype.newColor(200, 200,
				230, 50);
		// colors: axes, grid, background
		protected geogebra.common.awt.Color axesColor, gridColor;
		protected Rectangle selectionRectangle;
		protected static geogebra.common.awt.BasicStroke defAxesStroke = geogebra.common.factories.AwtFactory.prototype.newBasicStroke(1.0f,
				geogebra.common.awt.BasicStroke.CAP_BUTT, geogebra.common.awt.BasicStroke.JOIN_MITER);
		protected static geogebra.common.awt.BasicStroke boldAxesStroke = 
				geogebra.common.factories.AwtFactory.prototype.newBasicStroke(2.0f, 
				// changed
																			// from
																			// 1.8f
																			// (same
																			// as
																			// bold
																			// grid)
																			// Michael
																			// Borcherds
																			// 2008-04-12
				BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER);

		// axes and grid stroke
		protected geogebra.common.awt.BasicStroke axesStroke, tickStroke, gridStroke;

	protected Kernel kernel;
	
	public static final Integer[] getLineTypes() {
		Integer[] ret = { new Integer(EuclidianStyleConstants.LINE_TYPE_FULL),
				new Integer(EuclidianStyleConstants.LINE_TYPE_DASHED_LONG),
				new Integer(EuclidianStyleConstants.LINE_TYPE_DASHED_SHORT),
				new Integer(EuclidianStyleConstants.LINE_TYPE_DOTTED),
				new Integer(EuclidianStyleConstants.LINE_TYPE_DASHED_DOTTED) };
		return ret;
	}

	// G.Sturr added 2009-9-21
	public static final Integer[] getPointStyles() {
		Integer[] ret = {
				new Integer(EuclidianStyleConstants.POINT_STYLE_DOT),
				new Integer(EuclidianStyleConstants.POINT_STYLE_CROSS),
				new Integer(EuclidianStyleConstants.POINT_STYLE_CIRCLE),
				new Integer(EuclidianStyleConstants.POINT_STYLE_PLUS),
				new Integer(EuclidianStyleConstants.POINT_STYLE_FILLED_DIAMOND),
				new Integer(EuclidianStyleConstants.POINT_STYLE_EMPTY_DIAMOND),
				new Integer(EuclidianStyleConstants.POINT_STYLE_TRIANGLE_NORTH),
				new Integer(EuclidianStyleConstants.POINT_STYLE_TRIANGLE_SOUTH),
				new Integer(EuclidianStyleConstants.POINT_STYLE_TRIANGLE_EAST),
				new Integer(EuclidianStyleConstants.POINT_STYLE_TRIANGLE_WEST) };
		return ret;
	}

	// end
	private int fontSize;
	private geogebra.common.awt.AffineTransform coordTransform = 
			geogebra.common.factories.AwtFactory.prototype.newAffineTransform();
	protected double[] AxesTickInterval = { 1, 1 }; // for axes =
	protected NumberFormatAdapter[] axesNumberFormat;
	protected boolean[] showAxes = { true, true };

	// distances between grid lines
	protected boolean automaticGridDistance = true;

	protected double[] gridDistances = { 2, 2, Math.PI / 6 };

	protected int gridLineStyle, axesLineType;

	protected boolean gridIsBold = false; // Michael Borcherds 2008-04-11

	protected int tooltipsInThisView = EuclidianStyleConstants.TOOLTIPS_AUTOMATIC;

	// Michael Borcherds 2008-04-28
	public static final int GRID_CARTESIAN = 0;
	public static final int GRID_ISOMETRIC = 1;
	public static final int GRID_POLAR = 2;
	protected int gridType = GRID_CARTESIAN;

	// FONTS
	private Font fontPoint;

	private Font fontLine;

	private Font fontVector;

	private Font fontConic;

	private Font fontCoords;

	private Font fontAxes;

	private Font fontAngle;


	protected NumberFormatAdapter printScaleNF;


	protected boolean showGrid = false;

	protected boolean antiAliasing = true;

	protected boolean showMouseCoords = false;
	protected boolean allowShowMouseCoords = true;

	protected boolean showAxesRatio = false;
	protected boolean highlightAnimationButtons = false;

	// only used for temporary views eg Text Preview, Spreadsheet plots
	protected int pointCapturingMode;
	
	
	protected boolean showAxesCornerCoords = true;//private

	protected boolean[] showAxesNumbers = { true, true };

	protected String[] axesLabels = { null, null };

	protected String[] axesUnitLabels = { null, null };

	protected Previewable previewDrawable;//package private

	protected boolean firstPaint = true;//private

	protected StringBuilder sb = new StringBuilder();

	// object is hit if mouse is within this many pixels
	// (more for points, see DrawPoint)
	private int capturingThreshold = 3;


	protected AbstractApplication application;//private

	protected EuclidianSettings settings;//private final

	// member variables
	protected AbstractEuclidianController euclidianController;

	// ggb3D 2009-02-05
	protected final Hits hits;//private

	public AbstractEuclidianView(AbstractEuclidianController ec,
			EuclidianSettings settings) {
		// Michael Borcherds 2008-03-01
				drawLayers = new DrawableList[EuclidianStyleConstants.MAX_LAYERS + 1];
				for (int k = 0; k <= EuclidianStyleConstants.MAX_LAYERS; k++) {
					drawLayers[k] = new DrawableList();
				}
				axesNumberFormat = new NumberFormatAdapter[2];
				axesNumberFormat[0] = FormatFactory.prototype.getNumberFormat();
				axesNumberFormat[1] = FormatFactory.prototype.getNumberFormat();
				axesNumberFormat[0].setGroupingUsed(false);
				axesNumberFormat[1].setGroupingUsed(false);
				
				
				
		this.euclidianController = ec;
		kernel = ec.getKernel();
		application = kernel.getApplication();
		this.settings = settings;
		// no repaint
		xminObject = new GeoNumeric(kernel.getConstruction());
		xmaxObject = new GeoNumeric(kernel.getConstruction());
		yminObject = new GeoNumeric(kernel.getConstruction());
		ymaxObject = new GeoNumeric(kernel.getConstruction());

		// ggb3D 2009-02-05
		hits = new Hits();
	}
	
	public void setAxesColor(geogebra.common.awt.Color axesColor) {
		if (axesColor != null) {
			this.axesColor = axesColor;
		}
	}

	public void setStandardCoordSystem() {
		setStandardCoordSystem(true);
	}

	protected void setStandardCoordSystem(boolean repaint) {//private
		setCoordSystem(XZERO_STANDARD, YZERO_STANDARD, SCALE_STANDARD,
				SCALE_STANDARD, repaint);
	}

	public void attachView() {
		kernel.notifyAddAll(this);
		kernel.attach(this);
	}

	/**
	 * Returns point capturing mode.
	 */
	public int getPointCapturingMode() {

		if (settings != null) {
			return settings.getPointCapturingMode();
		}
		return pointCapturingMode;
		
	}

	/**
	 * Set capturing of points to the grid.
	 */
	public void setPointCapturing(int mode) {
		if (settings != null) {
			settings.setPointCapturing(mode);
		} else {
			pointCapturingMode = mode;
		}
	}

	public void setCapturingThreshold(int i) {
		capturingThreshold = i;
	}

	public int getCapturingThreshold() {
		return capturingThreshold;
	}

	public int getMode() {
		return mode;
	}

	public void setMode(int mode) {
		if (mode == this.mode) {
			return;
		}
		this.mode = mode;
		initCursor();
		getEuclidianController().clearJustCreatedGeos();
		getEuclidianController().setMode(mode);
		if (clearRectangle(mode)) {
			setSelectionRectangle(null);
		}
		setStyleBarMode(mode);
	}

	public Kernel getKernel() {
		return kernel;
	}

	/**
	 * whether to clear selection rectangle when mode selected
	 */
	final private static boolean clearRectangle(int mode) {
		switch (mode) {
		case EuclidianConstants.MODE_PEN:
			return false;
		case EuclidianConstants.MODE_MIRROR_AT_LINE:
			return false;
		case EuclidianConstants.MODE_MIRROR_AT_POINT:
			return false;
		case EuclidianConstants.MODE_ROTATE_BY_ANGLE:
			return false;
		case EuclidianConstants.MODE_TRANSLATE_BY_VECTOR:
			return false;
		case EuclidianConstants.MODE_DILATE_FROM_POINT:
			return false;
		default:
			return true;
		}
	}

	private NumberValue xminObject, xmaxObject, yminObject, ymaxObject;

	/**
	 * @return the xminObject
	 */
	public GeoNumeric getXminObject() {
		return (GeoNumeric) xminObject;
	}

	public void updateBoundObjects() {
		if (isZoomable()) {
			((GeoNumeric) xminObject).setValue(getXmin());
			((GeoNumeric) xmaxObject).setValue(getXmax());
			((GeoNumeric) yminObject).setValue(getYmin());
			((GeoNumeric) ymaxObject).setValue(getYmax());
		}
	}
	
	private boolean updatingBounds = false;

	protected boolean unitAxesRatio;

	/**
	 * returns true if the axes ratio is 1
	 * 
	 * @return true if the axes ratio is 1
	 */
	public boolean isUnitAxesRatio() {
		return unitAxesRatio || (gridType == GRID_POLAR);
	}

	/**
	 * Set unit axes ratio to 1
	 * 
	 * @param flag
	 *            true to set to 1, false to allow user
	 */
	public void setUnitAxesRatio(boolean flag) {
		unitAxesRatio = flag;
		if (flag) {
			updateBounds();
		}
	}

	public void updateBounds() {
		if (updatingBounds) {
			return;
		}
		updatingBounds = true;
		double xmin2 = xminObject.getDouble();
		double xmax2 = xmaxObject.getDouble();
		double ymin2 = yminObject.getDouble();
		double ymax2 = ymaxObject.getDouble();
		if (isUnitAxesRatio() && (getHeight() > 0) && (getWidth() > 0)) {
			double newWidth = ((ymax2 - ymin2) * getWidth()) / (getHeight() + 0.0);
			double newHeight = ((xmax2 - xmin2) * getHeight()) / (getWidth() + 0.0);

			if ((xmax2 - xmin2) < newWidth) {
				double c = (xmin2 + xmax2) / 2;
				xmin2 = c - (newWidth / 2);
				xmax2 = c + (newWidth / 2);
			} else {
				double c = (ymin2 + ymax2) / 2;
				ymin2 = c - (newHeight / 2);
				ymax2 = c + (newHeight / 2);
			}
		}
		if (((xmax2 - xmin2) > Kernel.MIN_PRECISION)
				&& ((ymax2 - ymin2) > Kernel.MIN_PRECISION)) {
			setRealWorldCoordSystem(xmin2, xmax2, ymin2, ymax2);
		}
		updatingBounds = false;
	}

	
	public boolean isZoomable() {
		if ((xminObject != null)
				&& (!((GeoNumeric) xminObject).isIndependent() || ((GeoNumeric) xminObject)
						.isLabelSet())) {
			return false;
		}
		if ((xmaxObject != null)
				&& (!((GeoNumeric) xmaxObject).isIndependent() || ((GeoNumeric) xmaxObject)
						.isLabelSet())) {
			return false;
		}
		if ((yminObject != null)
				&& (!((GeoNumeric) yminObject).isIndependent() || ((GeoNumeric) yminObject)
						.isLabelSet())) {
			return false;
		}
		if ((ymaxObject != null)
				&& (!((GeoNumeric) ymaxObject).isIndependent() || ((GeoNumeric) ymaxObject)
						.isLabelSet())) {
			return false;
		}
		return true;
	}

	/**
	 * @param xminObjectNew
	 *            the xminObject to set
	 */
	public void setXminObject(NumberValue xminObjectNew) {
		if (xminObject != null) {
			((GeoNumeric) xminObject).removeEVSizeListener(this);
		}
		if (xminObjectNew == null) {
			this.xminObject = new GeoNumeric(kernel.getConstruction());
			updateBoundObjects();
		} else {
			this.xminObject = xminObjectNew;
		}
		setSizeListeners();
	}

	/**
	 * @return the xmaxObject
	 */
	public GeoNumeric getXmaxObject() {
		return (GeoNumeric) xmaxObject;
	}

	/**
	 * @param xmaxObjectNew
	 *            the xmaxObject to set
	 */
	public void setXmaxObject(NumberValue xmaxObjectNew) {
		if (xmaxObject != null) {
			((GeoNumeric) xmaxObject).removeEVSizeListener(this);
		}
		if (xmaxObjectNew == null) {
			this.xmaxObject = new GeoNumeric(kernel.getConstruction());
			updateBoundObjects();
		} else {
			this.xmaxObject = xmaxObjectNew;
		}
		setSizeListeners();
	}

	/**
	 * @return the yminObject
	 */
	public GeoNumeric getYminObject() {
		return (GeoNumeric) yminObject;
	}

	/**
	 * @param yminObjectNew
	 *            the yminObject to set
	 */
	public void setYminObject(NumberValue yminObjectNew) {
		if (yminObject != null) {
			((GeoNumeric) yminObject).removeEVSizeListener(this);
		}
		if (yminObjectNew == null) {
			this.yminObject = new GeoNumeric(kernel.getConstruction());
			updateBoundObjects();
		} else {
			this.yminObject = yminObjectNew;
		}
		setSizeListeners();
	}

	private void setSizeListeners() {
		((GeoNumeric) xminObject).addEVSizeListener(this);
		((GeoNumeric) yminObject).addEVSizeListener(this);
		((GeoNumeric) xmaxObject).addEVSizeListener(this);
		((GeoNumeric) ymaxObject).addEVSizeListener(this);
	}
	
	/**
	 * convert real world coordinate x to screen coordinate x
	 * 
	 * @param xRW
	 * @return screen equivalent of real world x-coord
	 */
	final public int toScreenCoordX(double xRW) {
		return (int) Math.round(getxZero() + (xRW * getXscale()));
	}

	/**
	 * convert real world coordinate y to screen coordinate y
	 * 
	 * @param yRW
	 * @return screen equivalent of real world y-coord
	 */
	final public int toScreenCoordY(double yRW) {
		return (int) Math.round(getyZero() - (yRW * getYscale()));
	}

	/**
	 * convert real world coordinate x to screen coordinate x
	 * 
	 * @param xRW
	 * @return screen equivalent of real world x-coord as double
	 */
	final public double toScreenCoordXd(double xRW) {
		return getxZero() + (xRW * getXscale());
	}

	/**
	 * convert real world coordinate y to screen coordinate y
	 * 
	 * @param yRW
	 * @return screen equivalent of real world y-coord
	 */
	final public double toScreenCoordYd(double yRW) {
		return getyZero() - (yRW * getYscale());
	}

	/**
	 * convert real world coordinate x to screen coordinate x. If the value is
	 * outside the screen it is clipped to one pixel outside.
	 * 
	 * @param xRW
	 * @return real world coordinate x to screen coordinate x clipped to screen
	 */
	final public int toClippedScreenCoordX(double xRW) {
		if (xRW > getXmax()) {
			return getWidth() + 1;
		} else if (xRW < getXmin()) {
			return -1;
		} else {
			return toScreenCoordX(xRW);
		}
	}

	/** remembers the origins values (xzero, ...) */
	public void rememberOrigins() {
		xZeroOld = getxZero();
		yZeroOld = getyZero();
	}
	
	/**
	 * convert real world coordinate y to screen coordinate y. If the value is
	 * outside the screen it is clipped to one pixel outside.
	 * 
	 * @param yRW
	 * @return real world coordinate y to screen coordinate x clipped to screen
	 */
	final public int toClippedScreenCoordY(double yRW) {
		if (yRW > getYmax()) {
			return -1;
		} else if (yRW < getYmin()) {
			return getHeight() + 1;
		} else {
			return toScreenCoordY(yRW);
		}
	}

	/**
	 * Converts real world coordinates to screen coordinates. Note that
	 * MAX_SCREEN_COORD is used to avoid huge coordinates.
	 * 
	 * @param inOut
	 *            input and output array with x and y coords
	 * @return if resulting coords are on screen
	 */
	final public boolean toScreenCoords(double[] inOut) {
		// convert to screen coords
		inOut[0] = getxZero() + (inOut[0] * getXscale());
		inOut[1] = getyZero() - (inOut[1] * getYscale());

		// check if (x, y) is on screen
		boolean onScreen = true;

		// note that java drawing has problems for huge coord values
		// so we use FAR_OFF_SCREEN for clipping
		if (Double.isNaN(inOut[0]) || Double.isInfinite(inOut[0])) {
			inOut[0] = Double.NaN;
			onScreen = false;
		} else if (inOut[0] < 0) { // x left of screen
			// inOut[0] = Math.max(inOut[0], -MAX_SCREEN_COORD);
			onScreen = false;
		} else if (inOut[0] > getWidth()) { // x right of screen
			// inOut[0] = Math.min(inOut[0], width + MAX_SCREEN_COORD);
			onScreen = false;
		}

		// y undefined
		if (Double.isNaN(inOut[1]) || Double.isInfinite(inOut[1])) {
			inOut[1] = Double.NaN;
			onScreen = false;
		} else if (inOut[1] < 0) { // y above screen
			// inOut[1] = Math.max(inOut[1], -MAX_SCREEN_COORD);
			onScreen = false;
		} else if (inOut[1] > getHeight()) { // y below screen
			// inOut[1] = Math.min(inOut[1], height + MAX_SCREEN_COORD);
			onScreen = false;
		}

		return onScreen;
	}

	/**
	 * Checks if (screen) coords are on screen.
	 * 
	 * @param coords
	 * @return true if coords are on screen
	 */
	final public boolean isOnScreen(double[] coords) {
		return (coords[0] >= 0) && (coords[0] <= getWidth()) && (coords[1] >= 0)
				&& (coords[1] <= getHeight());
	}

	// private static final double MAX_SCREEN_COORD = Float.MAX_VALUE; //10000;

	// /**
	// * Converts real world coordinates to screen coordinates. If a coord value
	// * is outside the screen it is clipped to a rectangle with border
	// * PIXEL_OFFSET around the screen.
	// *
	// * @param inOut:
	// * input and output array with x and y coords
	// * @return true iff resulting coords are on screen, note: Double.NaN is
	// NOT
	// * checked
	// */
	// final public boolean toClippedScreenCoords(double[] inOut, int
	// PIXEL_OFFSET) {
	// inOut[0] = xZero + inOut[0] * xscale;
	// inOut[1] = yZero - inOut[1] * yscale;
	//
	// boolean onScreen = true;
	//
	// // x-coord on screen?
	// if (inOut[0] < 0) {
	// inOut[0] = Math.max(inOut[0], -PIXEL_OFFSET);
	// onScreen = false;
	// } else if (inOut[0] > width) {
	// inOut[0] = Math.min(inOut[0], width + PIXEL_OFFSET);
	// onScreen = false;
	// }
	//
	// // y-coord on screen?
	// if (inOut[1] < 0) {
	// inOut[1] = Math.max(inOut[1], -PIXEL_OFFSET);
	// onScreen = false;
	// } else if (inOut[1] > height) {
	// inOut[1] = Math.min(inOut[1], height + PIXEL_OFFSET);
	// onScreen = false;
	// }
	//
	// return onScreen;
	// }

	/**
	 * convert screen coordinate x to real world coordinate x
	 * 
	 * @param x
	 * @return real world equivalent of screen x-coord
	 */
	final public double toRealWorldCoordX(double x) {
		return (x - getxZero()) * getInvXscale();
	}

	/**
	 * convert screen coordinate y to real world coordinate y
	 * 
	 * @param y
	 * @return real world equivalent of screen y-coord
	 */
	final public double toRealWorldCoordY(double y) {
		return (getyZero() - y) * getInvYscale();
	}

	/**
	 * Sets real world coord system, where zero point has screen coords (xZero,
	 * yZero) and one unit is xscale pixels wide on the x-Axis and yscale pixels
	 * heigh on the y-Axis.
	 */
	final public void setCoordSystem(double xZero, double yZero, double xscale,
			double yscale) {
		setCoordSystem(xZero, yZero, xscale, yscale, true);
	}

	/** Sets coord system from mouse move */
	public void setCoordSystemFromMouseMove(int dx, int dy, int mode) {
		setCoordSystem(xZeroOld + dx, yZeroOld + dy, getXscale(), getYscale());
	}

	/**
	 * Sets real world coord system using min and max values for both axes in
	 * real world values.
	 */
	final public void setRealWorldCoordSystem(double xmin, double xmax,
			double ymin, double ymax) {
		double calcXscale = getWidth() / (xmax - xmin);
		double calcYscale = getHeight() / (ymax - ymin);
		double calcXzero = -calcXscale * xmin;
		double calcYzero = calcYscale * ymax;

		setCoordSystem(calcXzero, calcYzero, calcXscale, calcYscale);
	}

	
	public void setCoordSystem(double xZero, double yZero, double xscale,
			double yscale, boolean repaint) {
		if (Double.isNaN(xscale)
				|| (xscale < Kernel.MAX_DOUBLE_PRECISION)
				|| (xscale > Kernel.INV_MAX_DOUBLE_PRECISION)) {
			return;
		}
		if (Double.isNaN(yscale)
				|| (yscale < Kernel.MAX_DOUBLE_PRECISION)
				|| (yscale > Kernel.INV_MAX_DOUBLE_PRECISION)) {
			return;
		}

		this.setxZero(xZero);
		this.setyZero(yZero);
		this.setXscale(xscale);
		this.setYscale(yscale);
		setScaleRatio(yscale / xscale);

		// set transform for my coord system:
		// ( xscale 0 xZero )
		// ( 0 -yscale yZero )
		// ( 0 0 1 )
		if(getCoordTransform()!=null)
			getCoordTransform().setTransform(xscale, 0.0d, 0.0d, -yscale, xZero, yZero);

		// real world values
		setRealWorldBounds();

		// if (drawMode == DRAW_MODE_BACKGROUND_IMAGE)
		if (repaint) {
			updateBackgroundImage();
			updateAllDrawables(repaint);

			// needed so that eg Corner[2,1] updates properly on zoom / pan
			if (getApplication().hasEuclidianView2()) {
				kernel.notifyRepaint();
				// app.updateStatusLabelAxesRatio();
			}
		}
	}


	/**
	 * @return the ymaxObject
	 */
	public GeoNumeric getYmaxObject() {
		return (GeoNumeric) ymaxObject;
	}

	private double xmin; // ratio yscale / xscale

	private double xmax;

	private double ymin;

	private double ymax;

	private double invXscale;

	private double invYscale;

	private double xZero;

	private double yZero;

	private double xscale;

	private double yscale;

	private double scaleRatio = 1.0;
	
	protected double printingScale;

	// Map (geo, drawable) for GeoElements and Drawables
	protected HashMap<GeoElement, DrawableND> DrawableMap = new HashMap<GeoElement, DrawableND>(
			500);

	protected ArrayList<GeoPointND> stickyPointList = new ArrayList<GeoPointND>();

	protected DrawableList allDrawableList = new DrawableList();

	public DrawableList drawLayers[];

	// on add: change resetLists()

	protected DrawableList bgImageList = new DrawableList();
	
	protected boolean[] piAxisUnit = { false, false };

	protected int[] axesTickStyles = {
			EuclidianStyleConstants.AXES_TICK_STYLE_MAJOR,
			EuclidianStyleConstants.AXES_TICK_STYLE_MAJOR };

	// for axes labeling with numbers
	protected boolean[] automaticAxesNumberingDistances = { true, true };

	protected double[] axesNumberingDistances = { 2, 2 };


	public void setAntialiasing(boolean flag) {
		if (flag == antiAliasing) {
			return;
		}
		antiAliasing = flag;
		repaint();
	}

	public boolean getAntialiasing() {
		return antiAliasing;
	}


	/**
	 * This is only needed for second or above euclidian views
	 * 
	 * @param evNo
	 *            euclidian view number
	 */
	public void setEuclidianViewNo(int evNo) {
		if (evNo >= 2) {
			this.evNo = evNo;
		}
	}

	public int getEuclidianViewNo() {
		return evNo;
	}


	/**
	 * @param ymaxObjectNew
	 *            the ymaxObject to set
	 */
	public void setYmaxObject(NumberValue ymaxObjectNew) {
		if (ymaxObject != null) {
			((GeoNumeric) ymaxObject).removeEVSizeListener(this);
		}
		if (ymaxObjectNew == null) {
			this.ymaxObject = new GeoNumeric(kernel.getConstruction());
			updateBoundObjects();
		} else {
			this.ymaxObject = ymaxObjectNew;
		}
		setSizeListeners();
	}

	protected void setXscale(double xscale) {
		this.xscale = xscale;
		this.invXscale = 1/xscale;
	}

	protected void setYscale(double yscale) {
		this.yscale = yscale;
		this.invYscale = 1/yscale;
	}

	protected void setFontSize(int fontSize) {
		this.fontSize = fontSize;
	}

	public double getxZero() {
		return xZero;
	}

	protected void setxZero(double xZero) {
		this.xZero = xZero;
	}

	public double getyZero() {
		return yZero;
	}

	/**
	 * Returns x coordinate of axes origin.
	 */
	public double getXZero() {
		return getxZero();
	}

	/**
	 * Returns y coordinate of axes origin.
	 */
	public double getYZero() {
		return getyZero();
	}

	protected String getXYscaleRatioString() {
		StringBuilder sb = new StringBuilder();
		sb.append("x : y = ");
		if (getXscale() >= getYscale()) {
			sb.append("1 : ");
			sb.append(printScaleNF.format(getXscale() / getYscale()));
		} else {
			sb.append(printScaleNF.format(getYscale() / getXscale()));
			sb.append(" : 1");
		}
		sb.append(' ');
		return sb.toString();
	}

	/**
	 * Returns xscale of this view. The scale is the number of pixels in screen
	 * space that represent one unit in user space.
	 */
	public double getXscale() {
		return xscale;
	}

	/**
	 * Returns the yscale of this view. The scale is the number of pixels in
	 * screen space that represent one unit in user space.
	 */
	public double getYscale() {
		return yscale;
	}

	public double getInvXscale() {
		return invXscale;
	}

	public double getInvYscale() {
		return invYscale;
	}

	public int getViewWidth() {
		return getWidth();
	}

	public int getViewHeight() {
		return getHeight();
	}

	/**
	 * Returns the ratio yscale / xscale of this view. The scale is the number
	 * of pixels in screen space that represent one unit in user space.
	 * 
	 * @return yscale / xscale ratio
	 */
	public double getScaleRatio() {
		return getYscale() / getXscale();
	}
	
	/**
	 * @return Returns the xmax.
	 */
	public double getXmax() {
		return xmax;
	}

	/**
	 * @return Returns the xmin.
	 */
	public double getXmin() {
		return xmin;
	}

	/**
	 * @return Returns the ymax.
	 */
	public double getYmax() {
		return ymax;
	}

	/**
	 * @return Returns the ymin.
	 */
	public double getYmin() {
		return ymin;
	}

	/**
	 * When function (or parabola) is transformed to curve, we need some good
	 * estimate for which part of curve should be ploted
	 * 
	 * @return lower bound for function -> curve transform
	 */
	public double getXmaxForFunctions() {
		return (((2 * getXmax()) - getXmin()) + getYmax()) - getYmin();
	}

	/**
	 * @see #getXmaxForFunctions()
	 * @return upper bound for function -> curve transform
	 */
	public double getXminForFunctions() {
		return (((2 * getXmin()) - getXmax()) + getYmin()) - getYmax();
	}

	/**
	 * Returns grid type.
	 */
	final public int getGridType() {
		return gridType;
	}

	/**
	 * Set grid type.
	 */
	public void setGridType(int type) {
		gridType = type;
		if (type == GRID_POLAR) {
			updateBounds();
		}
	}


	protected void setyZero(double yZero) {
		this.yZero = yZero;
	}

	void setScaleRatio(double scaleRatio) {
		this.scaleRatio = scaleRatio;
	}

	public void setXmin(double xmin) {
		this.xmin = xmin;
	}

	public void setXmax(double xmax) {
		this.xmax = xmax;
	}

	public void setYmin(double ymin) {
		this.ymin = ymin;
	}

	public void setYmax(double ymax) {
		this.ymax = ymax;
	}

	final protected void setRealWorldBounds() {
		setXmin(-getxZero() * getInvXscale());
		setXmax((getWidth() - getxZero()) * getInvXscale());
		setYmax(getyZero() * getInvYscale());
		setYmin((getyZero() - getHeight()) * getInvYscale());
		updateBoundObjects();
		updateBounds();
		setAxesIntervals(getXscale(), 0);
		setAxesIntervals(getYscale(), 1);
		calcPrintingScale();

		// tell kernel
		if (evNo != EVNO_GENERAL) {
			kernel.setEuclidianViewBounds(evNo, getXmin(), getXmax(), getYmin(), getYmax(), getXscale(),
					getYscale());
		}

	}

	/**
	 * Zooms around fixed point (center of screen)
	 * 
	 * @param zoomFactor
	 */
	public final void zoomAroundCenter(double zoomFactor) {
		if (!isZoomable()) {
			return;
			// keep xmin, xmax, ymin, ymax constant, adjust everything else
		}

		setXscale(getXscale() * zoomFactor);
		setYscale(getYscale() * zoomFactor);

		setScaleRatio(getYscale() / getXscale());
		
		setxZero(-getXmin() * getXscale());
		setWidth((int) ((getXmax() * getXscale()) + getxZero()));
		setyZero(getYmax() * getYscale());
		setHeight((int) (getyZero() - (getYmin() * getYscale())));

		setAxesIntervals(getXscale(), 0);
		setAxesIntervals(getYscale(), 1);
		calcPrintingScale();

		// tell kernel
		if (evNo != EVNO_GENERAL) {
			kernel.setEuclidianViewBounds(evNo, getXmin(), getXmax(), getYmin(), getYmax(), getXscale(),
					getYscale());
		}

		getCoordTransform().setTransform(getXscale(), 0.0d, 0.0d, -getYscale(), getxZero(), getyZero());

		updateBackgroundImage();
		updateAllDrawables(true);

	}
	
	final public void updateAllDrawables(boolean repaint) {
		allDrawableList.updateAll();
		if (repaint) {
			repaint();
		}
	}


	protected void calcPrintingScale() {
		double unitPerCM = PRINTER_PIXEL_PER_CM / getXscale();
		int exp = (int) Math.round(Math.log(unitPerCM) / Math.log(10));
		printingScale = Math.pow(10, -exp);
	}

	// axis: 0 for x-axis, 1 for y-axis
	protected void setAxesIntervals(double scale, int axis) {
		double maxPix = 100; // only one tick is allowed per maxPix pixels
		double units = maxPix / scale;
		int exp = (int) Math.floor(Math.log(units) / Math.log(10));
		int maxFractionDigtis = Math.max(-exp, kernel.getPrintDecimals());

		if (automaticAxesNumberingDistances[axis]) {
			// force same unit if scales are same, see #1082
			if ((axis == 1) && automaticAxesNumberingDistances[0]
					&& Kernel.isEqual(getXscale(), getYscale())) {
				axesNumberingDistances[1] = axesNumberingDistances[0];
			} else if (piAxisUnit[axis]) {
				axesNumberingDistances[axis] = Math.PI;
			} else {
				double pot = Math.pow(10, exp);
				double n = units / pot;

				if (n > 5) {
					axesNumberingDistances[axis] = 5 * pot;
				} else if (n > 2) {
					axesNumberingDistances[axis] = 2 * pot;
				} else {
					axesNumberingDistances[axis] = pot;
				}
			}
		}
		AxesTickInterval[axis] = axesNumberingDistances[axis] / 2.0;

		// set axes number format
		
			NumberFormatAdapter df = axesNumberFormat[axis];

			// display large and small numbers in scienctific notation
			if ((axesNumberingDistances[axis] < 10E-6)
					|| (axesNumberingDistances[axis] > 10E6)) {
				df.applyPattern("0.##E0");
				// avoid 4.00000000000004E-11 due to rounding error when
				// computing
				// tick mark numbers
				maxFractionDigtis = Math.min(14, maxFractionDigtis);
			} else {
				df.applyPattern("###0.##");
			}
		
		axesNumberFormat[axis].setMaximumFractionDigits(maxFractionDigtis);

		if (automaticGridDistance) {
			gridDistances[axis] = axesNumberingDistances[axis]
					* EuclidianStyleConstants.automaticGridDistanceFactor;
		}
	}

	public int getFontSize() {
		return fontSize;
	}
	
	/**
	 * renames an element
	 */
	public void rename(GeoElement geo) {
		Object d = DrawableMap.get(geo);
		if (d != null) {
			((Drawable) d).update();
			repaint();
		}
	}

	public void update(GeoElement geo) {
		Object d = DrawableMap.get(geo);
		if (d != null) {
			((Drawable) d).update();
		}
	}
	
	/**
	 * adds a GeoElement to this view
	 */
	public void add(GeoElement geo) {
		// Application.printStacktrace(""+geo.isVisibleInView(this));

		// G.Sturr 2010-6-30
		// filter out any geo not marked for this view
		if (!isVisibleInThisView(geo)) {
			return;
			// END G.Sturr
		}

		// check if there is already a drawable for geo
		DrawableND d = getDrawable(geo);
		
		if (d != null) {
			return;
		}

		d = createDrawable(geo);
		if (d != null) {
			addToDrawableLists((Drawable) d);
			repaint();
		}

	}
	
	/**
	 * removes a GeoElement from this view
	 */
	public void remove(GeoElement geo) {
		Drawable d = (Drawable) DrawableMap.get(geo);
		int layer = geo.getLayer();
		if(d==null)
			return;
		if (d instanceof RemoveNeeded) {
			drawLayers[layer].remove(d);
			((RemoveNeeded)d).remove();
		}
		else{
			drawLayers[layer].remove(d);
		}
		allDrawableList.remove(d);

		DrawableMap.remove(geo);
		if (geo.isGeoPoint()) {
			stickyPointList.remove(geo);
		}
		repaint();
		
	}

	/** get the hits recorded */
	public Hits getHits() {
		return hits;
	}

	/**
	 * sets the hits of GeoElements whose visual representation is at screen
	 * coords (x,y). order: points, vectors, lines, conics
	 */
	public void setHits(geogebra.common.awt.Point p) {

		hits.init();

		DrawableIterator it = allDrawableList.getIterator();
		while (it.hasNext()) {
			Drawable d = it.next();
			if (d.hit(p.x, p.y) || d.hitLabel(p.x, p.y)) {
				GeoElement geo = d.getGeoElement();
				if (geo.isEuclidianVisible()) {
					hits.add(geo);
				}
			}
		}

		// look for axis
		if (hits.getImageCount() == 0) {
			if (showAxes[0] && (Math.abs(getyZero() - p.y) < 3)) {
				hits.add(kernel.getXAxis());
			}
			if (showAxes[1] && (Math.abs(getxZero() - p.x) < 3)) {
				hits.add(kernel.getYAxis());
			}
		}

		// keep geoelements only on the top layer
		int maxlayer = 0;
		for (int i = 0; i < hits.size(); ++i) {
			GeoElement geo = hits.get(i);
			if (maxlayer < geo.getLayer()) {
				maxlayer = geo.getLayer();
			}
		}
		for (int i = hits.size() - 1; i >= 0; i--) {
			GeoElement geo = hits.get(i);
			if (geo.getLayer() < maxlayer) {
				hits.remove(i);
			}
		}

		// remove all lists and images if there are other objects too
		if ((hits.size() - (hits.getListCount() + hits.getImageCount())) > 0) {
			for (int i = hits.size() - 1; i >= 0; i--) {
				GeoElement geo = hits.get(i);
				if (geo.isGeoList() || geo.isGeoImage()) {
					hits.remove(i);
				}
			}
		}

	}

	/**
	 * returns GeoElement whose label is at screen coords (x,y).
	 */
	public GeoElement getLabelHit(geogebra.common.awt.Point p) {
		if (!getApplication().isLabelDragsEnabled()) {
			return null;
		}
		DrawableIterator it = allDrawableList.getIterator();
		while (it.hasNext()) {
			Drawable d = it.next();
			if (d.hitLabel(p.x, p.y)) {
				GeoElement geo = d.getGeoElement();
				if (geo.isEuclidianVisible()) {
					return geo;
				}
			}
		}
		return null;
	}

	/**
	 * Returns array of GeoElements whose visual representation is at screen
	 * coords (x,y). order: points, vectors, lines, conics
	 * 
	 * @param p
	 * @return array of GeoElements drawn at (x,y)
	 */
	final public ArrayList<GeoElement> getHits(Point p) {
		return getHits(p, false);
	}

	/**
	 * Returns hits that are suitable for new point mode. A polygon is only kept
	 * if one of its sides is also in hits.
	 * 
	 * @param hits
	 * @return list of hits suitable for new point
	 */
	final public static ArrayList<GeoElement> getHitsForNewPointMode(
			ArrayList<GeoElement> hits) {
		if (hits == null) {
			return null;
		}

		Iterator<GeoElement> it = hits.iterator();
		while (it.hasNext()) {
			GeoElement geo = it.next();
			if (geo.isGeoPolygon()) {
				boolean sidePresent = false;
				GeoSegmentND[] sides = ((GeoPolygon) geo).getSegments();
				for (int k = 0; k < sides.length; k++) {
					if (hits.contains(sides[k])) {
						sidePresent = true;
						break;
					}
				}

				if (!sidePresent) {
					it.remove();
				}
			}
		}

		return hits;
	}

	final public ArrayList<GeoElement> getPointVectorNumericHits(Point p) {
		foundHits.clear();

		DrawableIterator it = allDrawableList.getIterator();
		while (it.hasNext()) {
			Drawable d = it.next();
			if (d.hit(p.x, p.y) || d.hitLabel(p.x, p.y)) {
				GeoElement geo = d.getGeoElement();
				if (geo.isEuclidianVisible()) {
					if (
					// geo.isGeoNumeric() ||
					geo.isGeoVector() || geo.isGeoPoint()) {
						foundHits.add(geo);
					}
				}
			}
		}

		return foundHits;
	}

	/**
	 * returns array of GeoElements whose visual representation is at screen
	 * coords (x,y). order: points, vectors, lines, conics
	 * 
	 * @param p
	 * @param includePolygons
	 * @return array of GeoElements drawn at (x,y) ordered by type
	 */
	final public ArrayList<GeoElement> getHits(Point p, boolean includePolygons) {
		foundHits.clear();

		// count lists, images and Polygons
		int listCount = 0;
		int polyCount = 0;
		int imageCount = 0;

		// get anything but a polygon
		DrawableIterator it = allDrawableList.getIterator();
		while (it.hasNext()) {
			Drawable d = it.next();
			if (d.hit(p.x, p.y) || d.hitLabel(p.x, p.y)) {
				GeoElement geo = d.getGeoElement();

				if (geo.isEuclidianVisible()) {
					if (geo.isGeoList()) {
						listCount++;
					} else if (geo.isGeoImage()) {
						imageCount++;
					} else if (geo.isGeoPolygon()) {
						polyCount++;
					}
					foundHits.add(geo);
				}
			}
		}

		// look for axes
		if (foundHits.size() == 0) {
			if (showAxes[0] && (Math.abs(getyZero() - p.y) < 3)) {
				foundHits.add(kernel.getXAxis());
			}
			if (showAxes[1] && (Math.abs(getxZero() - p.x) < 3)) {
				foundHits.add(kernel.getYAxis());
			}
		}

		int size = foundHits.size();
		if (size == 0) {
			return null;
		}

		// remove all lists, images and polygons if there are other objects too
		if ((size - (listCount + imageCount + polyCount)) > 0) {
			for (int i = 0; i < foundHits.size(); ++i) {
				GeoElement geo = foundHits.get(i);
				if (geo.isGeoList() || geo.isGeoImage()
						|| (!includePolygons && geo.isGeoPolygon())) {
					foundHits.remove(i);
				}
			}
		}

		return foundHits;
	}

	protected ArrayList<GeoElement> foundHits = new ArrayList<GeoElement>();

	/**
	 * Stores all GeoElements of type GeoPoint, GeoVector, GeoNumeric to result
	 * list.
	 * 
	 * @param hits
	 * @param result
	 * @return list of points, vectors and numbers
	 * 
	 */
	final protected static ArrayList<GeoElement> getRecordableHits(
			ArrayList<GeoElement> hits, ArrayList<GeoElement> result) {
		if (hits == null) {
			return null;
		}

		result.clear();
		for (int i = 0; i < hits.size(); ++i) {
			GeoElement hit = hits.get(i);
			boolean success = (hit.isGeoPoint() || hit.isGeoVector() || hit
					.isGeoNumeric());
			if (success) {
				result.add(hits.get(i));
			}
		}
		return result.size() == 0 ? null : result;
	}

	/**
	 * returns array of independent GeoElements whose visual representation is
	 * at streen coords (x,y). order: points, vectors, lines, conics
	 * 
	 * @param p
	 * @return array of independent GeoElements at given coords
	 */
	final public ArrayList<GeoElement> getMoveableHits(Point p) {
		return getMoveableHits(getHits(p));
	}



	/**
	 * returns array of changeable GeoElements out of hits
	 * 
	 * @param hits
	 *            hit elements
	 * @return array of changeable GeoElements out of hits
	 */
	final public ArrayList<GeoElement> getMoveableHits(
			ArrayList<GeoElement> hits) {
		return getMoveables(hits, TEST_MOVEABLE, null);
	}

	/**
	 * returns array of changeable GeoElements out of hits that implement
	 * PointRotateable
	 * 
	 * @param hits
	 * @param rotCenter
	 * @return array of changeable GeoElements out of hits that implement
	 */
	final public ArrayList<GeoElement> getPointRotateableHits(
			ArrayList<GeoElement> hits, GeoPoint2 rotCenter) {
		return getMoveables(hits, TEST_ROTATEMOVEABLE, rotCenter);
	}


	protected final int TEST_MOVEABLE = 1;

	protected final int TEST_ROTATEMOVEABLE = 2;

	protected ArrayList<GeoElement> getMoveables(ArrayList<GeoElement> hits,
			int test, GeoPoint2 rotCenter) {
		if (hits == null) {
			return null;
		}

		GeoElement geo;
		moveableList.clear();
		for (int i = 0; i < hits.size(); ++i) {
			geo = hits.get(i);
			switch (test) {
			case TEST_MOVEABLE:
				// moveable object
				if (geo.isMoveable(this)) {
					moveableList.add(geo);
				}
				// point with changeable parent coords
				else if (geo.isGeoPoint()) {
					GeoPoint2 point = (GeoPoint2) geo;
					if (point.hasChangeableCoordParentNumbers()) {
						moveableList.add(point);
					}
				}
				// not a point, but has moveable input points
				else if (geo.hasMoveableInputPoints(this)) {
					moveableList.add(geo);
				}
				break;

			case TEST_ROTATEMOVEABLE:
				// check for circular definition
				if (geo.isRotateMoveable()) {
					if ((rotCenter == null) || !geo.isParentOf(rotCenter)) {
						moveableList.add(geo);
					}
				}

				break;
			}
		}
		if (moveableList.size() == 0) {
			return null;
		} else {
			return moveableList;
		}
	}

	protected ArrayList<GeoElement> moveableList = new ArrayList<GeoElement>();


	protected ArrayList<GeoElement> topHitsList = new ArrayList<GeoElement>();

	final public static boolean containsGeoPoint(ArrayList<GeoElement> hits) {
		if (hits == null) {
			return false;
		}

		for (int i = 0; i < hits.size(); i++) {
			if (hits.get(i).isGeoPoint()) {
				return true;
			}
		}
		return false;
	}
	
	/**
	 * Returns the drawable for the given GeoElement.
	 * 
	 * @param geo
	 * @return drawable for the given GeoElement.
	 */
	final DrawableND getDrawable(GeoElement geo) {
		return DrawableMap.get(geo);
	}

	public DrawableND getDrawableND(GeoElement geo) {
		return getDrawable(geo);
	}

	/**
	 * adds a GeoElement to this view
	 * 
	 * @param d
	 *            drawable to be added
	 */
	protected void addToDrawableLists(Drawable d) {
		if (d == null) {
			return;
		}

		GeoElement geo = d.getGeoElement();
		int layer = geo.getLayer();

		switch (geo.getGeoClassType()) {

		case ANGLE:
			if (geo.isIndependent()) {
				drawLayers[layer].add(d);
			} else {
				if (geo.isDrawable()) {
					drawLayers[layer].add(d);
				} else {
					d = null;
				}
			}
			break;

		case IMAGE:
			if (!bgImageList.contains(d)) {
				drawLayers[layer].add(d);
			}
			break;

		default:
			drawLayers[layer].add(d);
			break;

		}

		if (d != null) {
			allDrawableList.add(d);
		}
	}


	public boolean isVisibleInThisView(GeoElement geo) {
		return geo.isVisibleInView(this.getViewID());
	}

	final public DrawableND createDrawableND(GeoElement geo) {
		return createDrawable(geo);
	}
	/**
	 * adds a GeoElement to this view
	 * 
	 * @param geo
	 *            GeoElement to be added
	 * @return drawable for given GeoElement
	 */
	protected DrawableND createDrawable(GeoElement geo) {
		DrawableND d = newDrawable(geo);
		if (d != null) {
			DrawableMap.put(geo, d);
			if (geo.isGeoPoint()) {
				stickyPointList.add((GeoPointND) geo);
			}
		}

		return d;
	}

	public void reset() {
		resetMode();
		updateBackgroundImage();
	}
	
	/**
	 * clears all selections and highlighting
	 */
	public void resetMode() {
		setMode(mode);
	}
	
	public void repaintView() {
		repaint();
	}

	public void repaintEuclidianView() {
		repaint();
	}


	public void updateVisualStyle(GeoElement geo) {
		update(geo);
	}

	final public DrawableND getDrawableFor(GeoElement geo) {
		return DrawableMap.get(geo);
	}

	final public void updateAuxiliaryObject(GeoElement geo) {
		// repaint();
	}

	
	final protected void updateDrawableFontSize() {
		allDrawableList.updateFontSizeAll();
		repaint();
	}

	public geogebra.common.awt.Font getFontPoint() {
		return fontPoint;
	}

	public void setFontPoint(Font fontPoint) {
		this.fontPoint = fontPoint;
	}

	public geogebra.common.awt.Font getFontLine() {
		return fontLine;
	}

	public void setFontLine(Font fontLine) {
		this.fontLine = fontLine;
	}

	public Font getFontVector() {
		return fontVector;
	}

	public void setFontVector(Font fontVector) {
		this.fontVector = fontVector;
	}

	public Font getFontConic() {
		return fontConic;
	}

	public void setFontConic(Font fontConic) {
		this.fontConic = fontConic;
	}

	public Font getFontCoords() {
		return fontCoords;
	}

	public void setFontCoords(Font fontCoords) {
		this.fontCoords = fontCoords;
	}

	public Font getFontAxes() {
		return fontAxes;
	}

	public void setFontAxes(Font fontAxes) {
		this.fontAxes = fontAxes;
	}

	public geogebra.common.awt.Font getFontAngle() {
		return fontAngle;
	}

	public void setFontAngle(Font fontAngle) {
		this.fontAngle = fontAngle;
	}

	public ArrayList<GeoPointND> getStickyPointList() {
		return stickyPointList;
	}
	
	/**
	 * Sets the global size for checkboxes. Michael Borcherds 2008-05-12
	 * 
	 * @param size
	 *            13 or 26
	 */
	public void setBooleanSize(int size) {

		// only 13 and 26 currently allowed
		getApplication().booleanSize = (size == 13) ? 13 : 26;

		updateAllDrawables(true);
	}

	final public int getBooleanSize() {
		return getApplication().booleanSize;
	}

	/**
	 * Sets the global style for point drawing.
	 * 
	 * @param style
	 */
	public void setPointStyle(int style) {
		if ((style > 0) && (style <= EuclidianStyleConstants.MAX_POINT_STYLE)) {
			getApplication().pointStyle = style;
		} else {
			getApplication().pointStyle = EuclidianStyleConstants.POINT_STYLE_DOT;
		}

		updateAllDrawables(true);
	}

	public int getPointStyle() {
		return getApplication().pointStyle;
	}

	public void setAllowToolTips(int setto) {
		tooltipsInThisView = setto;
	}

	final public int getAllowToolTips() {
		return tooltipsInThisView;
	}
	
	// /////////////////////////////////////////
		// FOR EUCLIDIANVIEWFORPLANE
		// /////////////////////////////////////////

	/**
	 * tranform in view coords
	 * 
	 * @param coords
	 * @return the same coords for classic 2d view
	 */
	public Coords getCoordsForView(Coords coords) {
		return coords;
	}

	/**
	 * return null if classic 2D view
	 * 
	 * @return matrix representation of the plane shown by this view
	 */
	public CoordMatrix getMatrix() {
		return null;
	}

	/**
	 * 
	 * @param conic
	 * @param M
	 * @param ev
	 * @return affine transform of the conic for this view
	 */
	public geogebra.common.awt.AffineTransform getTransform(GeoConicND conic, Coords M, Coords[] ev) {
		return conic.getAffineTransform();
	}

	public String getFromPlaneString() {
		return "xOyPlane";
	}

	public String getTranslatedFromPlaneString() {
		return getApplication().getPlain("xOyPlane");
	}

	public boolean isDefault2D() {
		return true;
	}

	public int getViewID() {
		switch (evNo) {
		case 1:
			return AbstractApplication.VIEW_EUCLIDIAN;
		case 2:
			return AbstractApplication.VIEW_EUCLIDIAN2;
		default:
			return AbstractApplication.VIEW_NONE;
		}
	}

	// Michael Borcherds 2008-02-29
	public void changeLayer(GeoElement geo, int oldlayer, int newlayer) {
		updateMaxLayerUsed(newlayer);
		// Application.debug(drawLayers[oldlayer].size());
		drawLayers[oldlayer].remove((Drawable) DrawableMap.get(geo));
		// Application.debug(drawLayers[oldlayer].size());
		drawLayers[newlayer].add((Drawable) DrawableMap.get(geo));
	}

	public void updateMaxLayerUsed(int layer) {
		if (layer > EuclidianStyleConstants.MAX_LAYERS) {
			layer = EuclidianStyleConstants.MAX_LAYERS;
		}
		if (layer > getApplication().maxLayerUsed) {
			getApplication().maxLayerUsed = layer;
		}
	}

	public int getMaxLayerUsed() {
		return getApplication().maxLayerUsed;
	}

	/**
	 * 
	 * @return null (for 2D) and xOyPlane (for 3D)
	 */
	public GeoPlaneND getPlaneContaining() {
		return kernel.getDefaultPlane();
	}

	/**
	 * 
	 * @return null (for 2D) and xOyPlane (for 3D)
	 */
	public GeoDirectionND getDirection() {
		return getPlaneContaining();
	}

	public void updateForPlane() {
		// only used in EuclidianViewForPlane
	}

	public boolean hasForParent(GeoElement geo) {
		return false;
	}

	public boolean isMoveable(GeoElement geo) {
		return geo.isMoveable();
	}

	public ArrayList<GeoPoint2> getFreeInputPoints(
			AlgoElementInterface algoParent) {
		return algoParent.getFreeInputPoints();
	}

	/**
	 * Replaces num by num2 in xmin, xmax,ymin,ymax. Does not add / remove EV
	 * listeners from these numerics
	 * 
	 * @param num
	 *            old numeric
	 * @param num2
	 *            new numeric
	 */
	public void replaceBoundObject(GeoNumeric num, GeoNumeric num2) {
		if (xmaxObject == num) {
			xmaxObject = num2;
		}
		if (xminObject == num) {
			xminObject = num2;
		}
		if (ymaxObject == num) {
			ymaxObject = num2;
		}
		if (yminObject == num) {
			yminObject = num2;
		}
		updateBounds();
	}

	/**
	 * Sets the global style for rightAngle drawing.
	 * 
	 * @param style
	 */
	public void setRightAngleStyle(int style) {
		getApplication().rightAngleStyle = style;
		updateAllDrawables(true);
	}

	final public int getRightAngleStyle() {
		return getApplication().rightAngleStyle;
	}

	public boolean isAutomaticGridDistance() {
		return automaticGridDistance;
	}

	public double[] getGridDistances() {
		return gridDistances;
	}

	public void setGridDistances(double[] dist) {
		if (dist == null) {
			AbstractApplication.debug("NULL");
			return;
		}
		gridDistances = dist;
		setAutomaticGridDistance(false);
	}

	public int getGridLineStyle() {
		return gridLineStyle;
	}

	public void setAutomaticGridDistance(boolean flag) {
		automaticGridDistance = flag;
		setAxesIntervals(getXscale(), 0);
		setAxesIntervals(getYscale(), 1);
		if (flag) {
			gridDistances[2] = Math.PI / 6;
		}
	}

	public int getAxesLineStyle() {
		return axesLineType;
	}

	public void setAxesLineStyle(int axesLineStyle) {
		this.axesLineType = axesLineStyle;
	}

	public geogebra.common.awt.AffineTransform getCoordTransform() {
		if(coordTransform==null)
			coordTransform = geogebra.common.factories.AwtFactory.prototype.newAffineTransform();
		return coordTransform;
	}

	protected void setCoordTransform(geogebra.common.awt.AffineTransform coordTransform) {
		this.coordTransform = coordTransform;
	}

	final public void updateBackground() {
		// make sure axis number formats are up to date
		setAxesIntervals(getXscale(), 0);
		setAxesIntervals(getYscale(), 1);

		updateBackgroundImage();
		updateAllDrawables(true);
		// repaint();
	}


	public abstract  Graphics2D getTempGraphics2D(geogebra.common.awt.Font plainFontCommon);
	public abstract  geogebra.common.awt.Font getFont();
	protected abstract void setHeight(int h);
	protected abstract void setWidth(int h);
	protected abstract void initCursor();
	protected abstract void setStyleBarMode(int mode);
	/**
	 * adds a GeoElement to this view
	 * 
	 * @param geo
	 *            GeoElement to be added
	 * @return drawable for given GeoElement
	 */
	protected DrawableND newDrawable(GeoElement geo) {
		Drawable d = null;
		switch (geo.getGeoClassType()) {
		case BOOLEAN:
			d = newDrawBoolean((GeoBoolean) geo);
			break;

		case BUTTON:

			d = newDrawButton((GeoButton) geo);
			break;

		case TEXTFIELD:

			d = newDrawTextField((GeoTextField) geo);
			break;

		case POINT:
		case POINT3D:
			d = new DrawPoint(this, (GeoPointND) geo);
			break;

		case SEGMENT:
		case SEGMENT3D:
			d = new DrawSegment(this, (GeoSegmentND) geo);
			break;

		case RAY:
		case RAY3D:
			d = new DrawRay(this, (GeoRayND) geo);
			break;

		case LINE:
		case LINE3D:
			d = new DrawLine(this, (GeoLineND) geo);
			break;

		case POLYGON:
		case POLYGON3D:
			d = new DrawPolygon(this, (GeoPolygon) geo);
			break;

		case POLYLINE:
			d = new DrawPolyLine(this, (GeoPolyLine) geo);
			break;

		case FUNCTION_NVAR:
			if (((GeoFunctionNVar) geo).isBooleanFunction()) {
				d = new DrawInequality(this, (GeoFunctionNVar) geo);
			}
			break;
		case INTERVAL:
			if (((GeoFunction) geo).isBooleanFunction()) {
				d = new DrawInequality(this, (GeoFunction) geo);
			}
			break;

		case ANGLE:
			if (geo.isIndependent()) {
				// independent number may be shown as slider
				d = new DrawSlider(this, (GeoNumeric) geo);
			} else {
				d = new DrawAngle(this, (GeoAngle) geo);
				if (geo.isDrawable()) {
					if (!geo.isColorSet()) {
						geogebra.common.awt.Color col = geo
										.getConstruction()
										.getConstructionDefaults()
										.getDefaultGeo(
												ConstructionDefaults.DEFAULT_ANGLE)
										.getObjectColor();
						AbstractApplication.debug(col);
						geo.setObjColor(col);
					}
				}
			}
			break;

		case NUMERIC:
			AlgoElement algo = geo.getDrawAlgorithm();
			if (algo == null) {
				// independent number may be shown as slider
				if (geo.isEuclidianVisible()) {
					// make sure min/max initialized properly on redefinition
					// eg f(x)=x^2
					// f = 1
					geo.setEuclidianVisible(false);
					geo.setEuclidianVisible(true);
				}
				d = new DrawSlider(this, (GeoNumeric) geo);
			} else if (algo instanceof AlgoSlope) {
				d = new DrawSlope(this, (GeoNumeric) geo);
			} else if (algo instanceof AlgoIntegralDefinite) {
				d = new DrawIntegral(this, (GeoNumeric) geo);
			} else if (algo instanceof AlgoIntegralFunctions) {
				d = new DrawIntegralFunctions(this, (GeoNumeric) geo);
			} else if (algo instanceof AlgoFunctionAreaSums) {
				d = new DrawUpperLowerSum(this, (GeoNumeric) geo);
			} else if (algo instanceof AlgoBoxPlot) {
				d = new DrawBoxPlot(this, (GeoNumeric) geo);
			}
			if (d != null) {
				if (!geo.isColorSet()) {
					ConstructionDefaults consDef = geo
							.getConstruction().getConstructionDefaults();
					if (geo.isIndependent()) {
						geogebra.common.awt.Color col =consDef
										.getDefaultGeo(
												ConstructionDefaults.DEFAULT_NUMBER)
										.getObjectColor();
						geo.setObjColor(col);
					} else {
						geogebra.common.awt.Color col = consDef
										.getDefaultGeo(
												ConstructionDefaults.DEFAULT_POLYGON)
										.getObjectColor();
						geo.setObjColor(col);
					}
				}
			}
			break;

		case VECTOR:
		case VECTOR3D:
			d = new DrawVector(this, (GeoVectorND) geo);
			break;

		case CONICPART:
			d = new DrawConicPart(this, (GeoConicPart) geo);
			break;

		case CONIC:
		case CONIC3D:
			d = new DrawConic(this, (GeoConicND) geo);
			break;

		case IMPLICIT_POLY:
			d = new DrawImplicitPoly(this, (GeoImplicitPoly) geo);
			break;

		case FUNCTION:
		case FUNCTIONCONDITIONAL:
			if (((GeoFunction) geo).isBooleanFunction()) {
				d = new DrawInequality(this, (FunctionalNVar) geo);
			} else {
				d = new DrawParametricCurve(this, (ParametricCurve) geo);
			}
			break;

		case TEXT:
			GeoText text = (GeoText) geo;
			d = new DrawText(this,text);
			break;

		case IMAGE:
			d = new DrawImage(this,(GeoImage) geo);
			break;

		case LOCUS:
			d = new DrawLocus(this, (GeoLocus) geo);
			break;

		case CURVE_CARTESIAN:
			d = new DrawParametricCurve(this, (GeoCurveCartesian) geo);
			break;

		case LIST:
			d = new DrawList(this, (GeoList) geo);
			break;
		}

		return d;
	}

	public abstract Drawable newDrawButton(GeoButton geo);
	public abstract Drawable newDrawTextField(GeoTextField geo);
	public abstract Drawable newDrawBoolean(GeoBoolean geo);
	public abstract void zoomAxesRatio(double d, boolean b);


	final public static boolean usesSelectionAsInput(int mode) {
		switch (mode) {
		case EuclidianConstants.MODE_MIRROR_AT_LINE:
			return true;
		case EuclidianConstants.MODE_MIRROR_AT_POINT:
			return true;
		case EuclidianConstants.MODE_ROTATE_BY_ANGLE:
			return true;
		case EuclidianConstants.MODE_TRANSLATE_BY_VECTOR:
			return false; // changed for new "drag" behaviour
		case EuclidianConstants.MODE_DILATE_FROM_POINT:
			return true;
		default:
			return false;
		}
	}


	final public static boolean usesSelectionRectangleAsInput(int mode) {
		switch (mode) {
		case EuclidianConstants.MODE_VISUAL_STYLE:
		case EuclidianConstants.MODE_FITLINE:
		case EuclidianConstants.MODE_CREATE_LIST:
		case EuclidianConstants.MODE_PEN:
		case EuclidianConstants.MODE_MIRROR_AT_LINE:
		case EuclidianConstants.MODE_MIRROR_AT_POINT:
		case EuclidianConstants.MODE_ROTATE_BY_ANGLE:
		case EuclidianConstants.MODE_TRANSLATE_BY_VECTOR:
		case EuclidianConstants.MODE_DILATE_FROM_POINT:
		case EuclidianConstants.MODE_COPY_VISUAL_STYLE:
			return true;
		default:
			return false;
		}
	}


	// axis control vars
	protected double[] axisCross = { 0, 0 };//private
	protected boolean[] positiveAxes = { false, false };//private
	protected boolean[] drawBorderAxes = { false, false };//private

	// getters and Setters for axis control vars

	public void setSelectionRectangle(geogebra.common.awt.Rectangle selectionRectangle) {
		// Application.printStacktrace("");
		this.selectionRectangle = selectionRectangle;
	}

	public double[] getAxesCross() {
		return axisCross;
	}

	public void setAxesCross(double[] axisCross) {
		this.axisCross = axisCross;
	}

	// for xml handler
	public void setAxisCross(int axis, double cross) {
		axisCross[axis] = cross;
	}

	public boolean[] getPositiveAxes() {
		return positiveAxes;
	}

	public void setPositiveAxes(boolean[] positiveAxis) {
		this.positiveAxes = positiveAxis;
	}

	// for xml handler
	public void setPositiveAxis(int axis, boolean isPositiveAxis) {
		positiveAxes[axis] = isPositiveAxis;
	}

	public boolean[] getDrawBorderAxes() {
		return drawBorderAxes;
	}

	public void setDrawBorderAxes(boolean[] drawBorderAxes) {
		this.drawBorderAxes = drawBorderAxes;
		// don't show corner coordinates if one of the axes is sticky
		this.setAxesCornerCoordsVisible(!(drawBorderAxes[0] || drawBorderAxes[1]));
	}

	// for xml handler
	public void setDrawBorderAxis(int axis, boolean drawBorderAxis) {
		drawBorderAxes[axis] = drawBorderAxis;
	}

	
	
	public boolean isAxesCornerCoordsVisible() {
		return showAxesCornerCoords;
	}

	public void setAxesCornerCoordsVisible(boolean showAxesCornerCoords) {
		this.showAxesCornerCoords = showAxesCornerCoords;
	}

	public final double getPrintingScale() {
		return printingScale;
	}

	public final void setPrintingScale(double printingScale) {
		this.printingScale = printingScale;
	}

	/**
	 * 
	 * setters and getters for EuclidianViewInterface
	 * 
	 */

	public String[] getAxesLabels() {
		return axesLabels;
	}

	public void setAxesLabels(String[] axesLabels) {
		this.axesLabels = axesLabels;
		for (int i = 0; i < 2; i++) {
			if ((axesLabels[i] != null) && (axesLabels[i].length() == 0)) {
				axesLabels[i] = null;
			}
		}
	}

	/**
	 * sets the axis label to axisLabel
	 * 
	 * @param axis
	 * @param axisLabel
	 */
	public void setAxisLabel(int axis, String axisLabel) {
		if ((axisLabel != null) && (axisLabel.length() == 0)) {
			axesLabels[axis] = null;
		} else {
			axesLabels[axis] = axisLabel;
		}
	}

	public void setAutomaticAxesNumberingDistance(boolean flag, int axis) {
		automaticAxesNumberingDistances[axis] = flag;
		if (axis == 0) {
			setAxesIntervals(getXscale(), 0);
		} else {
			setAxesIntervals(getYscale(), 1);
		}
	}

	public boolean[] isAutomaticAxesNumberingDistance() {
		return automaticAxesNumberingDistances;
	}

	public double[] getAxesNumberingDistances() {
		return axesNumberingDistances;
	}

	/**
	 * 
	 * @param dist
	 * @param axis
	 *            0 for xAxis, 1 for yAxis
	 */
	public void setAxesNumberingDistance(double dist, int axis) {
		if (!Double.isNaN(dist)) {
			axesNumberingDistances[axis] = dist;
			setAutomaticAxesNumberingDistance(false, axis);
		} else {
			setAutomaticAxesNumberingDistance(true, axis);
		}
	}

	// Michael Borcherds 2008-04-11
	public boolean getGridIsBold() {
		return gridIsBold;
	}

	public boolean[] getShowAxesNumbers() {
		return showAxesNumbers;
	}

	public void setShowAxesNumbers(boolean[] showAxesNumbers) {
		this.showAxesNumbers = showAxesNumbers;
	}

	public void setShowAxisNumbers(int axis, boolean showAxisNumbers) {
		showAxesNumbers[axis] = showAxisNumbers;
	}

	public String[] getAxesUnitLabels() {
		return axesUnitLabels;
	}

	public void setAxesUnitLabels(String[] axesUnitLabels) {
		this.axesUnitLabels = axesUnitLabels;

		// check if pi is an axis unit
		for (int i = 0; i < 2; i++) {
			piAxisUnit[i] = (axesUnitLabels[i] != null)
					&& axesUnitLabels[i].equals(Unicode.PI_STRING);
		}
		setAxesIntervals(getXscale(), 0);
		setAxesIntervals(getYscale(), 1);
	}

	public int[] getAxesTickStyles() {
		return axesTickStyles;
	}

	public void setAxisTickStyle(int axis, int tickStyle) {
		axesTickStyles[axis] = tickStyle;
	}

	public void setAxesTickStyles(int[] axesTickStyles) {
		this.axesTickStyles = axesTickStyles;
	}

	public void setShowMouseCoords(boolean b) {
		showMouseCoords = b;
	}

	public boolean getAllowShowMouseCoords() {
		return allowShowMouseCoords;
	}

	public void setAllowShowMouseCoords(boolean neverShowMouseCoords) {
		this.allowShowMouseCoords = neverShowMouseCoords;
	}

	public boolean getShowMouseCoords() {
		return showMouseCoords;
	}

	public void setShowAxesRatio(boolean b) {
		showAxesRatio = b;
	}

	public Previewable getPreviewDrawable() {
		return previewDrawable;
	}

	public double getGridDistances(int i) {
		return gridDistances[i];
	}

	public boolean getShowGrid() {
		return showGrid;
	}

	public final boolean isGridOrAxesShown() {
		return showAxes[0] || showAxes[1] || showGrid;
	}

	/**
	 * says if the axis is shown or not
	 * 
	 * @param axis
	 *            id of the axis
	 * @return if the axis is shown
	 */
	public boolean getShowAxis(int axis) {
		return showAxes[axis];
	}

	public boolean getShowXaxis() {
		// return showAxes[0];
		return getShowAxis(AXIS_X);
	}

	public boolean getShowYaxis() {
		return getShowAxis(AXIS_Y);
	}

	// ///////////////////////////////////////
	// previewables

	public Previewable createPreviewLine(ArrayList<GeoPointND> selectedPoints) {
		return new DrawLine(this, selectedPoints, DrawLine.PREVIEW_LINE);
	}

	public Previewable createPreviewPerpendicularBisector(
			ArrayList<GeoPointND> selectedPoints) {
		return new DrawLine(this, selectedPoints,
				DrawLine.PREVIEW_PERPENDICULAR_BISECTOR);
	}

	public Previewable createPreviewAngleBisector(
			ArrayList<GeoPointND> selectedPoints) {
		return new DrawLine(this, selectedPoints,
				DrawLine.PREVIEW_ANGLE_BISECTOR);
	}

	public Previewable createPreviewSegment(ArrayList<GeoPointND> selectedPoints) {
		return new DrawSegment(this, selectedPoints);
	}

	public Previewable createPreviewRay(ArrayList<GeoPointND> selectedPoints) {
		return new DrawRay(this, selectedPoints);
	}

	public Previewable createPreviewVector(ArrayList<GeoPointND> selectedPoints) {
		return new DrawVector(this, selectedPoints);
	}

	public Previewable createPreviewConic(int mode,
			ArrayList<GeoPointND> selectedPoints) {
		return new DrawConic(this, mode, selectedPoints);
	}
	
	public Previewable createPreviewPolygon(ArrayList<GeoPointND> selectedPoints) {
		return new DrawPolygon(this, selectedPoints);
	}

	public Previewable createPreviewAngle(ArrayList<GeoPointND> selectedPoints) {
		return new DrawAngle(this, selectedPoints);
	}

	public Previewable createPreviewPolyLine(
			ArrayList<GeoPointND> selectedPoints) {
		return new DrawPolyLine(this, selectedPoints);
	}

	public void updatePreviewable() {
		Point mouseLoc = AwtFactory.prototype.newPoint(getEuclidianController().mouseLoc.x,getEuclidianController().mouseLoc.y);
		getPreviewDrawable().updateMousePos(toRealWorldCoordX(mouseLoc.x),
				toRealWorldCoordY(mouseLoc.y));
	}


	public void mouseEntered() {
	}

	public void mouseExited() {
	}

	public Previewable createPreviewParallelLine(
			ArrayList<GeoPointND> selectedPoints,
			ArrayList<GeoLineND> selectedLines) {
		return new DrawLine(this, selectedPoints, selectedLines, true);
	}

	public Previewable createPreviewPerpendicularLine(
			ArrayList<GeoPointND> selectedPoints,
			ArrayList<GeoLineND> selectedLines) {
		return new DrawLine(this, selectedPoints, selectedLines, false);
	}

	protected void setApplication(AbstractApplication application) {
		this.application = application;
	}
	
	public AbstractApplication getApplication() {
		return this.application;
	}

	public void updateFonts() {
		setFontSize(getApplication().getFontSize());

		setFontPoint(getApplication().getPlainFontCommon().deriveFont(Font.PLAIN, getFontSize()));
		setFontAngle(getFontPoint());
		setFontLine(getFontPoint());
		setFontVector(getFontPoint());
		setFontConic(getFontPoint());
		setFontCoords(getApplication().getPlainFontCommon().deriveFont(Font.PLAIN, getFontSize() - 2));
		setFontAxes(getFontCoords());

		updateDrawableFontSize();
		updateBackground();
	}

	public abstract void updateSize();
	public abstract boolean requestFocusInWindow();
	
	// Michael Borcherds 2008-03-01
		protected void drawGeometricObjects(geogebra.common.awt.Graphics2D g2) {
			// boolean
			// isSVGExtensions=g2.getClass().getName().endsWith("SVGExtensions");
			int layer;

			for (layer = 0; layer <= getApplication().maxLayerUsed; layer++) // only draw layers
																// we need
			{
				// if (isSVGExtensions)
				// ((geogebra.export.SVGExtensions)g2).startGroup("layer "+layer);
				drawLayers[layer].drawAll(g2);
				// if (isSVGExtensions)
				// ((geogebra.export.SVGExtensions)g2).endGroup("layer "+layer);
			}
		}

		// Michael Borcherds 2008-03-01
		protected void drawObjects(geogebra.common.awt.Graphics2D g2) {

			drawGeometricObjects(g2);
			drawActionObjects(g2);

			if (previewDrawable != null) {
				previewDrawable.drawPreview(g2);
			}
		}
		
		final protected void clearBackground(geogebra.common.awt.Graphics2D g) {
			g.setColor(getBackgroundCommon());
			g.fillRect(0, 0, getWidth(), getHeight());
		}
		
		protected void drawBackgroundWithImages(geogebra.common.awt.Graphics2D g, boolean transparency) {
			if (!transparency) {
				clearBackground(g);
			}

			bgImageList.drawAll(g);
			drawBackground(g, false);
		}
		
		final protected void drawAxesRatio(geogebra.common.awt.Graphics2D g2) {
			Point pos = AwtFactory.prototype.newPoint(euclidianController.mouseLoc.x,euclidianController.mouseLoc.y);
			if (pos == null) {
				return;
			}

			g2.setColor(geogebra.common.awt.Color.darkGray);
			g2.setFont(getFontLine());
			g2.drawString(getXYscaleRatioString(), pos.x + 15, pos.y + 30);
		}

		final protected boolean drawSliderValue(geogebra.common.awt.Graphics2D g2) {

			if (mode != EuclidianConstants.MODE_MOVE) {
				return false;
			}
			
			if (euclidianController.mouseLoc == null) {
				return false;
			}
			
			Point pos = AwtFactory.prototype.newPoint(euclidianController.mouseLoc.x, euclidianController.mouseLoc.y);

			String val = euclidianController.getSliderValue();

			if (val == null) {
				return false;
			}

			g2.setColor(geogebra.common.awt.Color.darkGray);
			g2.setFont(getFontLine());
			g2.drawString(val, pos.x + 15, pos.y + 15);

			return true;
		}
		
		//@Override
		final public void paint(geogebra.common.awt.Graphics2D g2) {
			//Graphics2D g2 = (Graphics2D) g;
			// lastGraphics2D = g2;

			setDefRenderingHints(g2);
			// g2.setClip(0, 0, width, height);

			// BACKGROUND
			// draw background image (with axes and/or grid)
			if (bgImage == null) {
				if (firstPaint) {
					updateSize();
					g2.drawImage(bgImage, 0, 0, null);
					firstPaint = false;
				} else {
					drawBackgroundWithImages(g2);
				}
			} else {
				// draw background image
				g2.drawImage(bgImage, 0, 0, null);
			}

			/*
			 * switch (drawMode) { case DRAW_MODE_BACKGROUND_IMAGE: // draw
			 * background image (with axes and/or grid) if (bgImage == null)
			 * updateSize(); else g2.drawImage(bgImage, 0,0, null); break;
			 * 
			 * default: // DRAW_MODE_DIRECT_DRAW: drawBackground(g2, true); }
			 */

			// FOREGROUND
			if (antiAliasing) {
				setAntialiasing(g2);
			}

			// draw equations, checkboxes and all geo objects
			drawObjects(g2);

			if (selectionRectangle != null) {
				drawZoomRectangle(g2);
			}

			// when mouse over slider, show preview value of slider for that point
			boolean drawn = drawSliderValue(g2);

			if (!drawn) {
				if (allowShowMouseCoords && showMouseCoords
						&& (showAxes[0] || showAxes[1] || showGrid)) {
					drawMouseCoords(g2);
				}
				if (showAxesRatio) {
					drawAxesRatio(g2);
				}
			}

			if (kernel.needToShowAnimationButton()) {
				drawAnimationButtons(g2);
			}
		}
		
		final public void updateBackgroundImage() {
			if (bgGraphics != null) {
				drawBackgroundWithImages(bgGraphics,false);
			}
		}
		
		protected void drawZoomRectangle(geogebra.common.awt.Graphics2D g2) {
			g2.setColor(colZoomRectangleFill);
			g2.setStroke(boldAxesStroke);
			g2.fill( selectionRectangle );
			g2.setColor(colZoomRectangle);
			g2.draw( selectionRectangle );
		}

		final protected void drawMouseCoords(geogebra.common.awt.Graphics2D g2) {
			if (euclidianController.mouseLoc == null) {
				return;
			}
			Point pos = AwtFactory.prototype.newPoint(euclidianController.mouseLoc.x,euclidianController.mouseLoc.y);

			sb.setLength(0);
			sb.append('(');
			sb.append(kernel.format(Kernel
					.checkDecimalFraction(euclidianController.xRW)));
			if (kernel.getCoordStyle() == Kernel.COORD_STYLE_AUSTRIAN) {
				sb.append(" | ");
			} else {
				sb.append(", ");
			}
			sb.append(kernel.format(Kernel
					.checkDecimalFraction(euclidianController.yRW)));
			sb.append(')');

			g2.setColor(geogebra.common.awt.Color.darkGray);
			g2.setFont(getFontCoords());
			g2.drawString(sb.toString(), pos.x + 15, pos.y + 15);
		}

		
		private void drawBackgroundWithImages(geogebra.common.awt.Graphics2D g) {
			drawBackgroundWithImages(g, false);
		}
		final protected void drawBackground(geogebra.common.awt.Graphics2D g, boolean clear) {
			if (clear) {
				clearBackground(g);
			}

			setAntialiasing(g);

			// handle drawing axes near the screen edge
			if (drawBorderAxes[0] || drawBorderAxes[1]) {

				// edge axes are not drawn at the exact edge, instead they
				// are inset enough to draw the labels
				// labelOffset = amount of space needed to draw labels
				Point labelOffset = getMaximumLabelSize(g);

				// force the the axisCross position to be near the edge
				if (drawBorderAxes[0]) {
					axisCross[0] = getYmin() + ((labelOffset.y + 10) / getYscale());
				}
				if (drawBorderAxes[1]) {
					axisCross[1] = getXmin() + ((labelOffset.x + 10) / getXscale());
				}
			}

			if (showGrid) {
				drawGrid(g);
			}
			if (showAxes[0] || showAxes[1]) {
				drawAxes(g);
			}

			if (getApplication().showResetIcon() && getApplication().isApplet()) {
				drawResetIcon(g);
			}
		}
		protected Ellipse2DDouble circle = geogebra.common.factories.AwtFactory.prototype.newEllipse2DDouble(); //polar grid circles
		protected Line2D tempLine = geogebra.common.factories.AwtFactory.prototype.newLine2D();
		/**
		 * Get styleBar
		 */
		protected geogebra.common.euclidian.EuclidianStyleBar styleBar;
		
		final protected void drawGrid(geogebra.common.awt.Graphics2D g2) {
			g2.setColor(gridColor);
			g2.setStroke(gridStroke);

			// vars for handling positive-only axes
			double xCrossPix = this.getxZero() + (axisCross[1] * getXscale());
			double yCrossPix = this.getyZero() - (axisCross[0] * getYscale());
			int yAxisEnd = positiveAxes[1] ? (int) yCrossPix : getHeight();
			int xAxisStart = positiveAxes[0] ? (int) xCrossPix : 0;

			// set the clipping region to the region defined by the axes
			geogebra.common.awt.Shape oldClip = g2.getClip();
			if (gridType != GRID_POLAR) {
				g2.setClip(xAxisStart, 0, getWidth(), yAxisEnd);
			}

			switch (gridType) {

			case GRID_CARTESIAN:

				// vertical grid lines
				double tickStep = getXscale() * gridDistances[0];
				double start = getxZero() % tickStep;
				double pix = start;

				for (int i = 0; pix <= getWidth(); i++) {
					// int val = (int) Math.round(i);
					// g2.drawLine(val, 0, val, height);
					tempLine.setLine(pix, 0, pix, getHeight());
					g2.draw(tempLine);

					pix = start + (i * tickStep);
				}

				// horizontal grid lines
				tickStep = getYscale() * gridDistances[1];
				start = getyZero() % tickStep;
				pix = start;

				for (int j = 0; pix <= getHeight(); j++) {
					// int val = (int) Math.round(j);
					// g2.drawLine(0, val, width, val);
					tempLine.setLine(0, pix, getWidth(), pix);
					g2.draw(tempLine);

					pix = start + (j * tickStep);
				}

				break;

			case GRID_ISOMETRIC:

				double tickStepX = getXscale() * gridDistances[0] * Math.sqrt(3.0);
				double startX = getxZero() % (tickStepX);
				double startX2 = getxZero() % (tickStepX / 2);
				double tickStepY = getYscale() * gridDistances[0];
				double startY = getyZero() % tickStepY;

				// vertical
				pix = startX2;
				for (int j = 0; pix <= getWidth(); j++) {
					tempLine.setLine(pix, 0, pix, getHeight());
					g2.draw(tempLine);
					pix = startX2 + ((j * tickStepX) / 2.0);
				}

				// extra lines needed because it's diagonal
				int extra = (int) ((((getHeight() * getXscale()) / getYscale()) * Math.sqrt(3.0)) / tickStepX) + 3;

				// positive gradient
				pix = startX + (-(extra + 1) * tickStepX);
				for (int j = -extra; pix <= getWidth(); j += 1) {
					tempLine.setLine(
							pix,
							startY - tickStepY,
							pix
									+ (((getHeight() + tickStepY) * Math.sqrt(3) * getXscale()) / getYscale()),
							(startY - tickStepY) + getHeight() + tickStepY);
					g2.draw(tempLine);
					pix = startX + (j * tickStepX);
				}

				// negative gradient
				pix = startX;
				for (int j = 0; pix <= (getWidth() + ((((getHeight() * getXscale()) / getYscale()) + tickStepY) * Math
						.sqrt(3.0))); j += 1)
				// for (int j=0; j<=kk; j+=1)
				{
					tempLine.setLine(
							pix,
							startY - tickStepY,
							pix
									- (((getHeight() + tickStepY) * Math.sqrt(3) * getXscale()) / getYscale()),
							(startY - tickStepY) + getHeight() + tickStepY);
					g2.draw(tempLine);
					pix = startX + (j * tickStepX);
				}

				break;

			case GRID_POLAR: // G.Sturr 2010-8-13

				// find minimum grid radius
				double min;
				if ((getxZero() > 0) && (getxZero() < getWidth()) && (getyZero() > 0)
						&& (getyZero() < getHeight())) {
					// origin onscreen: min = 0
					min = 0;
				} else {
					// origin offscreen: min = distance to closest screen border
					double minW = Math
							.min(Math.abs(getxZero()), Math.abs(getxZero() - getWidth()));
					double minH = Math.min(Math.abs(getyZero()),
							Math.abs(getyZero() - getHeight()));
					min = Math.min(minW, minH);
				}

				// find maximum grid radius
				// max = max distance of origin to screen corners
				double d1 = MyMath.length(getxZero(), getyZero()); // upper left
				double d2 = MyMath.length(getxZero(), getyZero() - getHeight()); // lower left
				double d3 = MyMath.length(getxZero() - getWidth(), getyZero()); // upper right
				double d4 = MyMath.length(getxZero() - getWidth(), getyZero() - getHeight()); // lower
																			// right
				double max = Math.max(Math.max(d1, d2), Math.max(d3, d4));

				// draw the grid circles
				// note: x tick intervals are used for the radius intervals,
				// it is assumed that the x/y scaling ratio is 1:1
				double tickStepR = getXscale() * gridDistances[0];
				double r = min - (min % tickStepR);
				while (r <= max) {
					circle.setFrame(getxZero() - r, getyZero() - r, 2 * r, 2 * r);
					g2.draw(circle);
					r = r + tickStepR;

				}

				// draw the radial grid lines
				double angleStep = gridDistances[2];
				double y1,
				y2,
				m;

				// horizontal axis
				tempLine.setLine(0, getyZero(), getWidth(), getyZero());
				g2.draw(tempLine);

				// radial lines
				for (double a = angleStep; a < Math.PI; a = a + angleStep) {

					if (Math.abs(a - (Math.PI / 2)) < 0.0001) {
						// vertical axis
						tempLine.setLine(getxZero(), 0, getxZero(), getHeight());
					} else {
						m = Math.tan(a);
						y1 = (m * (getxZero())) + getyZero();
						y2 = (m * (getxZero() - getWidth())) + getyZero();
						tempLine.setLine(0, y1, getWidth(), y2);
					}
					g2.draw(tempLine);
				}

				break;
			}

			// reset the clipping region
			g2.setClip(oldClip);
		}

		
		private double getLabelLength(double rw, FontRenderContext frc) {
			TextLayout layout = geogebra.common.factories.AwtFactory.prototype.newTextLayout(
					kernel.formatPiE(rw, axesNumberFormat[0])
							+ ((axesUnitLabels[0] != null) && !piAxisUnit[0] ? axesUnitLabels[0]
									: ""), getFontAxes(), frc);
			return layout.getAdvance();
		}
		protected void drawAxes(geogebra.common.awt.Graphics2D g2) {

			// xCrossPix: yAxis crosses the xAxis at this x pixel
			double xCrossPix = this.getxZero() + (axisCross[1] * getXscale());

			// yCrossPix: xAxis crosses the YAxis at his y pixel
			double yCrossPix = this.getyZero() - (axisCross[0] * getYscale());

			// yAxis end value (for drawing half-axis)
			int yAxisEnd = positiveAxes[1] ? (int) yCrossPix : getHeight();

			// xAxis start value (for drawing half-axis)
			int xAxisStart = positiveAxes[0] ? (int) xCrossPix : 0;

			// for axes ticks
			double yZeroTick = yCrossPix;
			double xZeroTick = xCrossPix;
			double yBig = yCrossPix + 4;
			double xBig = xCrossPix - 4;
			double ySmall1 = yCrossPix + 0;
			double ySmall2 = yCrossPix + 2;
			double xSmall1 = xCrossPix - 0;
			double xSmall2 = xCrossPix - 2;
			int xoffset, yoffset;

			boolean bold = (axesLineType == EuclidianStyleConstants.AXES_LINE_TYPE_FULL_BOLD)
					|| (axesLineType == EuclidianStyleConstants.AXES_LINE_TYPE_ARROW_BOLD);
			boolean drawArrowsx = ((axesLineType == EuclidianStyleConstants.AXES_LINE_TYPE_ARROW) || (axesLineType == EuclidianStyleConstants.AXES_LINE_TYPE_ARROW_BOLD))
					&& !(positiveAxes[0] && (getXmax() < axisCross[1]));
			boolean drawArrowsy = ((axesLineType == EuclidianStyleConstants.AXES_LINE_TYPE_ARROW) || (axesLineType == EuclidianStyleConstants.AXES_LINE_TYPE_ARROW_BOLD))
					&& !(positiveAxes[1] && (getYmax() < axisCross[0]));
			// AXES_TICK_STYLE_MAJOR_MINOR = 0;
			// AXES_TICK_STYLE_MAJOR = 1;
			// AXES_TICK_STYLE_NONE = 2;

			boolean[] drawMajorTicks = { axesTickStyles[0] <= 1,
					axesTickStyles[1] <= 1 };
			boolean[] drawMinorTicks = { axesTickStyles[0] == 0,
					axesTickStyles[1] == 0 };

			FontRenderContext frc = g2.getFontRenderContext();
			g2.setFont(getFontAxes());
			int fontsize = getFontAxes().getSize();
			int arrowSize = fontsize / 3;
			g2.setPaint(axesColor);

			if (bold) {
				axesStroke = boldAxesStroke;
				tickStroke = boldAxesStroke;
				ySmall2++;
				xSmall2--;
				arrowSize += 1;
			} else {
				axesStroke = defAxesStroke;
				tickStroke = defAxesStroke;
			}

			// turn antialiasing off
			// Object antiAliasValue = g2
			// .getRenderingHint(RenderingHints.KEY_ANTIALIASING);
			// g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
			// RenderingHints.VALUE_ANTIALIAS_OFF);

			// make sure arrows don't go off screen (eg EMF export)
			double arrowAdjustx = drawArrowsx ? axesStroke.getLineWidth() : 0;
			double arrowAdjusty = drawArrowsy ? axesStroke.getLineWidth() : 0;

			// ========================================
			// X-AXIS

			if (showAxes[0] && (getYmin() < axisCross[0]) && (getYmax() > axisCross[0])) {
				if (showGrid) {
					yoffset = fontsize + 4;
					xoffset = 10;
				} else {
					yoffset = fontsize + 4;
					xoffset = 1;
				}

				// label of x axis
				if (axesLabels[0] != null) {
					TextLayout layout = geogebra.common.factories.AwtFactory.prototype.newTextLayout(axesLabels[0], getFontLine(), frc);
					g2.drawString(axesLabels[0],
							(int) (getWidth() - 10 - layout.getAdvance()),
							(int) (yCrossPix - 4));
				}

				// numbers

				double rw = getXmin() - (getXmin() % axesNumberingDistances[0]);
				int labelno = (int) Math.round(rw / axesNumberingDistances[0]);
				// by default we start with minor tick to the left of first major
				// tick, exception is for positive only
				double smallTickOffset = 0;
				double axesStep = getXscale() * axesNumberingDistances[0]; // pixelstep
				if (getPositiveAxes()[0] && (rw > getXmin())) {
					// start labels at the y-axis instead of screen border
					// be careful: axisCross[1] = x value for which the y-axis
					// crosses,
					// so xmin is replaced axisCross[1] and not axisCross[0]
					rw = MyMath.nextMultiple(axisCross[1],
							axesNumberingDistances[0]);
					smallTickOffset = axesStep;
					labelno = 0;
				}

				double pix = getxZero() + (rw * getXscale());

				double smallTickPix;
				double tickStep = axesStep / 2;
				double labelLengthMax = Math.max(
						getLabelLength(rw, frc),
						getLabelLength(MyMath.nextMultiple(getXmax(),
								axesNumberingDistances[0]), frc));
				int unitsPerLabelX = (int) MyMath.nextPrettyNumber(labelLengthMax
						/ axesStep);

				if (pix < SCREEN_BORDER) {
					// big tick
					if (drawMajorTicks[0]) {
						g2.setStroke(tickStroke);
						tempLine.setLine(pix, yZeroTick, pix, yBig);
						g2.draw(tempLine);
					}
					pix += axesStep;
					rw += axesNumberingDistances[0];
					labelno += 1;
				}
				int maxX = getWidth() - SCREEN_BORDER;

				for (; pix < getWidth(); rw += axesNumberingDistances[0], pix += axesStep) {
					if (pix <= maxX) {
						if (showAxesNumbers[0]) {
							String strNum = kernel.formatPiE(rw,
									axesNumberFormat[0]);

							// flag to handle drawing a label at axis crossing point
							boolean zero = strNum.equals(""
									+ kernel.formatPiE(axisCross[1],
											axesNumberFormat[0]));
							if ((labelno % unitsPerLabelX) == 0) {
								sb.setLength(0);
								sb.append(strNum);
								if ((axesUnitLabels[0] != null) && !piAxisUnit[0]) {
									sb.append(axesUnitLabels[0]);
								}

								TextLayout layout = geogebra.common.factories.AwtFactory.prototype.newTextLayout(sb.toString(),
										getFontAxes(), frc);
								int x, y = (int) (yCrossPix + yoffset);

								// if label intersects the y-axis then draw it 6
								// pixels to the left
								if (zero && showAxes[1] && !positiveAxes[1]) {
									x = (int) (pix + 6);
								} else {
									x = (int) ((pix + xoffset) - (layout
											.getAdvance() / 2));
								}

								// make sure we don't print one string on top of the
								// other

								// prevTextEnd = (int) (x + layout.getAdvance());
								g2.drawString(sb.toString(), x, y);
							}
						}

						// big tick
						if (drawMajorTicks[0]) {
							g2.setStroke(tickStroke);
							tempLine.setLine(pix, yZeroTick, pix, yBig);
							g2.draw(tempLine);
						}
					} else if (drawMajorTicks[0] && !drawArrowsx) {
						// draw last tick if there is no arrow
						tempLine.setLine(pix, yZeroTick, pix, yBig);
						g2.draw(tempLine);
					}

					// small tick
					smallTickPix = (pix - tickStep) + smallTickOffset;
					if (drawMinorTicks[0]) {
						g2.setStroke(tickStroke);
						tempLine.setLine(smallTickPix, ySmall1, smallTickPix,
								ySmall2);
						g2.draw(tempLine);
					}
					labelno++;
				}
				// last small tick
				smallTickPix = (pix - tickStep) + smallTickOffset;
				if (drawMinorTicks[0]) {
					g2.setStroke(tickStroke);
					tempLine.setLine(smallTickPix, ySmall1, smallTickPix, ySmall2);
					g2.draw(tempLine);
				}

				// x-Axis
				g2.setStroke(axesStroke);

				// tempLine.setLine(0, yCrossPix, width, yCrossPix);
				tempLine.setLine(xAxisStart, yCrossPix, getWidth() - arrowAdjustx - 1,
						yCrossPix);

				g2.draw(tempLine);

				if (drawArrowsx) {

					// draw arrow for x-axis
					tempLine.setLine(getWidth() - arrowAdjustx, yCrossPix + 0.5, getWidth()
							- arrowAdjustx - arrowSize, yCrossPix - arrowSize);
					g2.draw(tempLine);
					tempLine.setLine(getWidth() - arrowAdjustx, yCrossPix - 0.5, getWidth()
							- arrowAdjustx - arrowSize, yCrossPix + arrowSize);
					g2.draw(tempLine);

					// g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
					// RenderingHints.VALUE_ANTIALIAS_OFF);
				}
			}

			// ========================================
			// Y-AXIS

			if (showAxes[1] && (getXmin() < axisCross[1]) && (getXmax() > axisCross[1])) {

				if (showGrid) {
					xoffset = -2 - (fontsize / 4);
					yoffset = -2;
				} else {
					xoffset = -4 - (fontsize / 4);
					yoffset = (fontsize / 2) - 1;
				}

				// label of y axis
				if (axesLabels[1] != null) {
					TextLayout layout = geogebra.common.factories.AwtFactory.prototype.newTextLayout(axesLabels[1], getFontLine(), frc);
					g2.drawString(axesLabels[1], (int) (xCrossPix + 5),
							(int) (5 + layout.getAscent()));
				}

				// numbers
				double rw = getYmin() - (getYmin() % axesNumberingDistances[1]);
				int labelno = (int) Math.round(rw / axesNumberingDistances[1]);
				// by default we start with minor tick to the left of first major
				// tick, exception is for positive only
				double smallTickOffset = 0;
				double axesStep = getYscale() * axesNumberingDistances[1]; // pixelstep
				if (getPositiveAxes()[1] && (rw > getYmin())) {
					// start labels at the y-axis instead of screen border
					// be careful: axisCross[1] = x value for which the y-axis
					// crosses,
					// so xmin is replaced axisCross[1] and not axisCross[0]
					rw = MyMath.nextMultiple(axisCross[0],
							axesNumberingDistances[1]);
					smallTickOffset = axesStep;
					labelno = 0;
				}

				double pix = getyZero() - (rw * getYscale());

				double tickStep = axesStep / 2;

				double maxHeight = geogebra.common.factories.AwtFactory.prototype.newTextLayout("9", 
						getFontAxes(), frc).getBounds()
						.getHeight() * 2;
				int unitsPerLabelY = (int) MyMath.nextPrettyNumber(maxHeight
						/ axesStep);

				if (pix > (getHeight() - SCREEN_BORDER)) {
					// big tick
					if (drawMajorTicks[1]) {
						g2.setStroke(tickStroke);
						tempLine.setLine(xBig, pix, xZeroTick, pix);
						g2.draw(tempLine);
					}
					pix -= axesStep;
					rw += axesNumberingDistances[1];
					labelno++;
				}

				double smallTickPix = pix + tickStep;

				// draw all of the remaining ticks and labels

				// int maxY = height - SCREEN_BORDER;
				int maxY = SCREEN_BORDER;

				// for (; pix <= height; rw -= axesNumberingDistances[1], pix +=
				// axesStep) {
				// yAxisEnd

				for (; pix >= maxY; rw += axesNumberingDistances[1], pix -= axesStep, labelno++) {
					if (pix >= maxY) {
						if (showAxesNumbers[1]) {
							String strNum = kernel.formatPiE(rw,
									axesNumberFormat[1]);

							// flag for handling label at axis cross point
							boolean zero = strNum.equals(""
									+ kernel.formatPiE(axisCross[0],
											axesNumberFormat[0]));
							if ((labelno % unitsPerLabelY) == 0) {
								sb.setLength(0);
								sb.append(strNum);
								if ((axesUnitLabels[1] != null) && !piAxisUnit[1]) {
									sb.append(axesUnitLabels[1]);
								}

								TextLayout layout = geogebra.common.factories.AwtFactory.prototype.newTextLayout(sb.toString(),
										getFontAxes(), frc);
								int x = (int) ((xCrossPix + xoffset) - layout
										.getAdvance());
								int y;
								// if the label is at the axis cross point then draw
								// it 2 pixels above
								if (zero && showAxes[0] && !positiveAxes[0]) {
									y = (int) (yCrossPix - 2);
								} else {
									y = (int) (pix + yoffset);
								}
								g2.drawString(sb.toString(), x, y);
							}
						}
						if (drawMajorTicks[1]) {
							g2.setStroke(tickStroke);
							tempLine.setLine(xBig, pix, xZeroTick, pix);
							g2.draw(tempLine);
						}
					} else if (drawMajorTicks[1] && !drawArrowsy) {
						// draw last tick if there is no arrow
						g2.setStroke(tickStroke);
						tempLine.setLine(xBig, pix, xZeroTick, pix);
						g2.draw(tempLine);
					}

					// small tick
					smallTickPix = (pix + tickStep) - smallTickOffset;
					if (drawMinorTicks[1]) {
						g2.setStroke(tickStroke);
						tempLine.setLine(xSmall1, smallTickPix, xSmall2,
								smallTickPix);
						g2.draw(tempLine);
					}

				}// end for
				smallTickPix = (pix + tickStep) - smallTickOffset;
				if (drawMinorTicks[0]) {
					g2.setStroke(tickStroke);
					tempLine.setLine(smallTickPix, ySmall1, smallTickPix, ySmall2);
					g2.draw(tempLine);
				}

				// y-Axis

				// tempLine.setLine(xZero, 0, xZero, height);

				tempLine.setLine(xCrossPix, arrowAdjusty + (drawArrowsy ? 1 : -1),
						xCrossPix, yAxisEnd);

				g2.draw(tempLine);

				if (drawArrowsy) {
					// draw arrow for y-axis
					tempLine.setLine(xCrossPix + 0.5, arrowAdjusty, xCrossPix
							- arrowSize, arrowAdjusty + arrowSize);
					g2.draw(tempLine);
					tempLine.setLine(xCrossPix - 0.5, arrowAdjusty, xCrossPix
							+ arrowSize, arrowAdjusty + arrowSize);
					g2.draw(tempLine);
				}
			}

			// if one of the axes is not visible, show upper left and lower right
			// corner coords
			if (showAxesCornerCoords) {
				if ((getXmin() > 0) || (getXmax() < 0) || (getYmin() > 0) || (getYmax() < 0)) {
					// uper left corner
					sb.setLength(0);
					sb.append('(');
					sb.append(kernel.formatPiE(getXmin(), axesNumberFormat[0]));
					sb.append(AbstractApplication.unicodeComma);
					sb.append(" ");
					sb.append(kernel.formatPiE(getYmax(), axesNumberFormat[1]));
					sb.append(')');

					int textHeight = 2 + getFontAxes().getSize();
					g2.setFont(getFontAxes());
					g2.drawString(sb.toString(), 5, textHeight);

					// lower right corner
					sb.setLength(0);
					sb.append('(');
					sb.append(kernel.formatPiE(getXmax(), axesNumberFormat[0]));
					sb.append(AbstractApplication.unicodeComma);
					sb.append(" ");
					sb.append(kernel.formatPiE(getYmin(), axesNumberFormat[1]));
					sb.append(')');

					TextLayout layout = geogebra.common.factories.AwtFactory.prototype.newTextLayout(sb.toString(), getFontAxes(), frc);
					layout.draw(g2, (int) (getWidth() - 5 - layout.getAdvance()),
							getHeight() - 5);
				}
			}
		}

		protected void drawResetIcon(geogebra.common.awt.Graphics2D g){
			
		}
		protected abstract void drawActionObjects(geogebra.common.awt.Graphics2D g);

		public void setDefRenderingHints(Graphics2D g2){
			// TODO Auto-generated method stub
		}

		protected abstract void setAntialiasing(Graphics2D g2);
		
		protected void drawAnimationButtons(Graphics2D g2) {
			// TODO Auto-generated method stub
			
		}
	
		public abstract void setBackground(Color bgColor);

		protected void synchronizeMenuBarAndEuclidianStyleBar(EuclidianSettings evs) {
			getStyleBar().updateButtonPointCapture(evs.getPointCapturingMode());
		
			if (getApplication().getGuiManager() != null) {
				getApplication().getGuiManager().updateMenubar();
			}
		}

		public abstract void setPreferredSize(Dimension preferredSize);

		public void showGrid(boolean show) {
			if (show == showGrid) {
				return;
			}
			showGrid = show;
			updateBackgroundImage();
		}

		public void setGridIsBold(boolean gridIsBold) {
			if (this.gridIsBold == gridIsBold) {
				return;
			}
		
			this.gridIsBold = gridIsBold;
			setGridLineStyle(gridLineStyle);
		
			updateBackgroundImage();
		}

		public void setGridColor(geogebra.common.awt.Color gridColor) {
			if (gridColor != null) {
				this.gridColor = gridColor;
			}
		}

		public void setGridLineStyle(int gridLineStyle) {
			this.gridLineStyle = gridLineStyle;
			gridStroke = EuclidianStatic.getStroke(gridIsBold ? 2f : 1f, gridLineStyle); // Michael
																			// Borcherds
																			// 2008-04-11
																			// added
																			// gridisbold
		}

		public void settingsChanged(AbstractSettings settings) {
			EuclidianSettings evs = (EuclidianSettings) settings;
		
			setXminObject(evs.getXminObject());
			setXmaxObject(evs.getXmaxObject());
			setYminObject(evs.getYminObject());
			setYmaxObject(evs.getYmaxObject());
		
			setBackground(evs.getBackground());
			setAxesColor(evs.getAxesColor());
			setGridColor(evs.getGridColor());
			setAxesLineStyle(evs.getAxesLineStyle());
			setGridLineStyle(evs.getGridLineStyle());
		
			double[] d = evs.getGridDistances();
			if (!evs.getAutomaticGridDistance() && (d == null)) {
				setAutomaticGridDistance(false);
			} else if (d == null) {
				setAutomaticGridDistance(true);
			} else {
				setGridDistances(d);
			}
		
			setShowAxis(0, evs.getShowAxis(0), false);
			setShowAxis(1, evs.getShowAxis(1), false);
			axesLabels = evs.getAxesLabels();
			setAxesUnitLabels(evs.getAxesUnitLabels());
		
			showAxesNumbers = evs.getShowAxisNumbers();
		
			// might be Double.NaN, handled in setAxesNumberingDistance()
			if (!evs.getAutomaticAxesNumberingDistance(0)
					&& Double.isNaN(evs.getAxisNumberingDistanceX())) {
				setAutomaticAxesNumberingDistance(false, 0);
			} else {
				setAxesNumberingDistance(evs.getAxisNumberingDistanceX(), 0);
			}
			if (!evs.getAutomaticAxesNumberingDistance(1)
					&& Double.isNaN(evs.getAxisNumberingDistanceY())) {
				setAutomaticAxesNumberingDistance(false, 1);
			} else {
				setAxesNumberingDistance(evs.getAxisNumberingDistanceY(), 1);
			}
		
			axesTickStyles[0] = evs.getAxesTickStyles()[0];
			axesTickStyles[1] = evs.getAxesTickStyles()[1];
		
			setDrawBorderAxes(evs.getDrawBorderAxes());
		
			axisCross[0] = evs.getAxesCross()[0];
			axisCross[1] = evs.getAxesCross()[1];
			positiveAxes[0] = evs.getPositiveAxes()[0];
			positiveAxes[1] = evs.getPositiveAxes()[1];
		
			geogebra.common.awt.Dimension ps = evs.getPreferredSize();
			if (ps != null) {
				setPreferredSize(ps);
			}
		
			showGrid(evs.getShowGrid());
		
			setGridIsBold(evs.getGridIsBold());
		
			setGridType(evs.getGridType());
		
			setPointCapturing(evs.getPointCapturingMode());
		
			setAllowShowMouseCoords(evs.getAllowShowMouseCoords());
		
			setAllowToolTips(evs.getAllowToolTips());
		
			synchronizeMenuBarAndEuclidianStyleBar(evs);
		
			if (evs.getXmaxObject() == null) {
				setCoordSystem(evs.getXZero(), evs.getYZero(), evs.getXscale(),
						evs.getYscale(), true);
				evs.setXminObject(xminObject, false);
				evs.setXmaxObject(xmaxObject, false);
				evs.setYminObject(yminObject, false);
				evs.setYmaxObject(ymaxObject, false);
			} else {
				updateBounds();
			}
		}

		/**
		 * sets array of GeoElements whose visual representation is inside of the
		 * given screen rectangle
		 */
		public final void setHits(Rectangle rect) {
			hits.init();
			if (rect == null) {
				return;
			}
		
			DrawableIterator it = allDrawableList.getIterator();
			while (it.hasNext()) {
				Drawable d = it.next();
				GeoElement geo = d.getGeoElement();
				if (geo.isEuclidianVisible() && d.isInside(rect)) {
					hits.add(geo);
				}
			}
		}

		public Rectangle getSelectionRectangle() {
			return selectionRectangle;
		}

		public geogebra.common.awt.GeneralPath getBoundingPath() {
			geogebra.common.awt.GeneralPath gs = AwtFactory.prototype.newGeneralPath();
			gs.moveTo(0, 0);
			gs.lineTo(getWidth(), 0);
			gs.lineTo(getWidth(), getHeight());
			gs.lineTo(0, getHeight());
			gs.lineTo(0, 0);
			gs.closePath();
			return gs;
		}

		final public void addBackgroundImage(DrawImage img) {
			bgImageList.addUnique(img);
			// drawImageList.remove(img);

			// Michael Borcherds 2008-02-29
			int layer = img.getGeoElement().getLayer();
			drawLayers[layer].remove(img);
		}

		final public void removeBackgroundImage(DrawImage img) {
			bgImageList.remove(img);
			// drawImageList.add(img);

			// Michael Borcherds 2008-02-29
			int layer = img.getGeoElement().getLayer();
			drawLayers[layer].add(img);
		}
		
		protected void resetLists() {
			DrawableMap.clear();
			stickyPointList.clear();
			allDrawableList.clear();
			bgImageList.clear();

			for (int i = 0; i <= getApplication().maxLayerUsed; i++) {
				drawLayers[i].clear(); // Michael Borcherds 2008-02-29
			}

			setToolTipText(null);
		}
		
		/**
		 * Returns the bounding box of all Drawable objects in this view in screen
		 * coordinates.
		 * @return 
		 */
		public Rectangle getBounds() {
			Rectangle result = null;
			
			DrawableIterator it = allDrawableList.getIterator();
			while (it.hasNext()) {
				Drawable d = it.next();
				Rectangle bb = d.getBounds();
				if (bb != null) {
					if (result == null) {
						result = AwtFactory.prototype.newRectangle(bb); // changed () to (bb) bugfix,
													// otherwise top-left of screen
													// is always included
					}
					// add bounding box of list element
					result.add(bb);
				}
			}

			// Cong Liu
			if (result == null) {
				result = AwtFactory.prototype.newRectangle(0, 0, 0, 0);
			}
			return result;
		}
		
		public void setPreview(Previewable p) {
			if (previewDrawable != null) {
				previewDrawable.disposePreview();
			}
			previewDrawable = p;
		}
		int widthTemp, heightTemp;
		double xminTemp, xmaxTemp, yminTemp, ymaxTemp;

		final public void setTemporaryCoordSystemForExport() {
			widthTemp = getWidth();
			heightTemp = getHeight();
			xminTemp = getXmin();
			xmaxTemp = getXmax();
			yminTemp = getYmin();
			ymaxTemp = getYmax();

			try {
				GeoPoint2 export1 = (GeoPoint2) getApplication().getKernel().lookupLabel(
						AbstractEuclidianView.EXPORT1);
				GeoPoint2 export2 = (GeoPoint2) getApplication().getKernel().lookupLabel(
						AbstractEuclidianView.EXPORT2);

				if ((export1 == null) || (export2 == null)) {
					return;
				}

				double[] xy1 = new double[2];
				double[] xy2 = new double[2];
				export1.getInhomCoords(xy1);
				export2.getInhomCoords(xy2);

				setRealWorldCoordSystem(Math.min(xy1[0], xy2[0]),
						Math.max(xy1[0], xy2[0]), Math.min(xy1[1], xy2[1]),
						Math.max(xy1[1], xy2[1]));

			} catch (Exception e) {
				restoreOldCoordSystem();
			}
		}
		
		/**
		 * Finds maximum pixel width and height needed to draw current x and y axis
		 * labels. return[0] = max width, return[1] = max height
		 * 
		 * @param g2
		 * @return point (width,height)
		 */
		
		public Point getMaximumLabelSize(geogebra.common.awt.Graphics2D g2) {

			Point max = new Point(0, 0);

			g2.setFont(getFontAxes());
			geogebra.common.awt.FontRenderContext frc = g2.getFontRenderContext();

			int yAxisHeight = positiveAxes[1] ? (int) getyZero() : getHeight();
			int maxY = positiveAxes[1] ? (int) getyZero() : getHeight() - SCREEN_BORDER;

			double rw = getYmax() - (getYmax() % axesNumberingDistances[1]);
			double pix = getyZero() - (rw * getYscale());
			double axesStep = getYscale() * axesNumberingDistances[1]; // pixelstep

			for (; pix <= yAxisHeight; rw -= axesNumberingDistances[1], pix += axesStep) {
				if (pix <= maxY) {
					if (showAxesNumbers[1]) {
						String strNum = kernel.formatPiE(rw, axesNumberFormat[1]);

						sb.setLength(0);
						sb.append(strNum);
						if ((axesUnitLabels[1] != null) && !piAxisUnit[1]) {
							sb.append(axesUnitLabels[1]);
						}

						geogebra.common.awt.font.TextLayout layout = geogebra.common.factories.AwtFactory.prototype.newTextLayout(sb.toString(), 
								getFontAxes(),frc);
						// System.out.println(layout.getAdvance() + " : " + sb);
						if (max.x < layout.getAdvance()) {
							max.x = (int) layout.getAdvance();
						}
						if(max.y==0)
							max.y = (int) layout.getAscent();
					}
				}
			}
			return max;
		}


		final public void restoreOldCoordSystem() {
			setWidth(widthTemp);
			setHeight(heightTemp);
			setRealWorldCoordSystem(xminTemp, xmaxTemp, yminTemp, ymaxTemp);
		}
		
		/**
		 * used for rescaling applets when the reset button is hit use
		 * setTemporarySize(-1, -1) to disable
		 * @param w width
		 * @param h height
		 */
		public void setTemporarySize(int w, int h) {
			setWidth(w);
			setHeight(h);
			updateSize();
		}
		
		/**
		 * change showing flag of the axis
		 * 
		 * @param axis
		 *            id of the axis
		 * @param flag
		 *            show/hide
		 * @param update
		 *            update (or not) the background image
		 */
		public void setShowAxis(int axis, boolean flag, boolean update) {
			if (flag == showAxes[axis]) {
				return;
			}

			showAxes[axis] = flag;

			if (update) {
				updateBackgroundImage();
			}

		}

		public void setShowAxes(boolean flag, boolean update) {
			setShowAxis(AXIS_X, flag, false);
			setShowAxis(AXIS_Y, flag, true);
		}
		
		/**
		 * Tells if there are any traces in the background image.
		 * 
		 * @return true if there are any traces in background
		 */
		protected boolean isTracing() {
			DrawableIterator it = allDrawableList.getIterator();
			while (it.hasNext()) {
				if (it.next().isTracing()) {
					return true;
				}
			}
			return false;
		}

		/**
		 * Tells if there are any images in the background.
		 * 
		 * @return whether there are any images in the background.
		 */
		protected boolean hasBackgroundImages() {
			return bgImageList.size() > 0;
		}
		

		final public geogebra.common.awt.Graphics2D getBackgroundGraphics() {
			return bgGraphics;
		}
		
		/**
		 * returns settings in XML format
		 * 
		 * @param sb
		 * @param asPreference
		 */
		public void getXML(StringBuilder sb, boolean asPreference) {
			sb.append("<euclidianView>\n");
			if (evNo >= 2) {
				sb.append("\t<viewNumber ");
				sb.append("viewNo=\"");
				sb.append(evNo);
				sb.append("\"");
				sb.append("/>\n");
			}

			if ((getWidth() > MIN_WIDTH) && (getHeight() > MIN_HEIGHT)) {
				sb.append("\t<size ");
				sb.append(" width=\"");
				sb.append(getWidth());
				sb.append("\"");
				sb.append(" height=\"");
				sb.append(getHeight());
				sb.append("\"");
				sb.append("/>\n");
			}
			if (!isZoomable() && !asPreference) {
				sb.append("\t<coordSystem");
				sb.append(" xMin=\"");
				sb.append(((GeoNumeric) xminObject).getLabel());
				sb.append("\"");
				sb.append(" xMax=\"");
				sb.append(((GeoNumeric) xmaxObject).getLabel());
				sb.append("\"");
				sb.append(" yMin=\"");
				sb.append(((GeoNumeric) yminObject).getLabel());
				sb.append("\"");
				sb.append(" yMax=\"");
				sb.append(((GeoNumeric) ymaxObject).getLabel());
				sb.append("\"");
				sb.append("/>\n");
			} else {
				sb.append("\t<coordSystem");
				sb.append(" xZero=\"");
				sb.append(getxZero());
				sb.append("\"");
				sb.append(" yZero=\"");
				sb.append(getyZero());
				sb.append("\"");
				sb.append(" scale=\"");
				sb.append(getXscale());
				sb.append("\"");
				sb.append(" yscale=\"");
				sb.append(getYscale());
				sb.append("\"");
				sb.append("/>\n");
			}
			// NOTE: the attribute "axes" for the visibility state of
			// both axes is no longer needed since V3.0.
			// Now there are special axis tags, see below.
			sb.append("\t<evSettings axes=\"");
			sb.append(showAxes[0] || showAxes[1]);
			sb.append("\" grid=\"");
			sb.append(showGrid);
			sb.append("\" gridIsBold=\""); //
			sb.append(gridIsBold); // Michael Borcherds 2008-04-11
			sb.append("\" pointCapturing=\"");
			sb.append(getPointCapturingMode());
			sb.append("\" rightAngleStyle=\"");
			sb.append(getApplication().rightAngleStyle);
			if (asPreference) {
				sb.append("\" allowShowMouseCoords=\"");
				sb.append(getAllowShowMouseCoords());

				sb.append("\" allowToolTips=\"");
				sb.append(getAllowToolTips());
			}

			sb.append("\" checkboxSize=\"");
			sb.append(getApplication().booleanSize); // Michael Borcherds 2008-05-12

			sb.append("\" gridType=\"");
			sb.append(getGridType()); // cartesian/isometric/polar

			sb.append("\"/>\n");

			// background color
			sb.append("\t<bgColor r=\"");
			sb.append(getBackgroundCommon().getRed());
			sb.append("\" g=\"");
			sb.append(getBackgroundCommon().getGreen());
			sb.append("\" b=\"");
			sb.append(getBackgroundCommon().getBlue());
			sb.append("\"/>\n");

			// axes color
			sb.append("\t<axesColor r=\"");
			sb.append(axesColor.getRed());
			sb.append("\" g=\"");
			sb.append(axesColor.getGreen());
			sb.append("\" b=\"");
			sb.append(axesColor.getBlue());
			sb.append("\"/>\n");

			// grid color
			sb.append("\t<gridColor r=\"");
			sb.append(gridColor.getRed());
			sb.append("\" g=\"");
			sb.append(gridColor.getGreen());
			sb.append("\" b=\"");
			sb.append(gridColor.getBlue());
			sb.append("\"/>\n");

			// axes line style
			sb.append("\t<lineStyle axes=\"");
			sb.append(axesLineType);
			sb.append("\" grid=\"");
			sb.append(gridLineStyle);
			sb.append("\"/>\n");

			// axis settings
			for (int i = 0; i < 2; i++) {
				sb.append("\t<axis id=\"");
				sb.append(i);
				sb.append("\" show=\"");
				sb.append(showAxes[i]);
				sb.append("\" label=\"");
				sb.append(axesLabels[i] == null ? "" : StringUtil
						.encodeXML(axesLabels[i]));
				sb.append("\" unitLabel=\"");
				sb.append(axesUnitLabels[i] == null ? "" : StringUtil
						.encodeXML(axesUnitLabels[i]));
				sb.append("\" tickStyle=\"");
				sb.append(axesTickStyles[i]);
				sb.append("\" showNumbers=\"");
				sb.append(showAxesNumbers[i]);

				// the tick distance should only be saved if
				// it isn't calculated automatically
				if (!automaticAxesNumberingDistances[i]) {
					sb.append("\" tickDistance=\"");
					sb.append(axesNumberingDistances[i]);
				}

				// axis crossing values
				if (drawBorderAxes[i]) {
					sb.append("\" axisCrossEdge=\"");
					sb.append(true);
				} else if (!Kernel.isZero(axisCross[i])
						&& !drawBorderAxes[i]) {
					sb.append("\" axisCross=\"");
					sb.append(axisCross[i]);
				}

				// positive direction only flags
				if (positiveAxes[i]) {
					sb.append("\" positiveAxis=\"");
					sb.append(positiveAxes[i]);
				}

				sb.append("\"/>\n");
			}

			// grid distances
			if (!automaticGridDistance || (// compatibility to v2.7:
					EuclidianStyleConstants.automaticGridDistanceFactor != EuclidianStyleConstants.DEFAULT_GRID_DIST_FACTOR)) {
				sb.append("\t<grid distX=\"");
				sb.append(gridDistances[0]);
				sb.append("\" distY=\"");
				sb.append(gridDistances[1]);
				sb.append("\" distTheta=\"");
				// polar angle step added in v4.0
				sb.append(gridDistances[2]);
				sb.append("\"/>\n");
			}

			sb.append("</euclidianView>\n");
		}
		public void drawPoints(GeoImage ge, double[] x, double[] y) {
			ArrayList<geogebra.common.awt.Point> ptList = new ArrayList<geogebra.common.awt.Point>();

			AbstractApplication.debug("x0" + x[0]);
			for (int i = 0; i < x.length; i++) {
				int xi = toScreenCoordX(x[i]);
				int yi = toScreenCoordY(y[i]);
				if (ge.getCorner(1) != null) {
					int w = ge.getFillImage().getWidth();
					int h = ge.getFillImage().getHeight();

					double cx[] = new double[3], cy[] = new double[3];
					for (int j = 0; j < (ge.getCorner(2) != null ? 3 : 2); j++) {
						cx[j] = ge.getCorner(j).x;
						cy[j] = ge.getCorner(j).y;
					}
					if (ge.getCorner(2) == null) {
						cx[2] = cx[0] - ((h * (cy[1] - cy[0])) / w);
						cy[2] = cy[0] + ((h * (cx[1] - cx[0])) / w);
					}
					double dx1 = cx[1] - cx[0];
					double dx2 = cx[2] - cx[0];
					double dy1 = cy[1] - cy[0];
					double dy2 = cy[2] - cy[0];
					double ratio1 = (((x[i] - cx[0]) * dy2) - (dx2 * (y[i] - cy[0])))
							/ ((dx1 * dy2) - (dx2 * dy1));
					double ratio2 = ((-(x[i] - cx[0]) * dy1) + (dx1 * (y[i] - cy[0])))
							/ ((dx1 * dy2) - (dx2 * dy1));
					AbstractApplication.debug(cx[2] + "," + cy[2] + "," + h + ","
							+ w);
					xi = (int) Math.round(w * ratio1);
					yi = (int) Math.round(h * (1 - ratio2));

				} else if (ge.getCorner(0) != null) {
					xi = xi - toScreenCoordX(ge.getCorner(0).x);
					yi = ge.getFillImage().getHeight()
							+ (yi - toScreenCoordY(ge.getCorner(0).y));
				}
				ptList.add(new geogebra.common.awt.Point(xi, yi));
			}
			euclidianController.getPen().doDrawPoints(ge, ptList);

		}
		
		public void resetMaxLayerUsed() {
			getApplication().maxLayerUsed = 0;
		}

		public void resetXYMinMaxObjects() {
			if ((evNo == 1) || (evNo == 2)) {
				EuclidianSettings es = getApplication().getSettings().getEuclidian(evNo);
				// this is necessary in File->New because there might have been
				// dynamic xmin bounds
				GeoNumeric xmao = new GeoNumeric(kernel.getConstruction(),
						xmaxObject.getNumber().getDouble());
				GeoNumeric xmio = new GeoNumeric(kernel.getConstruction(),
						xminObject.getNumber().getDouble());
				GeoNumeric ymao = new GeoNumeric(kernel.getConstruction(),
						ymaxObject.getNumber().getDouble());
				GeoNumeric ymio = new GeoNumeric(kernel.getConstruction(),
						yminObject.getNumber().getDouble());
				es.setXmaxObject(xmao, false);
				es.setXminObject(xmio, false);
				es.setYmaxObject(ymao, false);
				es.setYminObject(ymio, true);
			}
		}
		
		public final void setViewShowAllObjects(boolean storeUndo) {

			double x0RW = getXmin();
			double x1RW;
			double y0RW;
			double y1RW;
			double y0RWfunctions = 0;
			double y1RWfunctions = 0;
			double factor = 0.03d; // don't want objects at edge
			double xGap = 0;

			TreeSet<GeoElement> allFunctions = kernel.getConstruction()
					.getGeoSetLabelOrder(GeoClass.FUNCTION);

			int noVisible = 0;
			// count no of visible functions
			Iterator<GeoElement> it = allFunctions.iterator();
			while (it.hasNext()) {
				if (((GeoFunction) (it.next())).isEuclidianVisible()) {
					noVisible++;
				}
			}
			;

			Rectangle rect = getBounds();
			if (Kernel.isZero(rect.getHeight())
					|| Kernel.isZero(rect.getWidth())) {
				if (noVisible == 0) {
					return; // no functions or objects
				}

				// just functions
				x0RW = Double.MAX_VALUE;
				x1RW = -Double.MAX_VALUE;
				y0RW = Double.MAX_VALUE;
				y1RW = -Double.MAX_VALUE;

				// Application.debug("just functions");

			} else {

				// get bounds of points, circles etc
				x0RW = toRealWorldCoordX(rect.getMinX());
				x1RW = toRealWorldCoordX(rect.getMaxX());
				y0RW = toRealWorldCoordY(rect.getMaxY());
				y1RW = toRealWorldCoordY(rect.getMinY());
			}

			xGap = (x1RW - x0RW) * factor;

			boolean ok = false;

			if (noVisible != 0) {

				// if there are functions we don't want to zoom in horizintally
				x0RW = Math.min(getXmin(), x0RW);
				x1RW = Math.max(getXmax(), x1RW);

				if (Kernel.isEqual(x0RW, getXmin())
						&& Kernel.isEqual(x1RW, getXmax())) {
					// just functions (at sides!), don't need a gap
					xGap = 0;
				} else {
					xGap = (x1RW - x0RW) * factor;
				}

				// Application.debug("checking functions from "+x0RW+" to "+x1RW);

				y0RWfunctions = Double.MAX_VALUE;
				y1RWfunctions = -Double.MAX_VALUE;

				it = allFunctions.iterator();

				while (it.hasNext()) {
					GeoFunction fun = (GeoFunction) (it.next());
					double abscissa;
					// check 100 random heights
					for (int i = 0; i < 200; i++) {

						if (i == 0) {
							abscissa = fun.evaluate(x0RW); // check far left
						} else if (i == 1) {
							abscissa = fun.evaluate(x1RW); // check far right
						} else {
							abscissa = fun.evaluate(x0RW
									+ (Math.random() * (x1RW - x0RW)));
						}

						if (!Double.isInfinite(abscissa) && !Double.isNaN(abscissa)) {
							ok = true;
							if (abscissa > y1RWfunctions) {
								y1RWfunctions = abscissa;
							}
							// no else: there **might** be just one value
							if (abscissa < y0RWfunctions) {
								y0RWfunctions = abscissa;
							}
						}
					}
				}

			}

			if (!Kernel.isZero(y1RWfunctions - y0RWfunctions) && ok) {
				y0RW = Math.min(y0RW, y0RWfunctions);
				y1RW = Math.max(y1RW, y1RWfunctions);
				// Application.debug("min height "+y0RW+" max height "+y1RW);
			}

			// don't want objects at edge
			double yGap = (y1RW - y0RW) * factor;

			final double x0RW2 = x0RW - xGap;
			final double x1RW2 = x1RW + xGap;
			final double y0RW2 = y0RW - yGap;
			final double y1RW2 = y1RW + yGap;

			setAnimatedRealWorldCoordSystem(x0RW2, x1RW2, y0RW2, y1RW2, 10,
					storeUndo);

		}
		
		public int getSelectedWidth() {
			if (selectionRectangle == null) {
				return getWidth();
			} else {
				return (int)selectionRectangle.getWidth();
			}
		}

		public int getSelectedHeight() {
			if (selectionRectangle == null) {
				return getHeight();
			} else {
				return (int)selectionRectangle.getHeight();
			}
		}

		public int getExportWidth() {
			if (selectionRectangle != null) {
				return (int)selectionRectangle.getWidth();
			}
			try {
				GeoPoint2 export1 = (GeoPoint2) kernel.lookupLabel(EXPORT1);
				GeoPoint2 export2 = (GeoPoint2) kernel.lookupLabel(EXPORT2);
				double[] xy1 = new double[2];
				double[] xy2 = new double[2];
				export1.getInhomCoords(xy1);
				export2.getInhomCoords(xy2);
				double x1 = xy1[0];
				double x2 = xy2[0];
				x1 = (x1 / getInvXscale()) + getxZero();
				x2 = (x2 / getInvXscale()) + getxZero();

				return (int) Math.abs(x1 - x2) + 2;
			} catch (Exception e) {
				return getWidth();
			}

		}

		public int getExportHeight() {
			if (selectionRectangle != null) {
				return (int)selectionRectangle.getHeight();
			}

			try {
				GeoPoint2 export1 = (GeoPoint2) kernel.lookupLabel(EXPORT1);
				GeoPoint2 export2 = (GeoPoint2) kernel.lookupLabel(EXPORT2);
				double[] xy1 = new double[2];
				double[] xy2 = new double[2];
				export1.getInhomCoords(xy1);
				export2.getInhomCoords(xy2);
				double y1 = xy1[1];
				double y2 = xy2[1];
				y1 = getyZero() - (y1 / getInvYscale());
				y2 = getyZero() - (y2 / getInvYscale());

				return (int) Math.abs(y1 - y2) + 2;
			} catch (Exception e) {
				return getHeight();
			}

		}
		
	protected Hits tempArrayList = new Hits();

		// for use in AlgebraController
	final public void clickedGeo(GeoElement geo, geogebra.common.euclidian.event.AbstractEvent event) {
		if (geo == null) {
			return;
		}

		tempArrayList.clear();
		tempArrayList.add(geo);
		
		boolean changedKernel = euclidianController.processMode(tempArrayList,
				event);
		if (changedKernel) {
			getApplication().storeUndoInfo();
		}
		kernel.notifyRepaint();
	}
}
