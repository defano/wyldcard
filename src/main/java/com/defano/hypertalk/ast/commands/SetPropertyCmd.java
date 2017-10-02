package com.defano.hypertalk.ast.commands;

import com.defano.hypercard.parts.model.PartModel;
import com.defano.hypercard.runtime.context.ExecutionContext;
import com.defano.hypertalk.ast.common.Value;
import com.defano.hypertalk.ast.expressions.PartExp;
import com.defano.hypertalk.ast.statements.Command;
import com.defano.hypertalk.exception.HtException;
import com.defano.hypertalk.exception.HtSemanticException;

public class SetPropertyCmd extends Command {

    public final PartExp part;
    public final String property;
    public final Value value;

    public SetPropertyCmd(String property, Value value) {
        this(null, property, value);
    }

    public SetPropertyCmd(PartExp part, String property, Value value) {
        super("set");

        this.part = part;
        this.property = property;
        this.value = value;
    }

    @Override
    public void onExecute() throws HtException {
        if (this.part == null) {
            ExecutionContext.getContext().getGlobalProperties().setProperty(property, value);
        } else {
            PartModel model = ExecutionContext.getContext().getCurrentCard().findPart(part.evaluateAsSpecifier());

            if (model.hasProperty(property)) {
                model.setKnownProperty(property, value);
            } else {
                throw new HtSemanticException("Can't set that property on this part.");
            }
        }
    }
}
