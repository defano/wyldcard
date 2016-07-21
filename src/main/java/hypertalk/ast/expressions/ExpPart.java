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

public abstract class ExpPart extends Expression {

	public abstract PartSpecifier evaluateAsSpecifier () throws HtSemanticException;
}
