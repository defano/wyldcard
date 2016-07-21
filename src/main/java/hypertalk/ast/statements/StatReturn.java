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
import hypertalk.exception.HtSemanticException;

import java.io.Serializable;

public class StatReturn extends Statement {

	public final Expression returnValue;
	
	public StatReturn () {
		this.returnValue = new ExpLiteral("");
	}
	
	public StatReturn (Expression returnValue) {
		this.returnValue = returnValue;
	}

	public void execute () throws HtSemanticException {
		GlobalContext.getContext().setReturnValue(returnValue.evaluate());
		this.breakExecution = true;
	}
}
