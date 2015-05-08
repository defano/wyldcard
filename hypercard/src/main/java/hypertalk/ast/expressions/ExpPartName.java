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
import hypertalk.exception.HtSyntaxException;

import java.io.Serializable;

public class ExpPartName extends ExpPart implements Serializable {
private static final long serialVersionUID = 5626552530253147320L;

	public final PartType type;
	public final Expression name;
	
	public ExpPartName (PartType type, Expression name) {
		this.type = type;
		this.name = name;
	}
	
	public Value evaluate () throws HtSyntaxException {
		try {
			PartSpecifier part = new PartNameSpecifier(type, name.evaluate().stringValue());
			return GlobalContext.getContext().get(part).getValue();
		} catch (Exception e) {
			throw new HtSyntaxException(e.getMessage());
		}
	}
	
	public PartSpecifier evaluateAsSpecifier () 
	throws HtSyntaxException 
	{		
		return new PartNameSpecifier(type, name.evaluate().stringValue());
	}	
}
