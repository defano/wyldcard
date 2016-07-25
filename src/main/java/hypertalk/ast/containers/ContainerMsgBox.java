/**
 * ContainerMsgBox.java
 *
 * @author matt.defano@gmail.com
 * <p>
 * Representation of the message box as a container for Value
 */

package hypertalk.ast.containers;

import hypercard.context.GlobalContext;
import hypercard.runtime.RuntimeEnv;
import hypertalk.ast.common.Chunk;
import hypertalk.ast.common.PartType;
import hypertalk.ast.common.Value;
import hypertalk.exception.HtException;

public class ContainerMsgBox extends Container {

    public final Chunk chunk;

    public ContainerMsgBox() {
        this.chunk = null;
    }

    public ContainerMsgBox(Chunk chunk) {
        this.chunk = chunk;
    }

    public Chunk chunk() {
        return chunk;
    }

    @Override
    public Value getValue() throws HtException {
        Value value = new Value(RuntimeEnv.getRuntimeEnv().getMsgBoxText());
        return chunkOf(value, this.chunk());
    }

    @Override
    public void putValue(Value value, Preposition preposition) throws HtException {
        GlobalContext.getContext().put(value, preposition, this);
        RuntimeEnv.getRuntimeEnv().setMessageBoxVisible(true);
    }

    public PartType type() {
        return PartType.MESSAGEBOX;
    }
}
