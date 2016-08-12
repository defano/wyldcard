/**
 * PropertyPermissionException.java
 * @author matt.defano@gmail.com
 * 
 * Thrown when attempting to set a read-only property (such as id) on a part
 */

package hypertalk.exception;

public class PropertyPermissionException extends HtException {

    public PropertyPermissionException (String message) {
        super(message);
    }
}
