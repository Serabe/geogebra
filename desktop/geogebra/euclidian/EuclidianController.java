/* 
 GeoGebra - Dynamic Mathematics for Everyone
 http://www.geogebra.org

 This file is part of GeoGebra.

 This program is free software; you can redistribute it and/or modify it 
 under the terms of the GNU General Public License as published by 
 the Free Software Foundation.

 */

package geogebra.euclidian;

import geogebra.common.euclidian.AbstractEuclidianView;
import geogebra.common.euclidian.EuclidianConstants;
import geogebra.common.euclidian.EuclidianViewInterfaceCommon;
import geogebra.common.euclidian.event.AbstractEvent;
import geogebra.common.kernel.Kernel;
import geogebra.common.kernel.Macro;
import geogebra.common.kernel.arithmetic.MyDouble;
import geogebra.common.kernel.arithmetic.VectorValue;
import geogebra.common.kernel.geos.GeoConicPart;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.kernelND.GeoPointND;
import geogebra.common.main.AbstractApplication;
import geogebra.main.Application;

import java.awt.Color;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.util.ArrayList;
import java.util.Locale;

import javax.swing.JComponent;
import javax.swing.ToolTipManager;
import javax.swing.text.JTextComponent;

/**
 * EuclidianController.java
 * 
 * Created on 16. October 2001, 15:41
 */
public class EuclidianController extends geogebra.common.euclidian.AbstractEuclidianController implements MouseListener, MouseMotionListener,
		MouseWheelListener, ComponentListener, PropertiesPanelMiniListener {

	

	
	

	// protected GeoVec2D b;

	

	// protected GeoSegment movedGeoSegment;

	

	// protected MyPopupMenu popupMenu;

	

	// boolean polygonRigid = false;

	/***********************************************
	 * Creates new EuclidianController
	 **********************************************/
	public EuclidianController(Kernel kernel) {
		setKernel(kernel);
		setApplication(kernel.getApplication());

		// for tooltip manager
		DEFAULT_INITIAL_DELAY = ToolTipManager.sharedInstance()
				.getInitialDelay();

		tempNum = new MyDouble(kernel);
	}

	@Override
	public void setApplication(AbstractApplication app) {
		this.app = app;
	}

	public Application getApplication() {
		return (Application) app;
	}

	protected void setView(EuclidianViewInterface view) {
		// void setView(EuclidianView view) {
		this.view = view;
	}


	public void setPen(EuclidianPen pen) {
		this.pen = pen;
	}

	public EuclidianPen getPen() {
		return (EuclidianPen) pen;
	}

	public void mouseClicked(MouseEvent e) {
		AbstractEvent event = geogebra.euclidian.event.MouseEvent.wrapEvent(e);
		wrapMouseclicked(event);
	}

	public void mousePressed(MouseEvent e) {
		AbstractEvent event = geogebra.euclidian.event.MouseEvent.wrapEvent(e);
		wrapMousePressed(event);
	}

	private EuclidianViewInterfaceCommon setShowMouseCoords(boolean b) {
		// TODO Auto-generated method stub
		return null;
	}

	public void mouseDragged(MouseEvent e) {
		AbstractEvent event = geogebra.euclidian.event.MouseEvent.wrapEvent(e);
		wrapMouseDragged(event);
	}

	public void mouseReleased(MouseEvent e) {
		AbstractEvent event = geogebra.euclidian.event.MouseEvent.wrapEvent(e);
		wrapMouseReleased(event);
	}

	public void mouseMoved(MouseEvent e) {
		AbstractEvent event = geogebra.euclidian.event.MouseEvent.wrapEvent(e);
		wrapMouseMoved(event);
	}
	
	public void mouseEntered(MouseEvent e) {
		AbstractEvent event = geogebra.euclidian.event.MouseEvent.wrapEvent(e);
		wrapMouseEntered(event);
	}
	
	public void mouseExited(MouseEvent e) {
		AbstractEvent event = geogebra.euclidian.event.MouseEvent.wrapEvent(e);
		wrapMouseExited(event);
	}

	/*
	 * public void focusGained(FocusEvent e) { initToolTipManager(); }
	 * 
	 * public void focusLost(FocusEvent e) { resetToolTipManager(); }
	 */

	@Override
	public void initToolTipManager() {
		// set tooltip manager
		ToolTipManager ttm = ToolTipManager.sharedInstance();
		ttm.setInitialDelay(DEFAULT_INITIAL_DELAY / 2);
		ttm.setEnabled(((Application) app).getAllowToolTips());
	}

	@Override
	public void resetToolTipManager() {
		ToolTipManager ttm = ToolTipManager.sharedInstance();
		ttm.setInitialDelay(DEFAULT_INITIAL_DELAY);
	}

	/* ****************************************************** */

	

	

	

	/*
	 * final protected void transformCoords(boolean usePointCapturing) { // calc
	 * real world coords calcRWcoords();
	 * 
	 * if (usePointCapturing) { double pointCapturingPercentage = 1; switch
	 * (view.getPointCapturingMode()) { case
	 * EuclidianConstants.POINT_CAPTURING_AUTOMATIC: if
	 * (!view.isGridOrAxesShown())break;
	 * 
	 * case EuclidianView.POINT_CAPTURING_ON: pointCapturingPercentage = 0.125;
	 * 
	 * case EuclidianView.POINT_CAPTURING_ON_GRID: // X = (x, y) ... next grid
	 * point double x = Kernel.roundToScale(xRW, view.gridDistances[0]); double
	 * y = Kernel.roundToScale(yRW, view.gridDistances[1]); // if |X - XRW| <
	 * gridInterval * pointCapturingPercentage then take the grid point double a
	 * = Math.abs(x - xRW); double b = Math.abs(y - yRW); if (a <
	 * view.gridDistances[0] * pointCapturingPercentage && b <
	 * view.gridDistances[1] * pointCapturingPercentage) { xRW = x; yRW = y;
	 * mouseLoc.x = view.toScreenCoordX(xRW); mouseLoc.y =
	 * view.toScreenCoordY(yRW); }
	 * 
	 * default: // point capturing off } } }
	 */

	

	

	

	

	

	// fetch the two selected points
	/*
	 * protected void join(){ GeoPoint[] points = getSelectedPoints(); GeoLine
	 * line = kernel.Line(null, points[0], points[1]); }
	 */

	@Override
	public GeoElement[] createCircle2ForPoints3D(GeoPointND p0, GeoPointND p1) {
		return new GeoElement[] { kernel.getManager3D().Circle3D(null, p0, p1,
				((AbstractEuclidianView) view).getDirection()) };
	}

	public void componentResized(ComponentEvent e) {
		// tell the view that it was resized
		((EuclidianViewInterface) view).updateSize();
	}

	public void componentShown(ComponentEvent e) {
	}

	public void componentHidden(ComponentEvent e) {
	}

	public void componentMoved(ComponentEvent e) {
	}
	
	/**
	 * Zooms in or out using mouse wheel
	 */
	public void mouseWheelMoved(MouseWheelEvent e) {
		AbstractEvent event = geogebra.euclidian.event.MouseEvent.wrapEvent(e);
		wrapMouseWheelMoved(event);
	}

	public void zoomInOut(KeyEvent event) {
		boolean allowZoom = !((Application)app).isApplet()
				|| (mode == EuclidianConstants.MODE_ZOOM_IN)
				|| (mode == EuclidianConstants.MODE_ZOOM_OUT)
				|| (((Application) app).isShiftDragZoomEnabled());
		if (!allowZoom) {
			return;
		}
		double px, py;
		if (mouseLoc != null) {
			px = mouseLoc.x;
			py = mouseLoc.y;
		} else {
			px = view.getWidth() / 2;
			py = view.getHeight() / 2;
		}

		double factor = event.getKeyCode() == KeyEvent.VK_MINUS ? 1d / AbstractEuclidianView.MOUSE_WHEEL_ZOOM_FACTOR
				: AbstractEuclidianView.MOUSE_WHEEL_ZOOM_FACTOR;

		// accelerated zoom
		if (event.isAltDown()) {
			factor *= event.getKeyCode() == KeyEvent.VK_MINUS ? 2d / 3d : 1.5;
		}

		// make zooming a little bit smoother by having some steps
		((EuclidianViewInterface) view).setAnimatedCoordSystem(
		// px + dx * factor,
		// py + dy * factor,
				px, py, factor, view.getXscale() * factor, 4, false);
		// view.yscale * factor);
		((Application)app).setUnsaved();

	}

	

	// /////////////////////////////////////////
	// moved GeoElements

	

	

	// /////////////////////////////////////////
	// EMPTY METHODS USED FOR EuclidianView3D

	// ******************************
	// PropertiesPanelMini Listeners
	// ******************************
	int penLineStyle = 0;

	public void setLineStyle(int lineStyle) {
		penLineStyle = lineStyle;

		// if (mode == EuclidianView.MODE_VISUAL_STYLE) {
		ArrayList<GeoElement> geos = ((Application)app).getSelectedGeos();

		for (int i = 0; i < geos.size(); i++) {
			GeoElement geo = geos.get(i);
			geo.setLineType(lineStyle);
			geo.updateRepaint();

		}
		// }
	}

	Color penColor = Color.black;

	public void setColor(Color color) {
		penColor = color;

		// if (mode == EuclidianView.MODE_VISUAL_STYLE) {
		ArrayList<GeoElement> geos = ((Application)app).getSelectedGeos();

		for (int i = 0; i < geos.size(); i++) {
			GeoElement geo = geos.get(i);
			geo.setObjColor(new geogebra.awt.Color(color));
			geo.updateRepaint();
		}
		// }
	}

}
