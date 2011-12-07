package geogebra.common.kernel.geos;

/**
 * Represents geos that can be mirrored atline or point
 * 
 */
public interface Mirrorable {
	/**
	 * Miror at point
	 * @param Q mirror
	 */
	public void mirror(GeoPointInterface Q);
	/**
	 * Miror at line
	 * @param g mirror
	 */
	public void mirror(GeoLineInterface g);
	/**
	 * Returns resulting element
	 * @return resulting element
	 */
	public GeoElement toGeoElement();
}