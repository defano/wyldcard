/**
 * StatExp.java
 * @author matt.defano@gmail.com
 * 
 * Encapsulation of an expression statement
 */

package hypertalk.ast.statements;

import hypercard.context.GlobalContext;
import hypertalk.ast.common.Value;
import hypertalk.ast.expressions.Expression;
import hypertalk.exception.HtSemanticException;

import java.io.Serializable;

public class StatExp extends Statement implements Serializable {
private static final long serialVersionUID = 4498941402376793009L;

	public final Expression expression;
	
	public StatExp (Expression expression) {
		this.expression = expression;
	}
	
	public void execute () throws HtSemanticException {
		Value v = expression.evaluate();
		GlobalContext.getContext().setIt(v);
	}
}
