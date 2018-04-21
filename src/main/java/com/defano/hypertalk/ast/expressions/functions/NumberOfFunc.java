package com.defano.hypertalk.ast.expressions.functions;

import com.defano.wyldcard.WyldCard;
import com.defano.wyldcard.menu.main.HyperCardMenuBar;
import com.defano.wyldcard.parts.bkgnd.BackgroundModel;
import com.defano.wyldcard.runtime.context.ExecutionContext;
import com.defano.hypertalk.ast.model.Countable;
import com.defano.hypertalk.ast.model.Owner;
import com.defano.hypertalk.ast.model.PartType;
import com.defano.hypertalk.ast.model.Value;
import com.defano.hypertalk.ast.expressions.Expression;
import com.defano.hypertalk.exception.HtException;
import com.defano.hypertalk.exception.HtSemanticException;
import com.defano.wyldcard.window.WindowManager;
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

    public Value onEvaluate(ExecutionContext context) throws HtException {
        switch (itemType) {
            case CHAR:
                return new Value(expression.evaluate(context).charCount(context));
            case WORD:
                return new Value(expression.evaluate(context).wordCount(context));
            case LINE:
            case MENU_ITEMS:
                return new Value(expression.evaluate(context).lineCount(context));
            case ITEM:
                return new Value(expression.evaluate(context).itemCount(context));
            case CARD_PARTS:
                return new Value(context.getCurrentCard().getCardModel().getPartCount(context, null, Owner.CARD));
            case BKGND_PARTS:
                return new Value(context.getCurrentCard().getCardModel().getPartCount(context, null, Owner.BACKGROUND));
            case CARD_BUTTONS:
                return new Value(context.getCurrentCard().getCardModel().getPartCount(context, PartType.BUTTON, Owner.CARD));
            case BKGND_BUTTONS:
                return new Value(context.getCurrentCard().getCardModel().getPartCount(context, PartType.BUTTON, Owner.BACKGROUND));
            case CARD_FIELDS:
                return new Value(context.getCurrentCard().getCardModel().getPartCount(context, PartType.FIELD, Owner.CARD));
            case BKGND_FIELDS:
                return new Value(context.getCurrentCard().getCardModel().getPartCount(context, PartType.FIELD, Owner.BACKGROUND));
            case MENUS:
                return new Value(HyperCardMenuBar.getInstance().getMenuCount());
            case CARDS:
                return new Value(context.getActiveStack().getCardCountProvider().blockingFirst());
            case MARKED_CARDS:
                return new Value(context.getActiveStack().getStackModel().getMarkedCards(context).size());
            case BKGNDS:
                return new Value(context.getActiveStack().getStackModel().getBackgroundCount());
            case CARDS_IN_BKGND:
                BackgroundModel model = expression.partFactor(context, BackgroundModel.class, new HtSemanticException("No such background."));
                return new Value(context.getActiveStack().getStackModel().getCardsInBackground(model.getId(context)).size());
            case WINDOWS:
                return new Value(WindowManager.getInstance().getWindows().size());
            default:
                throw new RuntimeException("Bug! Unimplemented countable item type: " + itemType);
        }
    }
}
