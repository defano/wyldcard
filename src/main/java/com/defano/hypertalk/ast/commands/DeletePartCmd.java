package com.defano.hypertalk.ast.commands;

import com.defano.hypercard.HyperCard;
import com.defano.hypercard.parts.PartException;
import com.defano.hypercard.parts.model.PartModel;
import com.defano.hypertalk.ast.expressions.PartExp;
import com.defano.hypertalk.ast.statements.Command;
import com.defano.hypertalk.exception.HtSemanticException;

public class DeletePartCmd extends Command {

    private final PartExp part;

    public DeletePartCmd(PartExp part) {
        super("delete");
        this.part = part;
    }

    @Override
    public void onExecute() throws HtSemanticException {
        try {
            PartModel p = HyperCard.getInstance().getCard().findPart(part.evaluateAsSpecifier());
            HyperCard.getInstance().getCard().removePart(p);
        } catch (PartException e) {
            throw new HtSemanticException("No such " + part.toString() + " to delete", e);
        }
    }
}
