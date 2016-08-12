/**
 * ExpLiteral.java
 * @author matt.defano@gmail.com
 * 
 * Encapsulation of a literal value in HyperTalk, for example: "Hello world"
 */

package hypertalk.ast.expressions;

import hypertalk.ast.common.Value;

public class ExpLiteral extends Expression {

    public final String literal;

    public ExpLiteral (Object literal) {
        this.literal = String.valueOf(literal);
    }
    
    public Value evaluate () {
        return new Value(literal);
    }
}
