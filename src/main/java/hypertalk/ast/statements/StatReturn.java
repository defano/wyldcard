/**
 * StatReturn.java
 * @author matt.defano@gmail.com
 * 
 * Encapsulation of the "return" statement
 */

package hypertalk.ast.statements;

import hypercard.context.GlobalContext;
import hypertalk.ast.expressions.ExpLiteral;
import hypertalk.ast.expressions.Expression;
import hypertalk.exception.HtSyntaxException;

import java.io.Serializable;

public class StatReturn extends Statement implements Serializable {
private static final long serialVersionUID = -7141968148410363812L;

	public final Expression returnValue;
	
	public StatReturn () {
		this.returnValue = new ExpLiteral("");
	}
	
	public StatReturn (Expression returnValue) {
		this.returnValue = returnValue;
	}

	public void execute () throws HtSyntaxException {
		GlobalContext.getContext().setReturnValue(returnValue.evaluate());
		this.breakExecution = true;
	}
}
