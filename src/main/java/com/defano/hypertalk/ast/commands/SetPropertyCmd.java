package com.defano.hypertalk.ast.commands;

import com.defano.hypercard.HyperCard;
import com.defano.hypercard.runtime.context.ExecutionContext;
import com.defano.hypertalk.ast.common.Value;
import com.defano.hypertalk.ast.expressions.PartExp;
import com.defano.hypertalk.ast.statements.Command;
import com.defano.hypertalk.exception.HtException;

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
            HyperCard.getInstance().getCard().findPart(part.evaluateAsSpecifier()).setKnownProperty(property, value);
        }
    }
}
