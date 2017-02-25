package com.defano.hypertalk.ast.statements;

import com.defano.hypercard.HyperCard;
import com.defano.hypercard.parts.model.AbstractPartModel;
import com.defano.hypertalk.ast.common.Value;
import com.defano.hypertalk.ast.expressions.ExpPart;
import com.defano.hypertalk.exception.HtException;

public class StatHideCmd extends Statement {

    public final ExpPart part;

    public StatHideCmd(ExpPart part) {
        this.part = part;
    }

    @Override
    public void execute() throws HtException {
        HyperCard.getInstance().getCard().getPart(part.evaluateAsSpecifier()).getPartModel().setKnownProperty(AbstractPartModel.PROP_VISIBLE, new Value(false));
    }
}
