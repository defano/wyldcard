/**
 * HtSemanticException.java
 * @author matt.defano@gmail.com
 * 
 * Exception representing a HyperTalk syntax, semantic or well-formedness error
 */

package hypertalk.exception;

public class HtSemanticException extends HtException {
	public HtSemanticException(String message) {
		super(message);
	}
}
