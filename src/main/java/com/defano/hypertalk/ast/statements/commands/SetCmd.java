package com.defano.hypertalk.ast.statements.commands;

import com.defano.wyldcard.WyldCard;
import com.defano.wyldcard.runtime.DefaultWyldCardProperties;
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
    
    public void onExecute(ExecutionContext context) throws HtException {
        try {
            
            // Setting the property of HyperCard
            if (propertySpec.isGlobalPropertySpecifier(context)) {
                WyldCard.getInstance().getWyldCardProperties().setProperty(context, propertySpec.getProperty(), expression.evaluate(context));
            }

            // Setting the property of menu / menu item
            else if (propertySpec.isMenuItemPropertySpecifier(context)) {
                MenuPropertiesDelegate.setProperty(context, propertySpec.getProperty(), expression.evaluate(context), propertySpec.getMenuItem(context));
            }

            // Setting the property of a chunk of text
            else if (propertySpec.isChunkPropertySpecifier(context)) {
                ChunkPropertiesDelegate.setProperty(context, propertySpec.getProperty(), expression.evaluate(context), propertySpec.getChunk(context), propertySpec.getPartExp(context).evaluateAsSpecifier(context));
            }

            // Setting the property of a part
            else {
                context.setProperty(propertySpec.getProperty(), propertySpec.getPartExp(context).evaluateAsSpecifier(context), Preposition.INTO, null, expression.evaluate(context));
            }

        } catch (HtSemanticException e) {
            if (propertySpec.getPartExp(context) != null) {
                throw (e);
            } else {
                // When all else fails, set the value of a variable container
                context.setVariable(propertySpec.getProperty(), expression.evaluate(context));
            }
        }
    }
}
