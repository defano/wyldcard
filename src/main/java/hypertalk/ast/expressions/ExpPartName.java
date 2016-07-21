/**
 * ExpPartName.java
 * @author matt.defano@gmail.com
 * 
 * Encapsulation of name-based part specification, for example: "field myField"
 */

package hypertalk.ast.expressions;

import hypercard.context.GlobalContext;
import hypertalk.ast.common.PartType;
import hypertalk.ast.common.Value;
import hypertalk.ast.containers.PartNameSpecifier;
import hypertalk.ast.containers.PartSpecifier;
import hypertalk.exception.HtSemanticException;

import java.io.Serializable;

public class ExpPartName extends ExpPart {

	public final PartType type;
	public final Expression name;
	
	public ExpPartName (PartType type, Expression name) {
		this.type = type;
		this.name = name;
	}
	
	public Value evaluate () throws HtSemanticException {
		try {
			PartSpecifier part = new PartNameSpecifier(type, name.evaluate().stringValue());
			return GlobalContext.getContext().get(part).getValue();
		} catch (Exception e) {
			throw new HtSemanticException(e.getMessage());
		}
	}
	
	public PartSpecifier evaluateAsSpecifier () 
	throws HtSemanticException
	{		
		return new PartNameSpecifier(type, name.evaluate().stringValue());
	}	
}
