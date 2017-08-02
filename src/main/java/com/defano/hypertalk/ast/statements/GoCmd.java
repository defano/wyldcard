/*
 * StatGoCmd
 * hypertalk-java
 *
 * Created by Matt DeFano on 2/19/17 3:11 PM.
 * Copyright © 2017 Matt DeFano. All rights reserved.
 */

package com.defano.hypertalk.ast.statements;

import com.defano.hypercard.HyperCard;
import com.defano.hypertalk.ast.common.Destination;
import com.defano.hypertalk.ast.common.Ordinal;
import com.defano.hypertalk.ast.common.VisualEffectSpecifier;
import com.defano.hypertalk.exception.HtException;
import com.defano.hypertalk.exception.HtSemanticException;

public class GoCmd extends Statement {

    private final Destination destination;
    private final VisualEffectSpecifier visualEffect;

    public GoCmd(Destination destination) {
        this(destination, null);
    }

    public GoCmd(Destination destination, VisualEffectSpecifier visualEffect) {
        this.destination = destination;
        this.visualEffect = visualEffect;
    }

    public void execute() throws HtException {

        // Special case: Go back
        if (destination == null) {
            HyperCard.getInstance().getStack().goBack(visualEffect);
        }

        // e.g., "go to first card", "go ninth card"
        else if (destination.ordinal != null) {
            if (destination.ordinal == Ordinal.FIRST) {
                HyperCard.getInstance().getStack().goFirstCard(visualEffect);
            } else if (destination.ordinal == Ordinal.LAST) {
                HyperCard.getInstance().getStack().goLastCard(visualEffect);
            } else {
                int destCard = destination.ordinal.intValue();
                if (destCard == Ordinal.MIDDLE.intValue()) {
                    destCard = HyperCard.getInstance().getStack().getStackModel().getCardCount() / 2;
                }

                if (destCard < 0 || destCard > HyperCard.getInstance().getStack().getStackModel().getCardCount()) {
                    throw new HtSemanticException("No card numbered " + destCard + " in this stack.");
                }

                HyperCard.getInstance().getStack().goCard(destCard, visualEffect);
            }
        }

        // e.g., "go next card", "go back"
        else if (destination.position != null) {
            switch (destination.position) {
                case NEXT:
                    HyperCard.getInstance().getStack().goNextCard(visualEffect);
                    break;
                case PREV:
                    HyperCard.getInstance().getStack().goPrevCard(visualEffect);
                    break;
                case THIS:
                    HyperCard.getInstance().getStack().goThisCard(visualEffect);
                    break;
            }
        }

        // e.g., "go to card 983", "go myCardNumber"
        else if (destination.expression != null) {
            int destCard = destination.expression.evaluate().integerValue() - 1;
            if (destCard < 0 || destCard >= HyperCard.getInstance().getStack().getStackModel().getCardCount()) {
                throw new HtSemanticException("No card numbered " + (destCard + 1) + " in this stack.");
            }

            HyperCard.getInstance().getStack().goCard(destCard, visualEffect);
        }
    }
}
