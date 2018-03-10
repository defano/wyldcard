package com.defano.hypertalk.ast.statements.commands;

import com.defano.wyldcard.runtime.context.ExecutionContext;
import com.defano.hypertalk.ast.model.Preposition;
import com.defano.hypertalk.ast.model.specifiers.PropertySpecifier;
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
                ExecutionContext.getContext().getGlobalProperties().setProperty(propertySpec.getProperty(), expression.evaluate());
            }

            // Setting the property of menu / menu item
            else if (propertySpec.isMenuItemPropertySpecifier()) {
                MenuPropertiesDelegate.setProperty(propertySpec.getProperty(), expression.evaluate(), propertySpec.getMenuItem());
            }

            // Setting the property of a chunk of text
            else if (propertySpec.isChunkPropertySpecifier()) {
                ChunkPropertiesDelegate.setProperty(propertySpec.getProperty(), expression.evaluate(), propertySpec.getChunk(), propertySpec.getPartExp().evaluateAsSpecifier());
            }

            // Setting the property of a part
            else {
                ExecutionContext.getContext().setProperty(propertySpec.getProperty(), propertySpec.getPartExp().evaluateAsSpecifier(), Preposition.INTO, null, expression.evaluate());
            }

        } catch (HtSemanticException e) {
            if (propertySpec.getPartExp() != null) {
                throw (e);
            } else {
                // When all else fails, set the value of a variable container
                ExecutionContext.getContext().setVariable(propertySpec.getProperty(), expression.evaluate());
            }
        }
    }
}
