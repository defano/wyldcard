package com.defano.hypertalk.ast.containers;

import com.defano.hypercard.runtime.context.ExecutionContext;
import com.defano.hypertalk.ast.common.Chunk;
import com.defano.hypertalk.ast.common.Preposition;
import com.defano.hypertalk.ast.common.Value;
import com.defano.hypertalk.ast.specifiers.PartSpecifier;
import com.defano.hypertalk.ast.specifiers.PropertySpecifier;
import com.defano.hypertalk.exception.HtException;
import com.defano.hypertalk.exception.HtSemanticException;
import com.defano.hypertalk.utils.MenuPropertiesDelegate;

public class PropertyContainer extends Container {

    public final PropertySpecifier propertySpec;
    public final Chunk chunk;

    public PropertyContainer(PropertySpecifier propertySpec) {
        this.propertySpec = propertySpec;
        this.chunk = null;
    }

    public PropertyContainer(PropertySpecifier propertySpec, Chunk chunk) {
        this.propertySpec = propertySpec;
        this.chunk = chunk;
    }

    public Chunk chunk() {
        return chunk;
    }

    @Override
    public Value getValue() throws HtException {
        Value propertyValue;

        if (propertySpec.isMenuItemPropertySpecifier()) {
            propertyValue = MenuPropertiesDelegate.getProperty(propertySpec.property, propertySpec.menuItem);
        } else {
            propertyValue = ExecutionContext.getContext().get(getPartSpecifier()).getProperty(getPropertyName());
        }

        return chunkOf(propertyValue, this.chunk());
    }

    @Override
    public void putValue(Value value, Preposition preposition) throws HtException {
        if (propertySpec.isMenuItemPropertySpecifier()) {
            throw new HtSemanticException("Cannot put a value into this kind of property.");
        } else {
            ExecutionContext.getContext().set(propertySpec.property, propertySpec.partExp.evaluateAsSpecifier(), preposition, chunk, value);
        }
    }

    public PartSpecifier getPartSpecifier() throws HtException {
        return propertySpec.partExp.evaluateAsSpecifier();
    }

    public String getPropertyName() {
        return propertySpec.property;
    }
}
