/*
 * ExpNumberOfFun
 * hypertalk-java
 *
 * Created by Matt DeFano on 2/19/17 3:11 PM.
 * Copyright © 2017 Matt DeFano. All rights reserved.
 */

/**
 * ExpNumberOfFun.java
 * @author matt.defano@gmail.com
 * 
 * Implementation of the built-in function "the number of"
 */

package com.defano.hypertalk.ast.functions;

import com.defano.hypercard.HyperCard;
import com.defano.hypercard.parts.CardLayer;
import com.defano.hypertalk.ast.common.ChunkType;
import com.defano.hypertalk.ast.common.Countable;
import com.defano.hypertalk.ast.common.PartType;
import com.defano.hypertalk.ast.common.Value;
import com.defano.hypertalk.ast.expressions.Expression;
import com.defano.hypertalk.exception.HtSemanticException;

public class ExpNumberOfFun extends Expression {

    public final Countable itemtype;
    public final Expression expression;

    public ExpNumberOfFun(Countable itemtype) {
        this.itemtype = itemtype;
        this.expression = null;
    }

    public ExpNumberOfFun(Countable itemtype, Expression expression) {
        this.itemtype = itemtype;
        this.expression = expression;
    }

    public Value evaluate() throws HtSemanticException {
        switch (itemtype) {
            case CHAR:
                return new Value(expression.evaluate().charCount());
            case WORD:
                return new Value(expression.evaluate().wordCount());
            case LINE:
                return new Value(expression.evaluate().lineCount());
            case ITEM:
                return new Value(expression.evaluate().itemCount());
            case CARD_PARTS:
                return new Value(HyperCard.getInstance().getCard().getPartCount(null, CardLayer.CARD_PARTS));
            case BKGND_PARTS:
                return new Value(HyperCard.getInstance().getCard().getPartCount(null, CardLayer.BACKGROUND_PARTS));
            case CARD_BUTTONS:
                return new Value(HyperCard.getInstance().getCard().getPartCount(PartType.BUTTON, CardLayer.CARD_PARTS));
            case BKGND_BUTTONS:
                return new Value(HyperCard.getInstance().getCard().getPartCount(PartType.BUTTON, CardLayer.BACKGROUND_PARTS));
            case CARD_FIELDS:
                return new Value(HyperCard.getInstance().getCard().getPartCount(PartType.FIELD, CardLayer.CARD_PARTS));
            case BKGND_FIELDS:
                return new Value(HyperCard.getInstance().getCard().getPartCount(PartType.FIELD, CardLayer.BACKGROUND_PARTS));
            default:
                throw new RuntimeException("Bug! Unimplemented countable item type: " + itemtype);
        }
    }
}
