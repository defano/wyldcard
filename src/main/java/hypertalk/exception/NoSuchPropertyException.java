/**
 * NoSuchPropertyException.java
 * @author matt.defano@gmail.com
 * 
 * Exception thrown when getting or setting a property that does not exist
 * for the part in which it was requested.
 */

package hypertalk.exception;

public class NoSuchPropertyException extends HtException {
    public NoSuchPropertyException(String message) {
        super(message);
    }
}
