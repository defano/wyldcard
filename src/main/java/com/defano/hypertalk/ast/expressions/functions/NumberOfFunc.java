package com.defano.hypertalk.ast.expressions.functions;

import com.defano.wyldcard.menubar.main.HyperCardMenuBar;
import com.defano.wyldcard.parts.bkgnd.BackgroundModel;
import com.defano.wyldcard.parts.card.CardModel;
import com.defano.wyldcard.parts.finder.LayeredPartFinder;
import com.defano.wyldcard.parts.stack.StackModel;
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
                return new Value(getScopedLayeredPart(context).getPartCount(context, null, Owner.CARD));
            case BKGND_PARTS:
                return new Value(getScopedLayeredPart(context).getPartCount(context, null, Owner.BACKGROUND));
            case CARD_BUTTONS:
                return new Value(getScopedLayeredPart(context).getPartCount(context, PartType.BUTTON, Owner.CARD));
            case BKGND_BUTTONS:
                return new Value(getScopedLayeredPart(context).getPartCount(context, PartType.BUTTON, Owner.BACKGROUND));
            case CARD_FIELDS:
                return new Value(getScopedLayeredPart(context).getPartCount(context, PartType.FIELD, Owner.CARD));
            case BKGND_FIELDS:
                return new Value(getScopedLayeredPart(context).getPartCount(context, PartType.FIELD, Owner.BACKGROUND));
            case MENUS:
                return new Value(HyperCardMenuBar.getInstance().getMenuCount());
            case CARDS:
                return countCards(context);
            case MARKED_CARDS:
                return countMarkedCards(context);
            case BKGNDS:
                return countBackgrounds(context);
            case WINDOWS:
                return new Value(WindowManager.getInstance().getFrames(false).size());
            default:
                throw new RuntimeException("Bug! Unimplemented countable item type: " + itemType);
        }
    }

    private LayeredPartFinder getScopedLayeredPart(ExecutionContext context) throws HtException {
        if (expression == null) {
            return context.getCurrentCard().getCardModel();
        } else {
            BackgroundModel bkgnd = expression.partFactor(context, BackgroundModel.class);
            if (bkgnd != null) {
                return bkgnd;
            } else {
                return expression.partFactor(context, CardModel.class, new HtSemanticException("No such stack or background."));
            }
        }
    }

    private Value countMarkedCards(ExecutionContext context) throws HtException {
        if (expression == null) {
            return new Value(context.getCurrentStack().getStackModel().getMarkedCards(context).size());
        } else {
            BackgroundModel bkgnd = expression.partFactor(context, BackgroundModel.class);
            if (bkgnd != null) {
                return new Value(bkgnd.getCardModels(context).stream().filter(p -> p.isMarked(context)).count());
            } else {
                return new Value(expression.partFactor(context, StackModel.class, new HtSemanticException("No such stack or background.")).getMarkedCards(context).size());
            }
        }
    }

    private Value countBackgrounds(ExecutionContext context) throws HtException {
        if (expression == null) {
            return new Value(context.getCurrentStack().getStackModel().getBackgroundCount());
        } else {
            BackgroundModel bkgnd = expression.partFactor(context, BackgroundModel.class);
            if (bkgnd != null) {
                return new Value(bkgnd.getCardModels(context).size());
            } else {
                return new Value(expression.partFactor(context, StackModel.class, new HtSemanticException("No such stack or background.")).getBackgroundCount());
            }
        }
    }

    private Value countCards(ExecutionContext context) throws HtException {
        if (expression == null) {
            return new Value(context.getCurrentStack().getCardCountProvider().blockingFirst());
        } else {
            BackgroundModel bkgnd = expression.partFactor(context, BackgroundModel.class);
            if (bkgnd != null) {
                return new Value(bkgnd.getCardModels(context).size());
            } else {
                return new Value(expression.partFactor(context, StackModel.class, new HtSemanticException("No such stack or background.")).getCardCount());
            }
        }
    }
}
