package com.defano.hypertalk.ast.commands;

import com.defano.hypercard.HyperCard;
import com.defano.hypercard.parts.bkgnd.BackgroundModel;
import com.defano.hypercard.parts.card.CardModel;
import com.defano.hypercard.parts.model.PartModel;
import com.defano.hypercard.runtime.context.ExecutionContext;
import com.defano.hypertalk.ast.expressions.Expression;
import com.defano.hypertalk.ast.expressions.VisualEffectExp;
import com.defano.hypertalk.ast.specifiers.VisualEffectSpecifier;
import com.defano.hypertalk.ast.statements.Command;
import com.defano.hypertalk.exception.HtException;
import com.defano.hypertalk.exception.HtSemanticException;
import org.antlr.v4.runtime.ParserRuleContext;

public class GoCmd extends Command {

    private final Expression destinationExp;
    private VisualEffectExp visualEffectExp;

    public GoCmd(ParserRuleContext context, Expression destinationExp) {
        this(context, destinationExp, null);
    }

    public GoCmd(ParserRuleContext context, Expression destinationExp, VisualEffectExp visualEffectExp) {
        super(context, "go");

        this.destinationExp = destinationExp;
        this.visualEffectExp = visualEffectExp;
    }

    public void onExecute() throws HtException {

        VisualEffectSpecifier visualEffect;

        if (visualEffectExp == null) {
            visualEffect = ExecutionContext.getContext().getVisualEffect();
        } else {
            visualEffect = visualEffectExp.evaluateAsVisualEffect();
        }

        // Special case: No destination means 'Go back'
        if (destinationExp == null) {
            HyperCard.getInstance().getStack().popCard(visualEffect);
        }

        else {
            Integer cardIndex = evaluateAsCardIndex(destinationExp.evaluateAsPartModel(CardModel.class));
            if (cardIndex == null) {
                cardIndex = evaluateAsCardIndex(destinationExp.evaluateAsPartModel(BackgroundModel.class));
            }

            if (cardIndex == null) {
                throw new HtSemanticException("That doesn't refer to a card.");
            } else {
                HyperCard.getInstance().getStack().goCard(cardIndex, visualEffect, true);
            }
        }
    }

    private Integer evaluateAsCardIndex(PartModel model) {

        if (model == null) {
            return null;
        }

        int destinationIndex;
        if (model instanceof CardModel) {
            destinationIndex = HyperCard.getInstance().getStack().getStackModel().getIndexOfCard((CardModel) model);
        } else if (model instanceof BackgroundModel) {
            destinationIndex = HyperCard.getInstance().getStack().getStackModel().getIndexOfBackground(model.getId());
        } else {
            return null;
        }

        return destinationIndex;
    }

}
