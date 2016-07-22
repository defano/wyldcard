/**
 * DestinationPart.java
 *
 * @author matt.defano@gmail.com
 * <p>
 * Representation of a HyperCard part as a destination for Value
 */

package hypertalk.ast.containers;

import hypercard.context.GlobalContext;
import hypertalk.ast.common.Chunk;
import hypertalk.ast.common.Value;
import hypertalk.ast.expressions.ExpPart;
import hypertalk.exception.HtException;

import java.io.Serializable;


public class DestinationPart extends Destination {

    private final ExpPart part;
    private final Chunk chunk;

    public DestinationPart(ExpPart part) {
        this.part = part;
        this.chunk = null;
    }

    public DestinationPart(ExpPart part, Chunk chunk) {
        this.part = part;
        this.chunk = chunk;
    }

    public ExpPart part() {
        return part;
    }

    public Chunk chunk() {
        return chunk;
    }

    @Override
    public Value getValue() throws HtException {
        Value value = GlobalContext.getContext().get(part.evaluateAsSpecifier()).getValue();
        return chunkOf(value, this.chunk());
    }

    @Override
    public void putValue(Value value, Preposition preposition) throws HtException {
        GlobalContext.getContext().put(value, preposition, this);
    }
}
