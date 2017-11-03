package com.defano.hypertalk.ast.commands;

import com.defano.hypercard.parts.model.PartModel;
import com.defano.hypercard.runtime.context.ExecutionContext;
import com.defano.hypertalk.ast.common.Value;
import com.defano.hypertalk.ast.expressions.PartExp;
import com.defano.hypertalk.ast.statements.Command;
import com.defano.hypertalk.exception.HtException;
import com.defano.hypertalk.exception.HtSemanticException;
import org.antlr.v4.runtime.ParserRuleContext;

public class SetPropertyCmd extends Command {

    public final PartExp part;
    public final String property;
    public final Value value;

    public SetPropertyCmd(ParserRuleContext context, String property, Value value) {
        this(context, null, property, value);
    }

    public SetPropertyCmd(ParserRuleContext context, PartExp part, String property, Value value) {
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
            PartModel model = ExecutionContext.getContext().getPart(part.evaluateAsSpecifier());

            if (model.hasProperty(property)) {
                model.setKnownProperty(property, value);
            } else {
                throw new HtSemanticException("Can't set that property on this part.");
            }
        }
    }
}
