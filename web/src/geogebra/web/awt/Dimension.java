package geogebra.web.awt;

public class Dimension extends geogebra.common.awt.Dimension {

	private geogebra.web.kernel.gawt.Dimension impl;
	
	public Dimension(geogebra.web.kernel.gawt.Dimension dim) {
		impl = dim;
	}
	
	public Dimension(int w, int h) {
		impl = new geogebra.web.kernel.gawt.Dimension(w, h);
	}
	@Override
	public int getWidth() {
		return impl.getWidth();
	}

	@Override
	public int getHeight() {
		return impl.getHeight();
	}

}
