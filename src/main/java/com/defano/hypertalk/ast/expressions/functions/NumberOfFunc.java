package com.defano.hypertalk.ast.expressions.functions;

import com.defano.wyldcard.WyldCard;
import com.defano.wyldcard.menu.HyperCardMenuBar;
import com.defano.wyldcard.parts.bkgnd.BackgroundModel;
import com.defano.wyldcard.runtime.context.ExecutionContext;
import com.defano.hypertalk.ast.model.Countable;
import com.defano.hypertalk.ast.model.Owner;
import com.defano.hypertalk.ast.model.PartType;
import com.defano.hypertalk.ast.model.Value;
import com.defano.hypertalk.ast.expressions.Expression;
import com.defano.hypertalk.exception.HtException;
import com.defano.hypertalk.exception.HtSemanticException;
import org.antlr.v4.runtime.ParserRuleContext;

/**
 * Implementation of a HyperTalk function that counts the number of elements in a given container.
 */
public class NumberOfFunc extends Expression {

    public final Countable itemType;
    public final Expression expression;

    public NumberOfFunc(ParserRuleContext context, Countable itemType) {
        super(context);
        this.itemType = itemType;
        this.expression = null;
    }

    public NumberOfFunc(ParserRuleContext context, Countable itemType, Expression expression) {
        super(context);
        this.itemType = itemType;
        this.expression = expression;
    }

    public Value onEvaluate() throws HtException {
        switch (itemType) {
            case CHAR:
                return new Value(expression.evaluate().charCount());
            case WORD:
                return new Value(expression.evaluate().wordCount());
            case LINE:
            case MENU_ITEMS:
                return new Value(expression.evaluate().lineCount());
            case ITEM:
                return new Value(expression.evaluate().itemCount());
            case CARD_PARTS:
                return new Value(ExecutionContext.getContext().getCurrentCard().getCardModel().getPartCount(null, Owner.CARD));
            case BKGND_PARTS:
                return new Value(ExecutionContext.getContext().getCurrentCard().getCardModel().getPartCount(null, Owner.BACKGROUND));
            case CARD_BUTTONS:
                return new Value(ExecutionContext.getContext().getCurrentCard().getCardModel().getPartCount(PartType.BUTTON, Owner.CARD));
            case BKGND_BUTTONS:
                return new Value(ExecutionContext.getContext().getCurrentCard().getCardModel().getPartCount(PartType.BUTTON, Owner.BACKGROUND));
            case CARD_FIELDS:
                return new Value(ExecutionContext.getContext().getCurrentCard().getCardModel().getPartCount(PartType.FIELD, Owner.CARD));
            case BKGND_FIELDS:
                return new Value(ExecutionContext.getContext().getCurrentCard().getCardModel().getPartCount(PartType.FIELD, Owner.BACKGROUND));
            case MENUS:
                return new Value(HyperCardMenuBar.getInstance().getMenuCount());
            case CARDS:
                return new Value(WyldCard.getInstance().getActiveStack().getCardCountProvider().blockingFirst());
            case MARKED_CARDS:
                return new Value(WyldCard.getInstance().getActiveStack().getStackModel().getMarkedCards().size());
            case BKGNDS:
                return new Value(WyldCard.getInstance().getActiveStack().getStackModel().getBackgroundCount());
            case CARDS_IN_BKGND:
                BackgroundModel model = expression.partFactor(BackgroundModel.class, new HtSemanticException("No such background."));
                return new Value(WyldCard.getInstance().getActiveStack().getStackModel().getCardsInBackground(model.getId()).size());
            default:
                throw new RuntimeException("Bug! Unimplemented countable item type: " + itemType);
        }
    }
}
