package geogebra.common.awt;


public abstract class GeneralPath implements geogebra.common.awt.Shape {

	public abstract void moveTo(float f, float g);

	public abstract void reset();

	public abstract void lineTo(float f, float g);

	public abstract void closePath();

	public abstract Shape createTransformedShape(AffineTransform affineTransform);

	public abstract Point2D getCurrentPoint();
}