package geogebra.awt;

public class Dimension extends geogebra.common.awt.Dimension {
	private java.awt.Dimension impl;
	public Dimension(int a,int b){
		impl = new java.awt.Dimension(a,b);
	}
	@Override
	public double getWidth() {
		return impl.width;
	}

	@Override
	public double getHeight() {
		// TODO Auto-generated method stub
		return impl.height;
	}
	
	/**
	 * @param d dimension, must be of the type geogebra.awt.Dimension
	 * @return AWT implementation wrapped in d
	 */
	public static java.awt.Dimension getAWTDimension(geogebra.common.awt.Dimension d){
		if(!(d instanceof Dimension))
			return null;
		return ((Dimension)d).impl;
	}

}