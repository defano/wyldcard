/**
 * Destination.java
 *
 * @author matt.defano@gmail.com
 * <p>
 * Abstract superclass of any HyperTalk element capable of accepting a value.
 * In this context, "destination" is analagous to "l-value"
 */

package hypertalk.ast.containers;

import hypercard.context.GlobalContext;
import hypercard.runtime.RuntimeEnv;
import hypertalk.ast.common.Chunk;
import hypertalk.ast.common.Value;
import hypertalk.exception.HtException;
import hypertalk.exception.HtSemanticException;

public abstract class Destination {

    public abstract Chunk chunk();

    public Value getValue() throws HtException {

        if (this instanceof DestinationVariable) {
            Value value = GlobalContext.getContext().get(((DestinationVariable) this).symbol());
            return chunkOf(value, this.chunk());
        } else if (this instanceof DestinationPart) {
            Value value = GlobalContext.getContext().get(((DestinationPart) this).part().evaluateAsSpecifier()).getValue();
            return chunkOf(value, this.chunk());
        } else if (this instanceof DestinationMsgBox) {
            Value value = new Value(RuntimeEnv.getRuntimeEnv().getMsgBoxText());
            return chunkOf(value, this.chunk());
        } else {
            throw new HtException("Bug! Unimplemented destination type.");
        }
    }

    private Value chunkOf (Value v, Chunk chunk) throws HtSemanticException {
        if (chunk == null) {
            return v;
        } else {
            return v.getChunk(chunk);
        }
    }

    public void putValue (Value value, Preposition preposition) throws HtException {
        if (this instanceof DestinationVariable)
            GlobalContext.getContext().put(value, preposition, (DestinationVariable)this);
        else if (this instanceof DestinationPart)
            GlobalContext.getContext().put(value, preposition, (DestinationPart)this);
        else if (this instanceof DestinationMsgBox)
            GlobalContext.getContext().put(value, preposition, (DestinationMsgBox)this);
        else
            throw new RuntimeException("Bug! Unimplemented destination type.");
    }
}

