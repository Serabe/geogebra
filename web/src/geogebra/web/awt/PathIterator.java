package geogebra.web.awt;

public class PathIterator implements geogebra.common.awt.PathIterator {

	geogebra.web.kernel.gawt.PathIterator impl;
	
	public PathIterator(geogebra.web.kernel.gawt.PathIterator pathIterator) {
	    impl = pathIterator;
    }

	public int getWindingRule() {
		return impl.getWindingRule();
	}

	public boolean isDone() {
		return impl.isDone();
	}

	public void next() {
		impl.next();
	}

	public int currentSegment(float[] coords) {
		return impl.currentSegment(coords);
	}

	public int currentSegment(double[] coords) {
		return impl.currentSegment(coords);
	}

}
