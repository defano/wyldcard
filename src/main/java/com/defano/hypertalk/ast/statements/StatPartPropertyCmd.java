package com.defano.hypertalk.ast.statements;

import com.defano.hypercard.HyperCard;
import com.defano.hypercard.parts.model.PartModel;
import com.defano.hypertalk.ast.common.Value;
import com.defano.hypertalk.ast.expressions.ExpPart;
import com.defano.hypertalk.exception.HtException;

public class StatPartPropertyCmd extends Statement {

    public final ExpPart part;
    public final String property;
    public final Value value;

    public StatPartPropertyCmd(ExpPart part, String property, Value value) {
        this.part = part;
        this.property = property;
        this.value = value;
    }

    @Override
    public void execute() throws HtException {
        HyperCard.getInstance().getCard().findPart(part.evaluateAsSpecifier()).getPartModel().setKnownProperty(property, value);
    }
}
