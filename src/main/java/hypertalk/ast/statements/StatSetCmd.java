/**
 * StatSetCmd.java
 * @author matt.defano@gmail.com
 * 
 * Encapsulation of the "set" command (for mutating a property)
 */

package hypertalk.ast.statements;

import hypercard.context.GlobalContext;
import hypertalk.ast.expressions.ExpPart;
import hypertalk.ast.expressions.Expression;
import hypertalk.exception.HtSemanticException;

import java.io.Serializable;

public class StatSetCmd extends Statement implements Serializable {
private static final long serialVersionUID = 5770378882468419709L;

	public final String property;
	public final ExpPart part;
	public final Expression expression;
	
	public StatSetCmd (String property, ExpPart part, Expression expression) {
		this.property = property;
		this.part = part;
		this.expression = expression;
	}
	
	public void execute () throws HtSemanticException {
		try {
			GlobalContext.getContext().set(property, part.evaluateAsSpecifier(), expression.evaluate());
		} catch (Exception e) {
			throw new HtSemanticException(e.getMessage());
		}
	}
}
