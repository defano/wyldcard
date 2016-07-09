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
import hypertalk.ast.common.Value;
import hypertalk.exception.HtException;

import java.io.Serializable;

public abstract class Destination implements Serializable {
    private static final long serialVersionUID = -6162809738803477696L;

    public Value getValue() throws HtException {

        if (this instanceof DestinationVariable) {
            return GlobalContext.getContext().get(((DestinationVariable) this).symbol());
        } else if (this instanceof DestinationPart) {
            return GlobalContext.getContext().get(((DestinationPart) this).part().evaluateAsSpecifier()).getValue();
        } else if (this instanceof DestinationMsgBox) {
            return new Value(RuntimeEnv.getRuntimeEnv().getMsgBoxText());
        } else {
            throw new HtException("Bug! Unimplemented destination type.");
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

