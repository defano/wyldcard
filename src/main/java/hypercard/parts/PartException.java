/**
 * PartException.java
 * @author matt.defano@gmail.com
 * 
 * Exception to be thrown when a request is made for a part that doesn't exist
 * or otherwise cannot handle the request.
 */

package hypercard.parts;

import hypertalk.exception.HtException;

public class PartException extends HtException {
private static final long serialVersionUID = 5837496988130817034L;

	public PartException(String message) {
		super(message);
	}
}
