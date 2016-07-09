/**
 * StatPutCmd.java
 * @author matt.defano@gmail.com
 * 
 * Encapsulation of the "put" command
 */

package hypertalk.ast.statements;

import hypertalk.ast.containers.Destination;
import hypertalk.ast.containers.Preposition;
import hypertalk.ast.expressions.Expression;
import hypertalk.exception.HtException;

import java.io.Serializable;

public class StatPutCmd extends Statement implements Serializable {
private static final long serialVersionUID = 2182347647936086216L;

	public final Expression expression;
	public final Preposition preposition;
	public final Destination destination;
	
	public StatPutCmd (Expression e, Preposition p, Destination d) {
		expression = e;
		preposition = p;
		destination = d;
	}
	
	public void execute () throws HtException {
		destination.putValue(expression.evaluate(), preposition);
	}
}
