/**
 * ExpProperty.java
 * @author matt.defano@gmail.com
 * 
 * Encapsulation of a property, for example "visible of button id 10"
 */

package hypertalk.ast.expressions;

import hypercard.context.GlobalContext;
import hypertalk.ast.common.Value;
import hypertalk.ast.containers.PropertySpecifier;
import hypertalk.exception.HtSemanticException;

public class ExpProperty extends Expression {

	public final PropertySpecifier propertySpecifier;

	public ExpProperty (PropertySpecifier propertySpecifier) {
		this.propertySpecifier = propertySpecifier;
	}
	
	public Value evaluate () throws HtSemanticException {
		try {
			return GlobalContext.getContext().get(propertySpecifier.property, propertySpecifier.partExp.evaluateAsSpecifier());
		} catch (Exception e) {
			throw new HtSemanticException(e.getMessage());
		}
	}	
}
