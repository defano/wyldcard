/**
 * ExpUnaryOperator.java
 * @author matt.defano@gmail.com
 * 
 * Encapsulation of a unary operation expression, for example: "not true"
 */

package hypertalk.ast.expressions;

import hypertalk.ast.common.UnaryOperator;
import hypertalk.ast.common.Value;
import hypertalk.exception.HtSemanticException;

import java.io.Serializable;

public class ExpUnaryOperator extends Expression implements Serializable {
private static final long serialVersionUID = 8022978584866255203L;

	public final UnaryOperator operator;
	public final Expression rhs;
	
	public ExpUnaryOperator (UnaryOperator op, Expression rhs) {
		this.operator = op;
		this.rhs = rhs;
	}
	
	public Value evaluate () throws HtSemanticException {
		Value rhs = this.rhs.evaluate();
		
		switch (operator) {
		case NOT: return rhs.not();
		case NEGATE: return rhs.negate();
		default: throw new HtSemanticException("Unhandeled unary operator in evaluation: " + operator);
		}
	}
}
