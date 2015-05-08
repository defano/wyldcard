/**
 * ExpProperty.java
 * @author matt.defano@gmail.com
 * 
 * Encapsulation of a property, for example "visible of button id 10"
 */

package hypertalk.ast.expressions;

import hypercard.context.GlobalContext;
import hypertalk.ast.common.Value;
import hypertalk.exception.HtSyntaxException;

import java.io.Serializable;

public class ExpProperty extends Expression implements Serializable {
private static final long serialVersionUID = 539520368871329594L;

	public final String property;
	public final ExpPart part;
	
	public ExpProperty (String property, ExpPart part) {
		this.property = property;
		this.part = part;
	}
	
	public Value evaluate () throws HtSyntaxException {
		try {
			return GlobalContext.getContext().get(property, part.evaluateAsSpecifier());
		} catch (Exception e) {
			throw new HtSyntaxException(e.getMessage());
		}
	}	
}
