package geogebra3D.gui.layout.panels;

import javax.swing.JComponent;

import geogebra.euclidian.EuclidianViewInterface;
import geogebra.gui.layout.panels.EuclidianDockPanelAbstract;
import geogebra.gui.toolbar.Toolbar;
import geogebra.main.Application;

import geogebra3D.Application3D;
import geogebra3D.euclidianForPlane.EuclidianViewForPlane;

/**
 * Dock panel for the primary euclidian view.
 */
public class EuclidianDockPanelForPlane extends EuclidianDockPanelAbstract {
	private static final long serialVersionUID = 1L;
	private Application app;
	private EuclidianViewForPlane view;
	
	/**
	 * @param app
	 */
	public EuclidianDockPanelForPlane(Application app, EuclidianViewForPlane view) {
		super(
			Application.VIEW_EUCLIDIAN_FOR_PLANE,	// view id 
			"GraphicsViewForPlaneA", 				// view title
			Toolbar.getAllToolsNoMacrosForPlane(),// toolbar string
			true,						// style bar?
			-1,							// menu order
			'P'
		);
		
		this.app = app;
		this.view = view;
	}
	
	@Override
	protected String getPlainTitle(){
		return app.getPlain(getViewTitle(),view.getTranslatedFromPlaneString());
	}

	@Override
	protected JComponent loadComponent() {
		return view.getJPanel();
	}
	
	@Override
	protected JComponent loadStyleBar() {
		return (JComponent) view.getStyleBar();
	}
	
	@Override
	public EuclidianViewInterface getEuclidianView() {
		return view;
	}
}
