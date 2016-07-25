/**
 * RepeatCount.java
 * @author matt.defano@gmail.com
 * 
 * Encapsulation of the "repeat for x times" construct
 */

package hypertalk.ast.constructs;

import hypertalk.ast.expressions.Expression;

public class RepeatCount extends RepeatSpecifier {
	public final Expression count;
	
	public RepeatCount (Expression count) {
		this.count = count;
	}
}
