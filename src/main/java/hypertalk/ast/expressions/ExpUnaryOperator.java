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

public class ExpUnaryOperator extends Expression {

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
		default: throw new HtSemanticException("Unhandled unary operator in evaluation: " + operator);
		}
	}
}
