/**
 * NoSuchPropertyException.java
 * @author matt.defano@gmail.com
 * 
 * Exception thrown when getting or setting a property that does not exist
 * for the part in which it was requested.
 */

package hypertalk.exception;

public class NoSuchPropertyException extends Exception {
private static final long serialVersionUID = -757170688052055290L;

	public NoSuchPropertyException(String message) {
		super(message);
	}
}
