package com.defano.hypertalk.ast.expression.function;

import com.defano.hypertalk.ast.expression.Expression;
import com.defano.hypertalk.ast.model.Value;
import com.defano.hypertalk.ast.model.enums.Owner;
import com.defano.hypertalk.ast.model.specifier.PartSpecifier;
import com.defano.hypertalk.exception.HtException;
import com.defano.wyldcard.WyldCard;
import com.defano.wyldcard.runtime.ExecutionContext;
import org.antlr.v4.runtime.ParserRuleContext;

public class ClickLineFunc extends Expression {

    public ClickLineFunc(ParserRuleContext context) {
        super(context);
    }

    @Override
    protected Value onEvaluate(ExecutionContext context) throws HtException {
        Value lineValue = WyldCard.getInstance().getSelectionManager().getClickLine();
        PartSpecifier target = context.getTarget();
        if (target.getType().hypertalkName.equals("field")){
            Owner owner = target.getOwner();
            String ownerHCName = owner.hyperTalkName;
            Value numberValue = context.getProperty("number", target);

            // eg. "line 2 of card field 1"
            StringBuilder sb = new StringBuilder().append("line ").append(lineValue).append(" of ").
                    append(ownerHCName.toLowerCase()).append(" ").append(target.getType().hypertalkName).append(" ").
                    append(numberValue);
            return new Value(sb);
        } else {
            throw new HtException("the clickLine can only be sent to a Field");
        }
    }
}
