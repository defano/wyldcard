package com.defano.hypertalk.ast.commands;

import com.defano.hypercard.HyperCard;
import com.defano.hypercard.parts.PartException;
import com.defano.hypercard.parts.bkgnd.BackgroundModel;
import com.defano.hypercard.parts.card.CardModel;
import com.defano.hypercard.parts.model.PartModel;
import com.defano.hypercard.runtime.Interpreter;
import com.defano.hypercard.runtime.context.ExecutionContext;
import com.defano.hypertalk.ast.expressions.PartExp;
import com.defano.hypertalk.ast.specifiers.VisualEffectSpecifier;
import com.defano.hypertalk.ast.specifiers.PartSpecifier;
import com.defano.hypertalk.ast.statements.Command;
import com.defano.hypertalk.exception.HtException;
import com.defano.hypertalk.exception.HtSemanticException;
import org.antlr.v4.runtime.ParserRuleContext;

public class GoCmd extends Command {

    private final PartExp destinationExp;
    private VisualEffectSpecifier visualEffect;

    public GoCmd(ParserRuleContext context, PartExp destinationExp) {
        this(context, destinationExp, null);
    }

    public GoCmd(ParserRuleContext context, PartExp destinationExp, VisualEffectSpecifier visualEffect) {
        super(context, "go");

        this.destinationExp = destinationExp;
        this.visualEffect = visualEffect;
    }

    public void onExecute() throws HtException {

        if (visualEffect == null) {
            visualEffect = ExecutionContext.getContext().getVisualEffect();
        }

        // Special case: No destination means 'Go back'
        if (destinationExp == null) {
            HyperCard.getInstance().getStack().popCard(visualEffect);
        }

        else {
            try {
                Integer destinationIndex = evaluateAsCardIndex(destinationExp);
                if (destinationIndex == null) {
                    PartExp refPart = Interpreter.dereference(destinationExp.evaluate(), PartExp.class);
                    destinationIndex = evaluateAsCardIndex(refPart);

                    if (destinationIndex == null) {
                        throw new HtSemanticException("Cannot go there.");
                    }
                }

                HyperCard.getInstance().getStack().goCard(destinationIndex, visualEffect, true);
            } catch (PartException e) {
                // Nothing to do; going to a non-existent card or bkgnd has no effect
            }
        }
    }

    private Integer evaluateAsCardIndex(PartExp destination) throws HtException {
        PartSpecifier cardPart = destination.evaluateAsSpecifier();
        PartModel model = ExecutionContext.getContext().getPart(cardPart);

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
