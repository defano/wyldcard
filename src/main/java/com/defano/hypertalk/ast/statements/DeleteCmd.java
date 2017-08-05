package com.defano.hypertalk.ast.statements;

import com.defano.hypercard.HyperCard;
import com.defano.hypercard.parts.PartException;
import com.defano.hypercard.parts.model.PartModel;
import com.defano.hypertalk.ast.expressions.PartExp;
import com.defano.hypertalk.exception.HtSemanticException;

public class DeleteCmd extends Statement {

    private final PartExp part;

    public DeleteCmd(PartExp part) {
        this.part = part;
    }

    @Override
    public void execute() throws HtSemanticException {
        try {
            PartModel p = HyperCard.getInstance().getCard().findPart(part.evaluateAsSpecifier());
            HyperCard.getInstance().getCard().removePart(p);
        } catch (PartException e) {
            throw new HtSemanticException("No such " + part.toString() + " to delete", e);
        }
    }
}
