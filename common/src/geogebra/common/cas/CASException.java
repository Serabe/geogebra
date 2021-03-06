package geogebra.common.cas;

//TODO add import for GeoGebraCAS as soon as it is in common
/**
 * Base class for all CAS exceptions.
 * All exceptions the CAS throws should be of this type (unless you want
 * to use {@link GeoGebraCAS#evaluateRaw(String)}.
 * All CAS exceptions have a translation key that is used to translate
 * the exception into a user-visible error message.
 * 
 * @author Thomas
 *
 */
public class CASException extends RuntimeException {

	private static final long serialVersionUID = 1L;
	private String key;

	public CASException(String message)
	{
		super(message);
	}
	
	public CASException(Throwable t)
	{
		super(t.getMessage());
	}
	
	/**
	 * Returns the Key for this Exception, which can also be used for translation.
	 * @return The error key.
	 */
	public String getKey()
	{
		if(key != null) {
			return key;
		}
		return "CAS.GeneralErrorMessage";
	}

	/**
	 * @param key the key to set
	 */
	public void setKey(String key) {
		this.key = key;
	}
}
