/**
 * ExpChunk.java
 * @author matt.defano@gmail.com
 * 
 * Encapsulation of a chunk expression in HyperTalk, for example: "the second word of..."
 */

package hypertalk.ast.expressions;

import hypertalk.ast.common.Chunk;
import hypertalk.ast.common.Value;
import hypertalk.exception.HtSemanticException;

import java.io.Serializable;

public class ExpChunk extends Expression implements Serializable {
private static final long serialVersionUID = 4831685944001487239L;

	public final Chunk chunk;
	public final Expression expression;
	
	public ExpChunk (Chunk chunk, Expression expression) {
		this.chunk = chunk;
		this.expression = expression;
	}
	
	public Value evaluate () throws HtSemanticException {
		return expression.evaluate().getChunk(chunk);
	}
}
