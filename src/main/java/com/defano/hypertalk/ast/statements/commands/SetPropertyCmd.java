package com.defano.hypertalk.ast.statements.commands;

import com.defano.wyldcard.parts.model.PartModel;
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
    public void onExecute() throws HtException {
        if (this.part == null) {
            ExecutionContext.getContext().getGlobalProperties().setProperty(property, value);
        } else {
            PartModel model = ExecutionContext.getContext().getPart(part.factor(PartExp.class, new HtSemanticException("Expected to find a part here.")).evaluateAsSpecifier());

            if (model.hasProperty(property)) {
                model.setKnownProperty(property, value);
            } else {
                throw new HtSemanticException("Can't set that property on this part.");
            }
        }
    }
}
