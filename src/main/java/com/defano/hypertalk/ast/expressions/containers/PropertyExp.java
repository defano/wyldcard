package com.defano.hypertalk.ast.expressions.containers;

import com.defano.hypercard.runtime.context.ExecutionContext;
import com.defano.hypercard.runtime.HyperCardProperties;
import com.defano.hypertalk.ast.model.Preposition;
import com.defano.hypertalk.ast.model.Value;
import com.defano.hypertalk.ast.model.specifiers.PartSpecifier;
import com.defano.hypertalk.ast.model.specifiers.PropertySpecifier;
import com.defano.hypertalk.exception.HtException;
import com.defano.hypertalk.exception.HtSemanticException;
import com.defano.hypertalk.exception.NoSuchPropertyException;
import com.defano.hypertalk.utils.ChunkPropertiesDelegate;
import com.defano.hypertalk.utils.MenuPropertiesDelegate;
import org.antlr.v4.runtime.ParserRuleContext;

public class PropertyExp extends ContainerExp {

    private final PropertySpecifier propertySpec;

    public PropertyExp(ParserRuleContext context, PropertySpecifier propertySpec) {
        super(context);
        this.propertySpec = propertySpec;
    }

    @Override
    public Value onEvaluate() throws HtException {
        Value propertyValue;

        if (propertySpec.isChunkPropertySpecifier()) {
            propertyValue = ChunkPropertiesDelegate.getProperty(propertySpec.getProperty(), propertySpec.getChunk(), getPartSpecifier());
        } else if (propertySpec.isMenuItemPropertySpecifier()) {
            propertyValue = MenuPropertiesDelegate.getProperty(propertySpec.getProperty(), propertySpec.getMenuItem());
        } else if (propertySpec.isGlobalPropertySpecifier()) {
            propertyValue = HyperCardProperties.getInstance().getProperty(propertySpec.getProperty());
        } else {
            propertyValue = ExecutionContext.getContext().getPart(getPartSpecifier()).getProperty(getPropertyName());
        }

        return chunkOf(propertyValue, getChunk());
    }

    @Override
    public void putValue(Value value, Preposition preposition) throws HtException {

        if (propertySpec.isChunkPropertySpecifier()) {
            ChunkPropertiesDelegate.setProperty(propertySpec.getProperty(), value, propertySpec.getChunk(), getPartSpecifier());
        } else if (propertySpec.isMenuItemPropertySpecifier()) {
            throw new HtSemanticException("Cannot put a value into this kind of property.");
        } else {
            try {
                ExecutionContext.getContext().setProperty(propertySpec.getProperty(), getPartSpecifier(), preposition, getChunk(), value);
            } catch (NoSuchPropertyException e) {
                // Context sensitive: Unknown HC property references are assumed to be local variable references
                ExecutionContext.getContext().setVariable(propertySpec.getProperty(), preposition, getChunk(), value);
            }
        }
    }

    private PartSpecifier getPartSpecifier() throws HtException {
        return propertySpec.getPartExp() == null ? null : propertySpec.getPartExp().evaluateAsSpecifier();
    }

    public String getPropertyName() {
        return propertySpec.getProperty();
    }
}
