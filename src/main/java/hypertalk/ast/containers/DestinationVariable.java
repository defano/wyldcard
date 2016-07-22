/**
 * DestinationVariable.java
 *
 * @author matt.defano@gmail.com
 * <p>
 * Representation of a variable as a destination for Value
 */

package hypertalk.ast.containers;

import hypercard.context.GlobalContext;
import hypertalk.ast.common.Chunk;
import hypertalk.ast.common.Value;
import hypertalk.exception.HtException;

import java.io.Serializable;

public class DestinationVariable extends Destination {

    private final String symbol;
    private final Chunk chunk;

    public DestinationVariable(String symbol) {
        this.symbol = symbol;
        this.chunk = null;
    }

    public DestinationVariable(String symbol, Chunk chunk) {
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
        GlobalContext.getContext().put(value, preposition, (DestinationVariable) this);
    }
}
