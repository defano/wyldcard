package com.defano.hypertalk.ast.statements.commands;

import com.defano.wyldcard.WyldCard;
import com.defano.wyldcard.parts.bkgnd.BackgroundModel;
import com.defano.wyldcard.parts.card.CardModel;
import com.defano.wyldcard.parts.model.PartModel;
import com.defano.hypertalk.ast.expressions.Expression;
import com.defano.hypertalk.ast.statements.Command;
import com.defano.hypertalk.exception.HtException;
import com.defano.hypertalk.exception.HtSemanticException;
import org.antlr.v4.runtime.ParserRuleContext;

public class PushCardCmd extends Command {

    private final Expression destinationExp;

    public PushCardCmd(ParserRuleContext context) {
        this(context, null);
    }

    public PushCardCmd(ParserRuleContext context, Expression destinationExp) {
        super(context, "push");
        this.destinationExp = destinationExp;
    }

    @Override
    protected void onExecute() throws HtException {
        if (destinationExp == null) {
            push(WyldCard.getInstance().getActiveStackDisplayedCard().getId());
        } else {

            Integer pushCardId = null;
            PartModel destinationModel = destinationExp.partFactor(CardModel.class);
            if (destinationModel == null) {
                destinationExp.partFactor(BackgroundModel.class);
            }

            if (destinationModel != null) {
                pushCardId = evaluateAsCardId(destinationModel);
            }

            if (pushCardId != null) {
                push(pushCardId);
            } else {
                throw new HtSemanticException("Can't push that.");
            }
        }
    }

    private Integer evaluateAsCardId(PartModel model) throws HtException {
        if (model instanceof CardModel) {
            return model.getId();
        } else if (model instanceof BackgroundModel) {
            int cardIndex = WyldCard.getInstance().getActiveStack().getStackModel().getIndexOfBackground(model.getId());
            return WyldCard.getInstance().getActiveStack().getStackModel().getCardModel(cardIndex).getId();
        } else {
            return null;
        }
    }

    private void push(int cardId) {
        WyldCard.getInstance().getActiveStack().getStackModel().getBackStack().push(cardId);
    }
}
