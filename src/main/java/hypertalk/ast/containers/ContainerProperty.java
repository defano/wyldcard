package hypertalk.ast.containers;

import hypercard.context.GlobalContext;
import hypertalk.ast.common.Chunk;
import hypertalk.ast.common.Value;
import hypertalk.exception.HtException;
import hypertalk.exception.HtSemanticException;

public class ContainerProperty extends Container {

    public final PropertySpecifier propertySpec;

    public ContainerProperty(PropertySpecifier propertySpec) {
        this.propertySpec = propertySpec;
    }

    @Override
    public Chunk chunk() {
        // Properties cannot be mutated by chunk
        return null;
    }

    @Override
    public Value getValue() throws HtException {
        Value propertyValue = GlobalContext.getContext().get(getPartSpecifier()).getProperty(getPropertyName());
        return chunkOf(propertyValue, this.chunk());
    }

    @Override
    public void putValue(Value value, Preposition preposition) throws HtException {
        GlobalContext.getContext().set(propertySpec.property, propertySpec.partExp.evaluateAsSpecifier(), value);
    }

    public PartSpecifier getPartSpecifier() throws HtSemanticException {
        return propertySpec.partExp.evaluateAsSpecifier();
    }

    public String getPropertyName() {
        return propertySpec.property;
    }
}
