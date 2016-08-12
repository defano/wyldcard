/**
 * ExpNumberOfFun.java
 * @author matt.defano@gmail.com
 * 
 * Implementation of the built-in function "the number of"
 */

package hypertalk.ast.functions;

import hypertalk.ast.common.ChunkType;
import hypertalk.ast.common.Value;
import hypertalk.ast.expressions.Expression;
import hypertalk.exception.HtSemanticException;

public class ExpNumberOfFun extends Expression {

    public final ChunkType itemtype;
    public final Expression expression;
    
    public ExpNumberOfFun(ChunkType itemtype, Expression expression) {
        this.itemtype = itemtype;
        this.expression = expression;
    }
    
    public Value evaluate () throws HtSemanticException {
        switch (itemtype) {
        case CHAR: return new Value(expression.evaluate().charCount());
        case WORD: return new Value(expression.evaluate().wordCount());
        case LINE: return new Value(expression.evaluate().lineCount());
        case ITEM: return new Value(expression.evaluate().itemCount());
        default: throw new RuntimeException("Unhandeled coutable item type");
        }
    }
}
