package com.defano.hypertalk.ast.commands;

import com.defano.hypercard.runtime.context.ExecutionContext;
import com.defano.hypertalk.ast.common.Preposition;
import com.defano.hypertalk.ast.specifiers.PropertySpecifier;
import com.defano.hypertalk.ast.expressions.Expression;
import com.defano.hypertalk.ast.statements.Command;
import com.defano.hypertalk.exception.HtException;
import com.defano.hypertalk.exception.HtSemanticException;
import com.defano.hypertalk.utils.MenuPropertiesDelegate;
import com.defano.hypertalk.utils.ChunkPropertiesDelegate;
import org.antlr.v4.runtime.ParserRuleContext;

public class SetCmd extends Command {

    public final Expression expression;
    public final PropertySpecifier propertySpec;

    public SetCmd(ParserRuleContext context, PropertySpecifier propertySpec, Expression expression) {
        super(context, "set");

        this.propertySpec = propertySpec;
        this.expression = expression;
    }
    
    public void onExecute () throws HtException {
        try {
            
            // Setting the property of HyperCard
            if (propertySpec.isGlobalPropertySpecifier()) {
                ExecutionContext.getContext().getGlobalProperties().setProperty(propertySpec.property, expression.evaluate());
            }

            // Setting the property of menu / menu item
            else if (propertySpec.isMenuItemPropertySpecifier()) {
                MenuPropertiesDelegate.setProperty(propertySpec.property, expression.evaluate(), propertySpec.menuItem);
            }

            else if (propertySpec.isChunkPropertySpecifier()) {
                ChunkPropertiesDelegate.setProperty(propertySpec.property, expression.evaluate(), propertySpec.chunk, propertySpec.partExp.evaluateAsSpecifier());
            }

            // Setting the property of a part
            else {
                ExecutionContext.getContext().set(propertySpec.property, propertySpec.partExp.evaluateAsSpecifier(), Preposition.INTO, null, expression.evaluate());
            }

        } catch (HtSemanticException e) {
            if (propertySpec.partExp != null) {
                throw (e);
            } else {
                // When all else fails, set the value of a variable container
                ExecutionContext.getContext().set(propertySpec.property, expression.evaluate());
            }
        }
    }
}
