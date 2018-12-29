package com.defano.hypertalk.ast.statements.commands;

import com.defano.wyldcard.WyldCard;
import com.defano.wyldcard.parts.model.PartModel;
import com.defano.wyldcard.runtime.DefaultWyldCardProperties;
import com.defano.wyldcard.runtime.context.ExecutionContext;
import com.defano.hypertalk.ast.model.Value;
import com.defano.hypertalk.ast.expressions.containers.PartExp;
import com.defano.hypertalk.ast.expressions.Expression;
import com.defano.hypertalk.ast.statements.Command;
import com.defano.hypertalk.exception.HtException;
import com.defano.hypertalk.exception.HtSemanticException;
import org.antlr.v4.runtime.ParserRuleContext;

public class SetPropertyCmd extends Command {

    public final Expression part;
    public final String property;
    public final Value value;

    public SetPropertyCmd(ParserRuleContext context, String property, Value value) {
        this(context, null, property, value);
    }

    public SetPropertyCmd(ParserRuleContext context, Expression part, String property, Value value) {
        super(context, "set");

        this.part = part;
        this.property = property;
        this.value = value;
    }

    @Override
    public void onExecute(ExecutionContext context) throws HtException {
        if (this.part == null) {
            WyldCard.getInstance().getWyldCardProperties().setProperty(context, property, value);
        } else {
            PartModel model = context.getPart(part.factor(context, PartExp.class, new HtSemanticException("Expected to find a part here.")).evaluateAsSpecifier(context));

            if (model.hasProperty(property)) {
                model.setKnownProperty(context, property, value);
            } else {
                throw new HtSemanticException("Can't set that property on this part.");
            }
        }
    }
}
