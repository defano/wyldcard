package com.defano.hypertalk.ast.containers;

import com.defano.hypercard.runtime.context.ExecutionContext;
import com.defano.hypertalk.ast.common.Chunk;
import com.defano.hypertalk.ast.common.Preposition;
import com.defano.hypertalk.ast.common.Value;
import com.defano.hypertalk.ast.specifiers.PartSpecifier;
import com.defano.hypertalk.ast.specifiers.PropertySpecifier;
import com.defano.hypertalk.exception.HtException;
import com.defano.hypertalk.exception.HtSemanticException;
import com.defano.hypertalk.exception.NoSuchPropertyException;
import com.defano.hypertalk.utils.ChunkPropertiesDelegate;
import com.defano.hypertalk.utils.MenuPropertiesDelegate;

public class PropertyContainer extends Container {

    public final PropertySpecifier propertySpec;

    public PropertyContainer(PropertySpecifier propertySpec) {
        this.propertySpec = propertySpec;
    }

    @Override
    public Value getValue() throws HtException {
        Value propertyValue;

        if (propertySpec.isChunkPropertySpecifier()) {
            propertyValue = ChunkPropertiesDelegate.getProperty(propertySpec.property, propertySpec.chunk, getPartSpecifier());
        } else if (propertySpec.isMenuItemPropertySpecifier()) {
            propertyValue = MenuPropertiesDelegate.getProperty(propertySpec.property, propertySpec.menuItem);
        } else {
            propertyValue = ExecutionContext.getContext().getPart(getPartSpecifier()).getProperty(getPropertyName());
        }

        return chunkOf(propertyValue, getChunk());
    }

    @Override
    public void putValue(Value value, Preposition preposition) throws HtException {

        if (propertySpec.isChunkPropertySpecifier()) {
            ChunkPropertiesDelegate.setProperty(propertySpec.property, value, propertySpec.chunk, getPartSpecifier());
        } else if (propertySpec.isMenuItemPropertySpecifier()) {
            throw new HtSemanticException("Cannot put a value into this kind of property.");
        } else {
            try {
                ExecutionContext.getContext().setProperty(propertySpec.property, getPartSpecifier(), preposition, getChunk(), value);
            } catch (NoSuchPropertyException e) {
                // Context sensitive: Unknown HC property references are assumed to be local variable references
                ExecutionContext.getContext().setVariable(propertySpec.property, preposition, getChunk(), value);
            }
        }
    }

    private PartSpecifier getPartSpecifier() throws HtException {
        return propertySpec.partExp == null ? null : propertySpec.partExp.evaluateAsSpecifier();
    }

    public String getPropertyName() {
        return propertySpec.property;
    }
}
