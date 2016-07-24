/**
 * ContainerVariable.java
 *
 * @author matt.defano@gmail.com
 * <p>
 * Representation of a variable as a container for Value
 */

package hypertalk.ast.containers;

import hypercard.context.GlobalContext;
import hypertalk.ast.common.Chunk;
import hypertalk.ast.common.Value;
import hypertalk.exception.HtException;

public class ContainerVariable extends Container {

    private final String symbol;
    private final Chunk chunk;

    public ContainerVariable(String symbol) {
        this.symbol = symbol;
        this.chunk = null;
    }

    public ContainerVariable(String symbol, Chunk chunk) {
        this.symbol = symbol;
        this.chunk = chunk;
    }

    public String symbol() {
        return symbol;
    }

    public Chunk chunk() {
        return chunk;
    }

    @Override
    public Value getValue() throws HtException {
        Value value = GlobalContext.getContext().get(symbol);
        return chunkOf(value, this.chunk());
    }

    @Override
    public void putValue(Value value, Preposition preposition) throws HtException {
        GlobalContext.getContext().put(value, preposition, (ContainerVariable) this);
    }
}
