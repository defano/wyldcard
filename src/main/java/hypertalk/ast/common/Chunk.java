/**
 * Chunk.java
 * @author matt.defano@gmail.com
 * 
 * Encapsulation of a chunked expression.
 */

package hypertalk.ast.common;

import hypertalk.ast.expressions.Expression;

public class Chunk {

    public final ChunkType type;
    public final Expression start;
    public final Expression end;
    
    public Chunk (ChunkType type, Expression item) {
        this.type = type;
        this.start = item;
        this.end = null;
    }
    
    public Chunk (ChunkType type, Expression start, Expression end) {
        this.type = type;
        this.start = start;
        this.end = end;
    }    
}
