/**
 * ExpPartId.java
 * @author matt.defano@gmail.com
 * 
 * Encapsulation of an id-based part specification, for example: "button id 12"
 */

package hypertalk.ast.expressions;

import hypercard.context.GlobalContext;
import hypercard.parts.PartException;
import hypertalk.ast.common.PartType;
import hypertalk.ast.common.Value;
import hypertalk.ast.containers.PartIdSpecifier;
import hypertalk.ast.containers.PartSpecifier;
import hypertalk.exception.HtSemanticException;

public class ExpPartId extends ExpPart {

	public final PartType type;
	public final Expression id;
	
	public ExpPartId (PartType type, Expression id) {
		this.type = type;
		this.id = id;
	}
	
	public Value evaluate () throws HtSemanticException {
		try {
			PartSpecifier part = new PartIdSpecifier(type, id.evaluate().integerValue());
			return GlobalContext.getContext().get(part).getValue();
		} catch (PartException e) {
			throw new HtSemanticException(e.getMessage());
		}
	}
	
	public PartSpecifier evaluateAsSpecifier () 
	throws HtSemanticException
	{		
		return new PartIdSpecifier(type, id.evaluate().integerValue());
	}
	
}
