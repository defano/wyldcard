/**
 * ExpPartMe.java
 * @author matt.defano@gmail.com
 * 
 * Encapsulation of a part self-reference, for example: "me"
 */

package hypertalk.ast.expressions;

import hypercard.context.GlobalContext;
import hypercard.parts.PartException;
import hypertalk.ast.common.Value;
import hypertalk.ast.containers.PartSpecifier;
import hypertalk.exception.HtSemanticException;

import java.io.Serializable;

public class ExpPartMe extends ExpPart implements Serializable {
private static final long serialVersionUID = 8526792298968057607L;

	public ExpPartMe () {}
	
	public Value evaluate () throws HtSemanticException {
		try {
			PartSpecifier part = GlobalContext.getContext().getMe();			
			return GlobalContext.getContext().get(part).getValue();
		} catch (PartException e) {
			throw new HtSemanticException(e.getMessage());
		}
	}
	
	public PartSpecifier evaluateAsSpecifier () 
	throws HtSemanticException
	{		
		return GlobalContext.getContext().getMe();
	}	
}
