package com.defano.hypertalk.ast.statements.commands;

import com.defano.wyldcard.WyldCard;
import com.defano.wyldcard.parts.bkgnd.BackgroundModel;
import com.defano.wyldcard.parts.card.CardModel;
import com.defano.wyldcard.parts.model.PartModel;
import com.defano.wyldcard.runtime.context.ExecutionContext;
import com.defano.wyldcard.util.ThreadUtils;
import com.defano.hypertalk.ast.expressions.Expression;
import com.defano.hypertalk.ast.expressions.VisualEffectExp;
import com.defano.hypertalk.ast.model.specifiers.VisualEffectSpecifier;
import com.defano.hypertalk.ast.statements.Command;
import com.defano.hypertalk.exception.HtException;
import com.defano.hypertalk.exception.HtSemanticException;
import org.antlr.v4.runtime.ParserRuleContext;

public class GoCmd extends Command {

    private final Expression destinationExp;
    private Expression visualEffectExp;

    public GoCmd(ParserRuleContext context, Expression destinationExp) {
        this(context, destinationExp, null);
    }

    public GoCmd(ParserRuleContext context, Expression destinationExp, Expression visualEffectExp) {
        super(context, "go");

        this.destinationExp = destinationExp;
        this.visualEffectExp = visualEffectExp;
    }

    public void onExecute(ExecutionContext context) throws HtException {

        VisualEffectSpecifier visualEffect;

        if (visualEffectExp == null) {
            visualEffect = context.getVisualEffect();
        } else {
            visualEffect = visualEffectExp.factor(context, VisualEffectExp.class, new HtSemanticException("Not a visual effect.")).effectSpecifier;
        }

        // Special case: No destination means 'Go back'
        if (destinationExp == null) {
            WyldCard.getInstance().getActiveStack().popCard(context, visualEffect);
        }

        else {
            Integer cardIndex = evaluateAsCardIndex(context, destinationExp.partFactor(context, CardModel.class));
            if (cardIndex == null) {
                cardIndex = evaluateAsCardIndex(context, destinationExp.partFactor(context, BackgroundModel.class));
            }

            if (cardIndex == null) {
                throw new HtSemanticException("No such card.");
            } else {
                Integer finalCardIndex = cardIndex;
                ThreadUtils.invokeAndWaitAsNeeded(() -> WyldCard.getInstance().getActiveStack().goCard(context, finalCardIndex, visualEffect, true));
            }
        }
    }

    private Integer evaluateAsCardIndex(ExecutionContext context, PartModel model) {

        if (model == null) {
            return null;
        }

        int destinationIndex;
        if (model instanceof CardModel) {
            destinationIndex = WyldCard.getInstance().getActiveStack().getStackModel().getIndexOfCard((CardModel) model);
        } else if (model instanceof BackgroundModel) {
            destinationIndex = WyldCard.getInstance().getActiveStack().getStackModel().getIndexOfBackground(model.getId(context));
        } else {
            return null;
        }

        return destinationIndex;
    }

}
