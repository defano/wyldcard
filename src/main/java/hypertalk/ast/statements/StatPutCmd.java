/**
 * StatPutCmd.java
 * @author matt.defano@gmail.com
 * 
 * Encapsulation of the "put" command
 */

package hypertalk.ast.statements;

import hypertalk.ast.containers.Container;
import hypertalk.ast.containers.Preposition;
import hypertalk.ast.expressions.Expression;
import hypertalk.exception.HtException;

public class StatPutCmd extends Statement {

	public final Expression expression;
	public final Preposition preposition;
	public final Container container;
	
	public StatPutCmd (Expression e, Preposition p, Container d) {
		expression = e;
		preposition = p;
		container = d;
	}
	
	public void execute () throws HtException {
		container.putValue(expression.evaluate(), preposition);
	}
}
