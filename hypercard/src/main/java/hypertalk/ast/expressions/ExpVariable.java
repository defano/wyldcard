/**
 * ExpVariable.java
 * @author matt.defano@gmail.com
 * 
 * Encapsulation of a variable expression in HyperTalk, for example: "myVariable"
 */

package hypertalk.ast.expressions;

import hypercard.context.GlobalContext;
import hypertalk.ast.common.Value;

import java.io.Serializable;

public class ExpVariable extends Expression implements Serializable {
private static final long serialVersionUID = 7519151766770969204L;

	public final String identifier;
	
	public ExpVariable (String identifier) {
		this.identifier = identifier;
	}
	
	public Value evaluate () {
		return GlobalContext.getContext().get(identifier);
	}
}
