/**
 * HtSyntaxException.java
 * @author matt.defano@gmail.com
 * 
 * Exception representing a HyperTalk syntax, semantic or well-formedness error
 */

package hypertalk.exception;

public class HtSyntaxException extends Exception {
private static final long serialVersionUID = 4663221245276673402L;

	public HtSyntaxException (String message) {
		super(message);
	}
}
