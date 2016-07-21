/**
 * StatIf.java
 * @author matt.defano@gmail.com
 * 
 * Encapsulation of an if-then-else statement
 */

package hypertalk.ast.statements;

import hypertalk.ast.constructs.ThenElseBlock;
import hypertalk.ast.expressions.Expression;
import hypertalk.exception.HtException;

import java.io.Serializable;

public class StatIf extends Statement {

	public final Expression condition;
	public final ThenElseBlock then;
	
	public StatIf (Expression condition, ThenElseBlock then) {
		this.condition = condition;
		this.then = then;
	}
	
	public void execute () throws HtException {
		if (condition.evaluate().booleanValue())
			then.thenBranch.execute();
		else
			then.elseBranch.execute();
	}
}
