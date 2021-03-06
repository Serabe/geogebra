package geogebra.common.euclidian;

import geogebra.common.awt.Rectangle;
import geogebra.common.euclidian.clipping.ClipLine;
import geogebra.common.factories.AwtFactory;

//import java.awt.Shape;
import java.util.ArrayList;

/**
 * A GeneralPath implementation that does clipping of line segments at the
 * screen in double coordinates. This is important to avoid rendering problems
 * that occur with GeneralPath when coordinates are larger than Float.MAX_VALUE.
 * 
 * @author Markus Hohenwarter
 * @version October 2009
 */
public class GeneralPathClipped  implements geogebra.common.awt.Shape {

	private static final float MAX_COORD_VALUE = 10000;
	private static final double TOLERANCE = 0.01; // pixel distance for equal
													// points

	private ArrayList<PathPoint> pathPoints;
	private geogebra.common.awt.GeneralPath gp;
	private EuclidianViewInterfaceSlim view;
	private double largestCoord;
	private boolean needClosePath;
	private geogebra.common.awt.Rectangle bounds;
	

	public GeneralPathClipped(EuclidianViewInterfaceSlim view) {
		//this.view = (EuclidianView)view;
		this.view = view;
		pathPoints = new ArrayList<PathPoint>();
		gp = AwtFactory.prototype.newGeneralPath();
		// bounds = new Rectangle();
		reset();
	}

	final public void reset() {
		pathPoints.clear();
		gp.reset();
		// bounds.setBounds(0,0,0,0);
		bounds = null;
		largestCoord = 0;
		needClosePath = false;
	}

	final public void closePath() {
		needClosePath = true;
	}

	public geogebra.common.awt.GeneralPath getGeneralPath() {
		if (pathPoints.size() == 0)
			return gp;

		gp.reset();
		if (largestCoord < MAX_COORD_VALUE) {
			addSimpleSegments();
		} else {
			addClippedSegments();
		}

		// clear pathPoints to free up memory
		pathPoints.clear();

		return gp;
	}

	private void addSimpleSegments() {
		int size = pathPoints.size();
		for (int i = 0; i < size; i++) {
			PathPoint curP = pathPoints.get(i);
			addToGeneralPath(curP, curP.getLineTo());
		}
		if (needClosePath)
			gp.closePath();
	}

	/**
	 * Clip all segments at screen to make sure we don't have to render huge
	 * coordinates. This is especially important for fill the GeneralPath.
	 */
	private void addClippedSegments() {
		geogebra.common.awt.Rectangle viewRect = AwtFactory.prototype.newRectangle(0, 0, view.getWidth(), view.getHeight());
		PathPoint curP = null, prevP;

		int size = pathPoints.size();
		for (int i = 0; i < size; i++) {
			prevP = curP;
			curP = pathPoints.get(i);
			if (!curP.getLineTo() || prevP == null) {
				// moveTo point, make sure it is only slightly outside screen
				geogebra.common.awt.Point2D p = getPointCloseToScreen(curP.getX(), curP.getY());
				addToGeneralPath(p, false);
			} else {
				// clip line at screen
				addClippedLine(prevP, curP, viewRect);
			}
		}

		if (needClosePath) {
			// line from last point to first point
			addClippedLine(curP, pathPoints.get(0), viewRect);
			gp.closePath();
		}
	}

	private void addClippedLine(PathPoint prevP, PathPoint curP,
			geogebra.common.awt.Rectangle viewRect) {
		// check if both points on screen
		if (viewRect.contains(prevP) && viewRect.contains(curP)) {
			// draw line to point
			addToGeneralPath(curP, true);
			return;
		}

		// at least one point is not on screen: clip line at screen
		geogebra.common.awt.Point2D[] clippedPoints = ClipLine.getClipped(prevP.getX(), prevP.getY(),
				curP.getX(), curP.getY(), -10, view.getWidth() + 10, -10, view.getHeight() + 10);

		if (clippedPoints != null) {
			// we have two intersection points with the screen
			// get closest clip point to prevP
			int first = 0;
			int second = 1;
			if (clippedPoints[first].distance(prevP.getX(), prevP.getY()) > clippedPoints[second]
					.distance(prevP.getX(), prevP.getY())) {
				first = 1;
				second = 0;
			}

			// draw line to first clip point
			addToGeneralPath(clippedPoints[first], true);
			// draw line between clip points: this ensures high quality
			// rendering
			// which Java2D doesn't deliver with the regular float GeneralPath
			// and huge coords
			addToGeneralPath(clippedPoints[second], true);

			// draw line to end point if not already there
			addToGeneralPath(getPointCloseToScreen(curP.getX(), curP.getY()), true);
		} else {
			// line is off screen
			// draw line to off screen end point
			addToGeneralPath(getPointCloseToScreen(curP.getX(), curP.getY()), true);
		}
	}

	private geogebra.common.awt.Point2D getPointCloseToScreen(double x, double y) {
		double border = 10;
		double right = view.getWidth() + border;
		double bottom = view.getHeight() + border;
		if (x > right) {
			x = right;
		} else if (x < -border) {
			x = -border;
		}
		if (y > bottom) {
			y = bottom;
		} else if (y < -border) {
			y = -border;
		}
		return AwtFactory.prototype.newPoint2D(x, y);
	}

	private void addToGeneralPath(geogebra.common.awt.Point2D q, boolean lineTo) {
		geogebra.common.awt.Point2D p = gp.getCurrentPoint();
		if (p != null && p.distance(q) < TOLERANCE) {
			return;
		}

		if (lineTo && p != null) {
			try {
				gp.lineTo((float) q.getX(), (float) q.getY());
			} catch (Exception e) {
				gp.moveTo((float) q.getX(), (float) q.getY());
			}
		} else {
			gp.moveTo((float) q.getX(), (float) q.getY());
		}
	}

	/**
	 * Move to (x,y).
	 */
	final public void moveTo(double x, double y) {
		addPoint(x, y, false);
	}

	/**
	 * Line to (x,y).
	 */
	final public void lineTo(double x, double y) {
		addPoint(x, y, true);
	}

	/**
	 * Adds point to point list and keeps track of largest coordinate.
	 */
	final public void addPoint(int pos, double x, double y) {
		PathPoint p = new PathPoint(x, y, true);
		updateBounds(p);
		pathPoints.ensureCapacity(pos + 1);
		while (pathPoints.size() <= pos) {
			pathPoints.add(null);
		}
		pathPoints.set(pos, p);
	}

	/**
	 * Adds point to point list and keeps track of largest coordinate.
	 */
	private void addPoint(double x, double y, boolean lineTo) {
		PathPoint p = new PathPoint(x, y, lineTo);
		updateBounds(p);
		pathPoints.add(p);
	}

	private void updateBounds(PathPoint p) {

		if (bounds == null) {
			bounds = AwtFactory.prototype.newRectangle();
			bounds.setBounds((int) p.getX(), (int) p.getY(), 0, 0);
		}

		if (Math.abs(p.getX()) > largestCoord)
			largestCoord = Math.abs(p.getX());
		if (Math.abs(p.getY()) > largestCoord)
			largestCoord = Math.abs(p.getY());

		bounds.add(p.getX(), p.getY());
	}

	public geogebra.common.awt.Point2D getCurrentPoint() {
		if (pathPoints.size() == 0) {
			return null;
		}
		return pathPoints.get(pathPoints.size() - 1);
	}

	public void transform(geogebra.common.awt.AffineTransform af) {
		int size = pathPoints.size();
		for (int i = 0; i < size; i++) {
			PathPoint p = pathPoints.get(i);
			af.transform(p, p);
		}
	}

	public boolean contains(geogebra.common.awt.Point2D p) {
		return getGeneralPath().contains(p);
	}

	public boolean contains(geogebra.common.awt.Rectangle2D p) {
		return getGeneralPath().contains(p);
	}

	public boolean contains(double arg0, double arg1) {
		return getGeneralPath().contains(arg0, arg1);
	}

	public boolean contains(double arg0, double arg1, double arg2, double arg3) {
		return getGeneralPath().contains(arg0, arg1, arg2, arg3);
	}

	public boolean contains(int x, int y) {
		// TODO Auto-generated method stub
		return getGeneralPath().contains(x, y);
	}

	public boolean contains(Rectangle rectangle) {
		// TODO Auto-generated method stub
		return getGeneralPath().contains(rectangle);
	}
	
	public geogebra.common.awt.Rectangle getBounds() {
		return bounds;
	}

	public geogebra.common.awt.Rectangle2D getBounds2D() {
		return bounds;
	}

	/*
	public PathIterator getPathIterator(AffineTransform arg0) {
		return geogebra.awt.GeneralPath.getAwtGeneralPath(getGeneralPath()).getPathIterator(arg0);
	}*/

	public geogebra.common.awt.PathIterator getPathIterator(
			geogebra.common.awt.AffineTransform arg0) {
		// TODO Auto-generated method stub
		return getGeneralPath().getPathIterator(arg0);
	}

	/*
	public PathIterator getPathIterator(AffineTransform at, double flatness) {
		return geogebra.awt.GeneralPath.getAwtGeneralPath(getGeneralPath()).getPathIterator(at, flatness);
	}*/
	
	public geogebra.common.awt.PathIterator getPathIterator(
			geogebra.common.awt.AffineTransform at, double flatness) {
		// TODO Auto-generated method stub
		return getGeneralPath().getPathIterator(at, flatness);
	}

	public boolean intersects(geogebra.common.awt.Rectangle2D arg0) {
		return getGeneralPath().intersects(arg0);
	}

	
	public boolean intersects(double arg0, double arg1, double arg2, double arg3) {
		return getGeneralPath().intersects(arg0, arg1, arg2, arg3);
	}
	
	
	public boolean intersects(int i, int j, int k, int l) {
		return getGeneralPath().intersects(i,j,k,l);
	}

	/*
	public Shape getAwtShape() {
		return geogebra.awt.GeneralPath.getAwtGeneralPath(getGeneralPath());
	}
	*/

}
