package com.defano.hypertalk.ast.statement.command;

import com.defano.wyldcard.WyldCard;
import com.defano.wyldcard.runtime.ExecutionContext;
import com.defano.hypertalk.ast.model.enums.Preposition;
import com.defano.hypertalk.ast.model.specifier.PropertySpecifier;
import com.defano.hypertalk.ast.expression.Expression;
import com.defano.hypertalk.ast.statement.Command;
import com.defano.hypertalk.exception.HtException;
import com.defano.hypertalk.delegate.MenuPropertiesDelegate;
import com.defano.hypertalk.delegate.ChunkPropertiesDelegate;
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
        // Setting the property of HyperCard
        if (propertySpec.isGlobalPropertySpecifier(context)) {
            WyldCard.getInstance().getWyldCardPart().trySet(context, propertySpec.getProperty(), expression.evaluate(context));
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
    }
}
