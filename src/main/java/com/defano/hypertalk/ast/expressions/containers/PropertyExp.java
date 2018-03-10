package com.defano.hypertalk.ast.expressions.containers;

import com.defano.wyldcard.runtime.context.ExecutionContext;
import com.defano.wyldcard.runtime.HyperCardProperties;
import com.defano.hypertalk.ast.model.Adjective;
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

        // Getting the chunk of a property
        if (propertySpec.isChunkPropertySpecifier()) {
            propertyValue = ChunkPropertiesDelegate.getProperty(propertySpec.getAdjectiveAppliedPropertyName(), propertySpec.getChunk(), getPartSpecifier());
        }

        // Getting the property of a menu item
        else if (propertySpec.isMenuItemPropertySpecifier()) {
            propertyValue = MenuPropertiesDelegate.getProperty(propertySpec.getProperty(), propertySpec.getMenuItem());
        }

        // Getting a HyperCard (global) property
        else if (propertySpec.isGlobalPropertySpecifier()) {
            propertyValue = HyperCardProperties.getInstance().getProperty(propertySpec.getProperty());
        }

        // Getting a part property
        else {
            PartSpecifier partSpecifier = getPartSpecifier();
            propertyValue = ExecutionContext.getContext().getPart(partSpecifier).getProperty(propertySpec.getAdjectiveAppliedPropertyName());
        }

        return chunkOf(propertyValue, getChunk());
    }

    @Override
    public void putValue(Value value, Preposition preposition) throws HtException {

        // Cannot set the adjective-form of a property (i.e., set 'the name' not 'the long name')
        if (propertySpec.getAdjective() != Adjective.DEFAULT) {
            throw new HtSemanticException("Cannot set that property.");
        }

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

}
