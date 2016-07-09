/**
 * HtSemanticException.java
 * @author matt.defano@gmail.com
 * 
 * Exception representing a HyperTalk syntax, semantic or well-formedness error
 */

package hypertalk.exception;

public class HtSemanticException extends HtException {
private static final long serialVersionUID = 4663221245276673402L;

	public HtSemanticException(String message) {
		super(message);
	}
}
