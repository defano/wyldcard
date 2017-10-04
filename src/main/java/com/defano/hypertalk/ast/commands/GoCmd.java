/*
 * StatGoCmd
 * hypertalk-java
 *
 * Created by Matt DeFano on 2/19/17 3:11 PM.
 * Copyright © 2017 Matt DeFano. All rights reserved.
 */

package com.defano.hypertalk.ast.commands;

import com.defano.hypercard.HyperCard;
import com.defano.hypercard.parts.bkgnd.BackgroundModel;
import com.defano.hypercard.parts.card.CardModel;
import com.defano.hypercard.parts.model.PartModel;
import com.defano.hypercard.runtime.context.ExecutionContext;
import com.defano.hypertalk.ast.expressions.DestinationExp;
import com.defano.hypertalk.ast.specifiers.VisualEffectSpecifier;
import com.defano.hypertalk.ast.specifiers.PartSpecifier;
import com.defano.hypertalk.ast.statements.Command;
import com.defano.hypertalk.exception.HtException;

public class GoCmd extends Command {

    private final DestinationExp destinationExp;
    private VisualEffectSpecifier visualEffect;

    public GoCmd(DestinationExp destinationExp) {
        this(destinationExp, null);
    }

    public GoCmd(DestinationExp destinationExp, VisualEffectSpecifier visualEffect) {
        super("go");

        this.destinationExp = destinationExp;
        this.visualEffect = visualEffect;
    }

    public void onExecute() throws HtException {

        if (visualEffect == null) {
            visualEffect = ExecutionContext.getContext().getVisualEffect();
        }

        // Special case: No destination means 'Go back'
        if (destinationExp == null) {
            HyperCard.getInstance().getStack().goBack(visualEffect);
        }

        else {
            PartSpecifier cardPart = destinationExp.evaluateAsSpecifier();
            PartModel model = HyperCard.getInstance().getStack().findPart(cardPart);

            int destinationIndex;
            if (model instanceof CardModel) {
                destinationIndex = HyperCard.getInstance().getStack().getStackModel().getIndexOfCard((CardModel) model);
            } else if (model instanceof BackgroundModel) {
                destinationIndex = HyperCard.getInstance().getStack().getStackModel().getIndexOfBackground(model.getId());
            } else {
                throw new IllegalStateException("Bug! Expected to find a card but got: " + model);
            }

            HyperCard.getInstance().getStack().goCard(destinationIndex, visualEffect);
        }

    }
}
