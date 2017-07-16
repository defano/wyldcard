package com.defano.hypertalk.ast.statements;

import com.defano.hypercard.HyperCard;
import com.defano.hypercard.parts.Part;
import com.defano.hypercard.parts.PartException;
import com.defano.hypertalk.ast.expressions.ExpPart;
import com.defano.hypertalk.exception.HtSemanticException;

public class StatDeleteCmd extends Statement {

    private final ExpPart part;

    public StatDeleteCmd(ExpPart part) {
        this.part = part;
    }

    @Override
    public void execute() throws HtSemanticException {
        try {
            Part p = HyperCard.getInstance().getCard().findPart(part.evaluateAsSpecifier());
            HyperCard.getInstance().getCard().removePart(p);
        } catch (PartException e) {
            throw new HtSemanticException("No such " + part.toString() + " to delete", e);
        }
    }
}
