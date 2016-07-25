/**
 * Expression.java
 * @author matt.defano@gmail.com
 * 
 * Abstract superclass of all expression types
 */

package hypertalk.ast.expressions;

import hypertalk.ast.common.Value;
import hypertalk.exception.HtSemanticException;


public abstract class Expression {

	public abstract Value evaluate() throws HtSemanticException;
}
