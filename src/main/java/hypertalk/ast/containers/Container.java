/**
 * Container.java
 *
 * @author matt.defano@gmail.com
 * <p>
 * Abstract superclass of any HyperTalk element capable of accepting a value.
 * In this context, "container" is analagous to "l-value"
 */

package hypertalk.ast.containers;

import hypertalk.ast.common.Chunk;
import hypertalk.ast.common.Value;
import hypertalk.exception.HtException;
import hypertalk.exception.HtSemanticException;

public abstract class Container {

    public abstract Value getValue() throws HtException;
    public abstract void putValue(Value value, Preposition preposition) throws HtException;

    protected Value chunkOf (Value v, Chunk chunk) throws HtSemanticException {
        if (chunk == null) {
            return v;
        } else {
            return v.getChunk(chunk);
        }
    }
}

