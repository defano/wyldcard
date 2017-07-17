package com.defano.hypertalk.ast.statements;

import com.defano.hypercard.HyperCard;
import com.defano.hypertalk.ast.common.Value;
import com.defano.hypertalk.ast.expressions.PartExp;
import com.defano.hypertalk.exception.HtException;

public class PartPropertyCmd extends Statement {

    public final PartExp part;
    public final String property;
    public final Value value;

    public PartPropertyCmd(PartExp part, String property, Value value) {
        this.part = part;
        this.property = property;
        this.value = value;
    }

    @Override
    public void execute() throws HtException {
        HyperCard.getInstance().getCard().findPart(part.evaluateAsSpecifier()).getPartModel().setKnownProperty(property, value);
    }
}
