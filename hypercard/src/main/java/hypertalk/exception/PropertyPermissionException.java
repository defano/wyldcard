/**
 * PropertyPermissionException.java
 * @author matt.defano@gmail.com
 * 
 * Thrown when attempting to set a read-only property (such as id) on a part
 */

package hypertalk.exception;

public class PropertyPermissionException extends Exception {
private static final long serialVersionUID = -6377806818401606350L;

	public PropertyPermissionException (String message) {
		super(message);
	}
}
