/**
 * ExpPart.java
 * @author matt.defano@gmail.com
 * 
 * Abstract superclass for part expressions
 */

package hypertalk.ast.expressions;

import hypertalk.ast.containers.PartSpecifier;
import hypertalk.exception.HtSemanticException;

import java.io.Serializable;

public abstract class ExpPart extends Expression implements Serializable {
	private static final long serialVersionUID = 5383623948321039949L;

	public abstract PartSpecifier evaluateAsSpecifier () throws HtSemanticException;
}
