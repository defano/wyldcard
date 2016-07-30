/**
 * StatSetCmd.java
 * @author matt.defano@gmail.com
 * 
 * Encapsulation of the "set" command (for mutating a property)
 */

package hypertalk.ast.statements;

import hypercard.context.GlobalContext;
import hypertalk.ast.containers.PropertySpecifier;
import hypertalk.ast.expressions.Expression;
import hypertalk.exception.HtSemanticException;

public class StatSetCmd extends Statement {

	public final Expression expression;
	public final PropertySpecifier propertySpec;

	public StatSetCmd (PropertySpecifier propertySpec, Expression expression) {
		this.propertySpec = propertySpec;
		this.expression = expression;
	}
	
	public void execute () throws HtSemanticException {
		try {
			GlobalContext.getContext().set(propertySpec.property, propertySpec.partExp.evaluateAsSpecifier(), expression.evaluate());
		} catch (Exception e) {
			throw new HtSemanticException(e);
		}
	}
}
