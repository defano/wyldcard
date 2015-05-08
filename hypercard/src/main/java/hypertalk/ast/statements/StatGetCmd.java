/**
 * StatGetCmd.java
 * @author matt.defano@gmail.com
 * 
 * Encapsulation of the "get" statement
 */

package hypertalk.ast.statements;

import hypercard.context.GlobalContext;
import hypertalk.ast.containers.PartSpecifier;
import hypertalk.ast.expressions.Expression;
import hypertalk.exception.HtSyntaxException;

import java.io.Serializable;

public class StatGetCmd extends Statement implements Serializable {
private static final long serialVersionUID = 9073999143856874300L;

	public final Expression expression;
	public final PartSpecifier part;
	
	public StatGetCmd (Expression e) {
		expression = e;
		part = null;
	}
	
	public StatGetCmd (PartSpecifier ps) {
		expression = null;
		part = ps;
	}
	
	public void execute () throws HtSyntaxException {
		if (expression != null)
			GlobalContext.getContext().setIt(expression.evaluate());
		else {
		}
	}
}
