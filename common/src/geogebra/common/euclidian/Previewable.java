package geogebra.common.euclidian;

/**
 * @author Markus Hohenwarter
 */
public interface Previewable {
	
	public void updatePreview(); 
	public void updateMousePos(double x, double y);
	public void drawPreview(geogebra.common.awt.Graphics2D g2);
	public void disposePreview();
	
}
