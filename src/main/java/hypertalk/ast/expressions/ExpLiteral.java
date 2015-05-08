/**
 * ExpLiteral.java
 * @author matt.defano@gmail.com
 * 
 * Encapsulation of a literal value in HyperTalk, for example: "Hello world"
 */

package hypertalk.ast.expressions;

import hypertalk.ast.common.Value;

import java.io.Serializable;

public class ExpLiteral extends Expression implements Serializable {
private static final long serialVersionUID = 7764685929011758950L;

	public final String literal;
	
	public ExpLiteral (String literal) {
		this.literal = literal;
	}
	
	public Value evaluate () {
		return new Value(literal);
	}
}
