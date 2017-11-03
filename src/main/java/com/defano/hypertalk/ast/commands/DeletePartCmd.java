package com.defano.hypertalk.ast.commands;

import com.defano.hypercard.HyperCard;
import com.defano.hypercard.parts.PartException;
import com.defano.hypercard.parts.card.CardPart;
import com.defano.hypercard.parts.model.PartModel;
import com.defano.hypercard.runtime.context.ExecutionContext;
import com.defano.hypertalk.ast.expressions.PartExp;
import com.defano.hypertalk.ast.specifiers.PartSpecifier;
import com.defano.hypertalk.ast.specifiers.RemotePartSpecifier;
import com.defano.hypertalk.ast.statements.Command;
import com.defano.hypertalk.exception.HtException;
import com.defano.hypertalk.exception.HtSemanticException;
import org.antlr.v4.runtime.ParserRuleContext;

public class DeletePartCmd extends Command {

    private final PartExp part;

    public DeletePartCmd(ParserRuleContext context, PartExp part) {
        super(context, "delete");
        this.part = part;
    }

    @Override
    public void onExecute() throws HtException {
        try {
            PartSpecifier ps = part.evaluateAsSpecifier();
            PartModel p = ExecutionContext.getContext().getPart(ps);

            CardPart owner;
            if (ps instanceof RemotePartSpecifier) {
                owner = HyperCard.getInstance().getStack().findRemotePartOwner((RemotePartSpecifier) ps);
            } else {
                owner = ExecutionContext.getContext().getCurrentCard();
            }

            owner.removePart(p);
        } catch (PartException e) {
            throw new HtSemanticException("No such " + part.toString() + " to delete", e);
        }
    }
}
