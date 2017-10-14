package com.defano.hypertalk.ast.expressions;

import com.defano.hypercard.runtime.context.ExecutionContext;
import com.defano.hypertalk.ast.common.Value;
import com.defano.hypertalk.ast.specifiers.PropertySpecifier;
import com.defano.hypertalk.exception.HtException;
import com.defano.hypertalk.utils.MenuPropertiesDelegate;
import com.defano.hypertalk.utils.ChunkPropertiesDelegate;
import org.antlr.v4.runtime.ParserRuleContext;

public class PropertyExp extends Expression {

    public final PropertySpecifier propertySpecifier;

    public PropertyExp(ParserRuleContext context, PropertySpecifier propertySpecifier) {
        super(context);
        this.propertySpecifier = propertySpecifier;
    }

    public Value onEvaluate() throws HtException {

        // Getting a HyperCard property
        if (propertySpecifier.isGlobalPropertySpecifier()) {
            return ExecutionContext.getContext().getGlobalProperties().getProperty(propertySpecifier.property);
        }

        // Getting a menu property
        else if (propertySpecifier.isMenuItemPropertySpecifier()) {
            return MenuPropertiesDelegate.getProperty(propertySpecifier.property, propertySpecifier.menuItem);
        }

        // Getting the property of a chunk of text
        else if (propertySpecifier.isChunkPropertySpecifier()) {
            return ChunkPropertiesDelegate.getProperty(propertySpecifier.property, propertySpecifier.chunk, propertySpecifier.partExp.evaluateAsSpecifier());
        }

        // Getting the property of a part
        else {
            return ExecutionContext.getContext().getProperty(propertySpecifier.property, propertySpecifier.partExp.evaluateAsSpecifier());
        }

    }
}
