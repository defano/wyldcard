/**
 * ContainerPart.java
 *
 * @author matt.defano@gmail.com
 * <p>
 * Representation of a HyperCard part as a container for Value
 */

package hypertalk.ast.containers;

import hypercard.context.GlobalContext;
import hypertalk.ast.common.Chunk;
import hypertalk.ast.common.Value;
import hypertalk.ast.expressions.ExpPart;
import hypertalk.exception.HtException;


public class ContainerPart extends Container {

    private final ExpPart part;
    private final Chunk chunk;

    public ContainerPart(ExpPart part) {
        this.part = part;
        this.chunk = null;
    }

    public ContainerPart(ExpPart part, Chunk chunk) {
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
