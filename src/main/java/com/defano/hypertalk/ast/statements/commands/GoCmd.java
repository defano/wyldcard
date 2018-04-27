package com.defano.hypertalk.ast.statements.commands;

import com.defano.hypertalk.ast.model.RemoteNavigationOptions;
import com.defano.wyldcard.WyldCard;
import com.defano.wyldcard.parts.bkgnd.BackgroundModel;
import com.defano.wyldcard.parts.card.CardModel;
import com.defano.wyldcard.parts.model.PartModel;
import com.defano.wyldcard.parts.stack.StackModel;
import com.defano.wyldcard.runtime.context.ExecutionContext;
import com.defano.wyldcard.util.ThreadUtils;
import com.defano.hypertalk.ast.expressions.Expression;
import com.defano.hypertalk.ast.expressions.VisualEffectExp;
import com.defano.hypertalk.ast.model.specifiers.VisualEffectSpecifier;
import com.defano.hypertalk.ast.statements.Command;
import com.defano.hypertalk.exception.HtException;
import com.defano.hypertalk.exception.HtSemanticException;
import com.defano.wyldcard.window.StackWindow;
import com.defano.wyldcard.window.WindowManager;
import org.antlr.v4.runtime.ParserRuleContext;

public class GoCmd extends Command {

    private final Expression destinationExp;
    private final RemoteNavigationOptions navigationOptions;
    private Expression visualEffectExp;

    public GoCmd(ParserRuleContext context, Expression destinationExp, RemoteNavigationOptions navigationOptions) {
        this(context, destinationExp, null, navigationOptions);
    }

    public GoCmd(ParserRuleContext context, Expression destinationExp, Expression visualEffectExp, RemoteNavigationOptions navigationOptions) {
        super(context, "go");

        this.destinationExp = destinationExp;
        this.visualEffectExp = visualEffectExp;
        this.navigationOptions = navigationOptions;
    }

    public void onExecute(ExecutionContext context) throws HtException {

        VisualEffectSpecifier visualEffect;

        if (visualEffectExp == null) {
            visualEffect = context.getStackFrame().getVisualEffect();
        } else {
            visualEffect = visualEffectExp.factor(context, VisualEffectExp.class, new HtSemanticException("Not a visual effect.")).effectSpecifier;
        }

        // Special case: No destination means 'Go back'
        if (destinationExp == null) {
            context.getCurrentStack().popCard(context, visualEffect);
        }

        else {
            Destination destination = getDestination(context, destinationExp.partFactor(context, CardModel.class));
            if (destination == null) {
                destination = getDestination(context, destinationExp.partFactor(context, BackgroundModel.class));
            }

            if (destination == null) {
                throw new HtSemanticException("No such card.");
            } else {
                goToDestination(context, destination, visualEffect);
            }
        }
    }

    private void goToDestination(ExecutionContext context, Destination destination, VisualEffectSpecifier visualEffect) {
        ThreadUtils.invokeAndWaitAsNeeded(() -> {
            StackWindow stackWindow = WindowManager.getInstance().findWindowForStack(destination.stack);
            stackWindow.requestFocus();
            stackWindow.getStack().goCard(context, destination.cardIndex, visualEffect, true);
        });
    }

    private Destination getDestination(ExecutionContext context, PartModel model) {
        Integer destinationIndex;
        StackModel destinationStack;

        if (model == null) {
            return null;
        } else if (model instanceof CardModel) {
            destinationStack = ((CardModel) model).getStackModel();
            destinationIndex = destinationStack.getIndexOfCard((CardModel) model);
            return new Destination(destinationStack, destinationIndex);
        } else if (model instanceof BackgroundModel) {
            destinationStack = ((BackgroundModel) model).getStackModel();
            destinationIndex = destinationStack.getIndexOfBackground(model.getId(context));
            return new Destination(destinationStack, destinationIndex);
        } else {
            return null;
        }

    }

    private class Destination {
        public final StackModel stack;
        public final int cardIndex;

        public Destination(StackModel stackModel, int cardIndex) {
            this.stack = stackModel;
            this.cardIndex = cardIndex;
        }
    }

}
