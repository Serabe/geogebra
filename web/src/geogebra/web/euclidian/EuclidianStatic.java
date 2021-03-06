package geogebra.web.euclidian;

import geogebra.common.awt.Color;
import geogebra.common.awt.Font;
import geogebra.common.awt.Graphics2D;
import geogebra.common.awt.Rectangle;
import geogebra.common.awt.Shape;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.main.AbstractApplication;
import geogebra.web.awt.FontRenderContext;

public class EuclidianStatic extends geogebra.common.euclidian.EuclidianStatic {

	@Override
	protected Rectangle doDrawMultilineLaTeX(AbstractApplication app,
	        Graphics2D tempGraphics, GeoElement geo, Graphics2D g2, Font font,
	        Color fgColor, Color bgColor, String labelDesc, int xLabel,
	        int yLabel, boolean serif) {
		AbstractApplication.debug("implementation needed"); // TODO Auto-generated
		return new geogebra.web.awt.Rectangle(xLabel, yLabel, 50, 50);
	}

	private static Font getIndexFont(Font f) {
		// index font size should be at least 8pt
		int newSize = Math.max((int) (f.getSize() * 0.9), 8);
		return f.deriveFont(f.getStyle(), newSize);
	}

	
	@Override
	protected void doFillWithValueStrokePure(Shape shape, Graphics2D g2) {
		g2.fill(shape);
		AbstractApplication.debug("implementation needed"); // TODO Auto-generated

	}

	@Override
	protected void doDrawWithValueStrokePure(Shape shape, Graphics2D g2) {
		g2.draw(shape);
		// TODO can we emulate somehow the "pure stroke" behavior?

	}

	@Override
    protected Object doSetInterpolationHint(Graphics2D g3,
            boolean needsInterpolationRenderingHint) {
	    // TODO Auto-generated method stub
	    return null;
    }

	@Override
    protected void doResetInterpolationHint(Graphics2D g3, Object hint) {
	    // TODO Auto-generated method stub
	    
    }

}
