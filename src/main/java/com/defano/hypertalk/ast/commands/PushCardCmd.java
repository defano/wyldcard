package com.defano.hypertalk.ast.commands;

import com.defano.hypercard.HyperCard;
import com.defano.hypercard.parts.bkgnd.BackgroundModel;
import com.defano.hypercard.parts.card.CardModel;
import com.defano.hypercard.parts.model.PartModel;
import com.defano.hypertalk.ast.breakpoints.Breakpoint;
import com.defano.hypertalk.ast.expressions.DestinationExp;
import com.defano.hypertalk.ast.statements.Command;
import com.defano.hypertalk.exception.HtException;
import com.defano.hypertalk.exception.HtSemanticException;
import org.antlr.v4.runtime.ParserRuleContext;

public class PushCardCmd extends Command {

    private final DestinationExp destinationExp;

    public PushCardCmd(ParserRuleContext context) {
        this(context, null);
    }

    public PushCardCmd(ParserRuleContext context, DestinationExp destinationExp) {
        super(context, "push");
        this.destinationExp = destinationExp;
    }

    @Override
    protected void onExecute() throws HtException, Breakpoint {
        if (destinationExp == null) {
            push(HyperCard.getInstance().getDisplayedCard().getId());
        } else {
            PartModel model = HyperCard.getInstance().getStack().findPart(destinationExp.evaluateAsSpecifier());
            if (model instanceof CardModel) {
                push(model.getId());
            } else if (model instanceof BackgroundModel) {
                int cardIndex = HyperCard.getInstance().getStack().getStackModel().getIndexOfBackground(model.getId());
                push(HyperCard.getInstance().getStack().getStackModel().getCardModel(cardIndex).getId());
            } else {
                throw new HtSemanticException("Cannot push this.");
            }
        }
    }

    private void push(int cardId) {
        HyperCard.getInstance().getStack().getStackModel().getBackStack().push(cardId);
    }
}