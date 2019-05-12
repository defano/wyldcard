package com.defano.hypertalk.ast.statement.command;

import com.defano.wyldcard.WyldCard;
import com.defano.wyldcard.part.model.PartModel;
import com.defano.wyldcard.runtime.ExecutionContext;
import com.defano.hypertalk.ast.model.Value;
import com.defano.hypertalk.ast.expression.container.PartExp;
import com.defano.hypertalk.ast.expression.Expression;
import com.defano.hypertalk.ast.statement.Command;
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
            WyldCard.getInstance().getWyldCardPart().trySet(context, property, value);
        } else {
            PartModel model = context.getPart(part.factor(context, PartExp.class, new HtSemanticException("Expected to find a part here.")).evaluateAsSpecifier(context));

            if (model.hasProperty(property)) {
                model.set(context, property, value);
            } else {
                throw new HtSemanticException("Can't set that property on this part.");
            }
        }
    }
}
