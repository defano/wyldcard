/**
 * RepeatCount.java
 * @author matt.defano@gmail.com
 * 
 * Encapsulation of the "repeat for x times" construct
 */

package hypertalk.ast.constructs;

import hypertalk.ast.expressions.Expression;
import java.io.Serializable;

public class RepeatCount extends RepeatSpecifier implements Serializable {
private static final long serialVersionUID = -4149083979586304618L;

	public final Expression count;
	
	public RepeatCount (Expression count) {
		this.count = count;
	}
}
