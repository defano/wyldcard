/**
 * RepeatWith.java
 * @author matt.defano@gmail.com
 * 
 * Encapsulation of the "repeat with x = y to z" construct
 */

package hypertalk.ast.constructs;

public class RepeatWith extends RepeatSpecifier {

	public final String symbol;
	public final RepeatRange range;
	
	public RepeatWith (String symbol, RepeatRange range) {
		this.symbol = symbol;
		this.range = range;
	}
}
