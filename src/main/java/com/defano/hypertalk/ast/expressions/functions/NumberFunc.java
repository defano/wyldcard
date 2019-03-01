package com.defano.hypertalk.ast.expressions.functions;

import com.defano.hypertalk.ast.expressions.CountableExp;
import com.defano.hypertalk.ast.expressions.Expression;
import com.defano.hypertalk.ast.model.Countable;
import com.defano.hypertalk.ast.model.Owner;
import com.defano.hypertalk.ast.model.PartType;
import com.defano.hypertalk.ast.model.Value;
import com.defano.hypertalk.exception.HtException;
import com.defano.hypertalk.exception.HtSemanticException;
import com.defano.wyldcard.WyldCard;
import com.defano.wyldcard.parts.bkgnd.BackgroundModel;
import com.defano.wyldcard.parts.card.CardModel;
import com.defano.wyldcard.parts.finder.LayeredPartFinder;
import com.defano.wyldcard.parts.stack.StackModel;
import com.defano.wyldcard.runtime.context.ExecutionContext;
import com.defano.wyldcard.window.WindowManager;
import com.google.inject.Inject;
import org.antlr.v4.runtime.ParserRuleContext;

import javax.swing.*;

public class NumberFunc extends Expression {

    @Inject
    WindowManager windowManager;

    private final Expression expression;

    public NumberFunc(ParserRuleContext context, Expression expression) {
        super(context);
        this.expression = expression;
    }

    @Override
    protected Value onEvaluate(ExecutionContext context) throws HtException {
        CountableExp countableExp = expression.factor(context, CountableExp.class, new HtSemanticException("Can't count that."));
        Expression containerExp = countableExp.getArgument();

        switch (countableExp.getCountable()) {
            case CARDS:
            case CARDS_OF:
                return getNumberOfCards(context, containerExp);
            case MARKED_CARDS:
            case MARKED_CARDS_OF:
                return getNumberOfMarkedCards(context, containerExp);
            case CHARS_OF:
                return getNumberOfChars(context, containerExp);
            case ITEMS_OF:
                return getNumberOfItems(context, containerExp);
            case WORDS_OF:
                return getNumberOfWords(context, containerExp);
            case LINES_OF:
                return getNumberOfLines(context, containerExp);
            case CARD_BUTTONS:
                return getNumberOfCardButtons(context, containerExp);
            case BACKGROUNDS:
                return getNumberOfBackgrounds(context, containerExp);
            case CARD_FIELDS:
                return getNumberOfCardFields(context, containerExp);
            case CARD_PARTS:
                return getNumberOfCardParts(context, containerExp);
            case BKGND_BUTTONS:
                return getNumberOfBackgroundButtons(context, containerExp);
            case BKGND_FIELDS:
                return getNumberOfBackgroundFields(context, containerExp);
            case BKGND_PARTS:
                return getNumberOfBackgroundParts(context, containerExp);
            case WINDOWS:
                return getNumberOfWindows();
            case MENUS:
                return getNumberOfMenus();
            case MENU_ITEMS:
                return getNumberOfMenuItems(context, containerExp);
        }

        throw new IllegalStateException("Bug! Unimplemented countable: " + countableExp.getCountable());
    }

    private Value getNumberOfCards(ExecutionContext context, Expression containerExpr) throws HtException {
        if (containerExpr == null) {
            return new Value(context.getCurrentStack().getCardCountProvider().blockingFirst());
        } else {
            BackgroundModel bkgnd = containerExpr.partFactor(context, BackgroundModel.class);
            if (bkgnd != null) {
                return new Value(bkgnd.getCardModels(context).size());
            } else {
                return new Value(containerExpr.partFactor(context, StackModel.class, new HtSemanticException("No such stack or background.")).getCardCount());
            }
        }
    }

    private Value getNumberOfMarkedCards(ExecutionContext context, Expression containerExpr) throws HtException {
        if (containerExpr == null) {
            return new Value(context.getCurrentStack().getStackModel().getMarkedCards(context).size());
        } else {
            BackgroundModel bkgnd = containerExpr.partFactor(context, BackgroundModel.class);
            if (bkgnd != null) {
                return new Value(bkgnd.getCardModels(context).stream().filter(p -> p.isMarked(context)).count());
            } else {
                return new Value(containerExpr.partFactor(context, StackModel.class, new HtSemanticException("No such stack or background.")).getMarkedCards(context).size());
            }
        }
    }

    private Value getNumberOfBackgrounds(ExecutionContext context, Expression containerExpr) throws HtException {
        if (containerExpr == null) {
            return new Value(context.getCurrentStack().getStackModel().getBackgroundCount());
        } else {
            BackgroundModel bkgnd = containerExpr.partFactor(context, BackgroundModel.class);
            if (bkgnd != null) {
                return new Value(bkgnd.getCardModels(context).size());
            } else {
                return new Value(containerExpr.partFactor(context, StackModel.class, new HtSemanticException("No such stack or background.")).getBackgroundCount());
            }
        }
    }

    private Value getNumberOfChars(ExecutionContext context, Expression containerExpr) throws HtException {
        return new Value(containerExpr.evaluate(context).charCount(context));
    }

    private Value getNumberOfWords(ExecutionContext context, Expression containerExpr) throws HtException {
        return new Value(containerExpr.evaluate(context).wordCount(context));
    }

    private Value getNumberOfLines(ExecutionContext context, Expression containerExpr) throws HtException {
        return new Value(containerExpr.evaluate(context).lineCount(context));
    }

    private Value getNumberOfItems(ExecutionContext context, Expression containerExpr) throws HtException {
        return new Value(containerExpr.evaluate(context).itemCount(context));
    }

    private Value getNumberOfCardButtons(ExecutionContext context, Expression containerExp) throws HtException {
        return new Value(findOwningLayer(context, containerExp, Countable.CARD_BUTTONS).getPartCount(context, PartType.BUTTON, Owner.CARD));
    }

    private Value getNumberOfCardFields(ExecutionContext context, Expression containerExp) throws HtException {
        return new Value(findOwningLayer(context, containerExp, Countable.CARD_FIELDS).getPartCount(context, PartType.FIELD, Owner.CARD));
    }

    private Value getNumberOfCardParts(ExecutionContext context, Expression containerExp) throws HtException {
        return new Value(findOwningLayer(context, containerExp, Countable.CARD_PARTS).getPartCount(context, null, Owner.CARD));
    }

    private Value getNumberOfBackgroundButtons(ExecutionContext context, Expression containerExp) throws HtException {
        return new Value(findOwningLayer(context, containerExp,Countable.BKGND_BUTTONS).getPartCount(context, PartType.BUTTON, Owner.BACKGROUND));
    }

    private Value getNumberOfBackgroundFields(ExecutionContext context, Expression containerExp) throws HtException {
        return new Value(findOwningLayer(context, containerExp, Countable.BKGND_FIELDS).getPartCount(context, PartType.FIELD, Owner.BACKGROUND));
    }

    private Value getNumberOfBackgroundParts(ExecutionContext context, Expression containerExp) throws HtException {
        return new Value(findOwningLayer(context, containerExp, Countable.BKGND_PARTS).getPartCount(context, null, Owner.BACKGROUND));
    }

    private Value getNumberOfWindows() {
        return new Value(windowManager.getFrames(false).size());
    }

    private Value getNumberOfMenus() {
        return new Value(WyldCard.getInstance().getWyldCardMenuBar().getMenuCount());
    }

    private Value getNumberOfMenuItems(ExecutionContext context, Expression containerExpr) throws HtException {
        JMenu menu = WyldCard.getInstance().getWyldCardMenuBar().findMenuByName(containerExpr.evaluate(context).toString());
        if (menu != null) {
            return new Value(menu.getItemCount());
        }

        throw new HtSemanticException("No such menu.");
    }

    /**
     * Attempts to find the layer (card layer or background) on which the parts being counted are located. For example,
     * when counting 'the number of bg buttons on card 2', this method returns the {@link BackgroundModel} of card 2.
     *
     * @param context The execution context
     * @param containerExp The expression identifying the part container (i.e., 'card 2')
     * @param countable The things being counted
     * @return The LayeredPartFinder (interface) of the found {@link BackgroundModel} or {@link CardModel}.
     * @throws HtException Thrown if the owning layer cannot be found.
     */
    private LayeredPartFinder findOwningLayer(ExecutionContext context, Expression containerExp, Countable countable) throws HtException {

        // Base case: Owning layer isn't specified; implies the current card (`number of card parts`) or its background
        if (containerExp == null) {
            if (countable.isCardPart()) {
                return context.getCurrentCard().getCardModel();
            } else if (countable.isBkgndPart()) {
                return context.getCurrentCard().getCardModel().getBackgroundModel();
            }

            throw new IllegalStateException("Bug! Unsupported syntax.");
        }

        // User specified an owning layer (i.e., 'number of card buttons of <owning layer>')
        else {

            // See if owning layer can be factored to a background
            BackgroundModel bkgnd = containerExp.partFactor(context, BackgroundModel.class);
            if (bkgnd != null) {

                // Can't count card parts on a background (i.e., 'number of card buttons of bg 1' is illegal)
                if (countable.isCardPart()) {
                    throw new HtSemanticException("Can't count card parts on a background.");
                }
                return bkgnd;
            }

            // ... if not, must be a card
            else {
                CardModel card = containerExp.partFactor(context, CardModel.class, new HtSemanticException("No such card or background."));

                // Counting number of background parts of a card (i.e., 'number of bg fields of card 2')
                if (countable.isBkgndPart()) {
                    return card.getBackgroundModel();
                }

                // Counting card parts of a card (i.e., 'number of card fields of card 2')
                else {
                    return card;
                }
            }
        }
    }

}
