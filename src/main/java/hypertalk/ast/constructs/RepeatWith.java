/**
 * RepeatWith.java
 * @author matt.defano@gmail.com
 * 
 * Encapsulation of the "repeat with x = y to z" construct
 */

package hypertalk.ast.constructs;

import java.io.Serializable;

public class RepeatWith extends RepeatSpecifier implements Serializable {
private static final long serialVersionUID = 4786603013878943529L;

	public final String symbol;
	public final RepeatRange range;
	
	public RepeatWith (String symbol, RepeatRange range) {
		this.symbol = symbol;
		this.range = range;
	}
}
