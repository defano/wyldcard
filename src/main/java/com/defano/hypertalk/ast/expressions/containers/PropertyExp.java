package com.defano.hypertalk.ast.expressions.containers;

import com.defano.hypertalk.ast.model.LengthAdjective;
import com.defano.wyldcard.WyldCard;
import com.defano.wyldcard.runtime.context.ExecutionContext;
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
    public Value onEvaluate(ExecutionContext context) throws HtException {
        Value propertyValue;

        // Getting the chunk of a property
        if (propertySpec.isChunkPropertySpecifier(context)) {
            propertyValue = ChunkPropertiesDelegate.getProperty(context, propertySpec.getAdjectiveAppliedPropertyName(context), propertySpec.getChunk(context), getPartSpecifier(context));
        }

        // Getting the property of a menu item
        else if (propertySpec.isMenuItemPropertySpecifier(context)) {
            propertyValue = MenuPropertiesDelegate.getProperty(context, propertySpec.getProperty(), propertySpec.getMenuItem(context));
        }

        // Getting a HyperCard (global) property
        else if (propertySpec.isGlobalPropertySpecifier(context)) {
            propertyValue = WyldCard.getInstance().getWyldCardProperties().getProperty(context, propertySpec.getProperty());
        }

        // Getting a part property
        else {
            PartSpecifier partSpecifier = getPartSpecifier(context);
            propertyValue = context.getPart(partSpecifier).getProperty(context, propertySpec.getAdjectiveAppliedPropertyName(context));
        }

        return chunkOf(context, propertyValue, getChunk());
    }

    @Override
    public void putValue(ExecutionContext context, Value value, Preposition preposition) throws HtException {

        // Cannot set the adjective-form of a property (i.e., set 'the name' not 'the long name')
        if (propertySpec.getLengthAdjective() != LengthAdjective.DEFAULT) {
            throw new HtSemanticException("Cannot set that property.");
        }

        if (propertySpec.isChunkPropertySpecifier(context)) {
            ChunkPropertiesDelegate.setProperty(context, propertySpec.getProperty(), value, propertySpec.getChunk(context), getPartSpecifier(context));
        } else if (propertySpec.isMenuItemPropertySpecifier(context)) {
            throw new HtSemanticException("Cannot put a value into this kind of property.");
        } else {
            try {
                context.setProperty(propertySpec.getProperty(), getPartSpecifier(context), preposition, getChunk(), value);
            } catch (NoSuchPropertyException e) {
                // Context sensitive: Unknown HC property references are assumed to be local variable references
                context.setVariable(propertySpec.getProperty(), preposition, getChunk(), value);
            }
        }
    }

    private PartSpecifier getPartSpecifier(ExecutionContext context) throws HtException {
        return propertySpec.getPartExp(context) == null ? null : propertySpec.getPartExp(context).evaluateAsSpecifier(context);
    }

}
