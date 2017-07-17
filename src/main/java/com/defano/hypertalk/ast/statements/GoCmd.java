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
import com.defano.hypertalk.ast.common.Position;
import com.defano.hypertalk.exception.HtException;
import com.defano.hypertalk.exception.HtSemanticException;

public class GoCmd extends Statement {

    private final Destination destination;

    public GoCmd(Destination destination) {
        this.destination = destination;
    }

    public void execute() throws HtException {
        if (destination.ordinal != null) {
            if (destination.ordinal == Ordinal.FIRST) {
                HyperCard.getInstance().getStack().goFirstCard();
            } else if (destination.ordinal == Ordinal.LAST) {
                HyperCard.getInstance().getStack().goLastCard();
            } else {
                int destCard = destination.ordinal.intValue();
                if (destCard == Ordinal.MIDDLE.intValue()) {
                    destCard = HyperCard.getInstance().getStack().getStackModel().getCardCount() / 2;
                }

                if (destCard < 0 || destCard > HyperCard.getInstance().getStack().getStackModel().getCardCount()) {
                    throw new HtSemanticException("No card numbered " + destCard + " in this stack.");
                }

                HyperCard.getInstance().getStack().goCard(destCard);
            }
        }

        else if (destination.position != null) {
            if (destination.position == Position.NEXT) {
                HyperCard.getInstance().getStack().goNextCard();
            } else {
                HyperCard.getInstance().getStack().goPrevCard();
            }
        }

        else if (destination.expression != null) {
            int destCard = destination.expression.evaluate().integerValue() - 1;
            if (destCard < 0 || destCard >= HyperCard.getInstance().getStack().getStackModel().getCardCount()) {
                throw new HtSemanticException("No card numbered " + (destCard + 1) + " in this stack.");
            }

            HyperCard.getInstance().getStack().goCard(destCard);
        }
    }
}
