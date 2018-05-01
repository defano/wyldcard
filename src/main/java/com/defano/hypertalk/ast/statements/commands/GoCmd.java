package com.defano.hypertalk.ast.statements.commands;

import com.defano.hypertalk.ast.expressions.Expression;
import com.defano.hypertalk.ast.expressions.VisualEffectExp;
import com.defano.hypertalk.ast.expressions.parts.CompositePartExp;
import com.defano.hypertalk.ast.expressions.parts.StackPartExp;
import com.defano.hypertalk.ast.model.RemoteNavigationOptions;
import com.defano.hypertalk.ast.model.Value;
import com.defano.hypertalk.ast.model.specifiers.CompositePartSpecifier;
import com.defano.hypertalk.ast.model.specifiers.PartSpecifier;
import com.defano.hypertalk.ast.model.specifiers.StackPartSpecifier;
import com.defano.hypertalk.ast.model.specifiers.VisualEffectSpecifier;
import com.defano.hypertalk.ast.statements.Command;
import com.defano.hypertalk.exception.HtException;
import com.defano.hypertalk.exception.HtSemanticException;
import com.defano.wyldcard.WyldCard;
import com.defano.wyldcard.parts.bkgnd.BackgroundModel;
import com.defano.wyldcard.parts.card.CardModel;
import com.defano.wyldcard.parts.model.PartModel;
import com.defano.wyldcard.parts.stack.StackModel;
import com.defano.wyldcard.runtime.context.ExecutionContext;
import com.defano.wyldcard.window.WindowManager;
import com.defano.wyldcard.window.layouts.StackWindow;
import org.antlr.v4.runtime.ParserRuleContext;

import javax.swing.*;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicReference;

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

        // Special case: No destination means 'go back'
        if (destinationExp == null) {
            context.getCurrentStack().popCard(context, getVisualEffect(context));
            return;
        }

        // Case 1: Navigate to a stack ('go to stack "My Stack"')
        StackPartExp stackPartExp = destinationExp.factor(context, StackPartExp.class);
        if (stackPartExp != null) {
            StackModel model = WyldCard.getInstance().locateStack(context, (StackPartSpecifier) stackPartExp.evaluateAsSpecifier(context), navigationOptions);
            if (model != null) {
                goToDestination(context, getDestination(context, model), getVisualEffect(context));
                return;
            }

            context.setResult(new Value("No such stack."));
        }

        // Case 2: Navigate to a card in this stack ('go to card 3', 'go to card 3 of next bg')
        Destination destination = getDestination(context, destinationExp.partFactor(context, CardModel.class));
        if (destination != null) {
            goToDestination(context, destination, getVisualEffect(context));
            return;
        }

        // Case 3: Navigate to a background in this stack ('go to next background')
        destination = getDestination(context, destinationExp.partFactor(context, BackgroundModel.class));
        if (destination != null) {
            goToDestination(context, destination, getVisualEffect(context));
            return;
        }

        // Case 4: Navigate to a card or background in another stack ('go to cd 3 of bg 2 of stack "Another"')
        CompositePartExp cpe = destinationExp.factor(context, CompositePartExp.class);
        if (cpe != null) {
            CompositePartSpecifier cps = (CompositePartSpecifier) cpe.evaluateAsSpecifier(context);
            PartSpecifier rps = cps.getRootOwningPartSpecifier(context);

            // Is root part a stack? If not, we're toast
            if (rps instanceof StackPartSpecifier) {

                // Try to locate (or prompt to locate) requested stack
                StackModel model = WyldCard.getInstance().locateStack(context, (StackPartSpecifier) rps, navigationOptions);
                if (model == null) {
                    context.setResult(new Value("No such stack."));
                    return;
                }

                // We found the remote stack, now try to find the card
                destination = getDestination(context, model.findPart(context, cps));
                if (destination != null) {
                    goToDestination(context, destination, getVisualEffect(context));
                    return;
                } else {
                    context.setResult(new Value("No such card."));
                    return;
                }
            }
        }

        context.setResult(new Value("No such card."));
    }

    /**
     * Attempts to navigate to the given destination, applying a visual effect as requested and blocking the thread
     * until the navigation and animation has completed.
     *
     * @param context The execution context
     * @param destination The destination to navigate to
     * @param visualEffect The optional visual effect (null for no effect)
     */
    private void goToDestination(ExecutionContext context, Destination destination, VisualEffectSpecifier visualEffect) {
        CountDownLatch latch = new CountDownLatch(1);
        AtomicReference<StackWindow> stackWindow = new AtomicReference<>();

        // Start the navigation on the UI thread
        SwingUtilities.invokeLater(() -> {
            stackWindow.set(WindowManager.getInstance().findWindowForStack(destination.stack));
            context.setCurrentStack(stackWindow.get().getStack());
            stackWindow.get().setVisible(true);
            stackWindow.get().requestFocus();
            stackWindow.get().getStack().goCard(context, destination.cardIndex, visualEffect, true);
            latch.countDown();
        });

        try {
            // Wait for the navigation to start...
            latch.await();

            // ... then wait for the visual effect to complete
            stackWindow.get().getStack().getCurtainManager().waitForEffectToFinish();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

    }

    private Destination getDestination(ExecutionContext context, PartModel model) {
        Integer destinationIndex;
        StackModel destinationStack;

        // Part is a card in a stack
        if (model instanceof CardModel) {
            destinationStack = ((CardModel) model).getStackModel();
            destinationIndex = destinationStack.getIndexOfCard((CardModel) model);
            return new Destination(destinationStack, destinationIndex);
        }

        // Part is a background in a stack
        else if (model instanceof BackgroundModel) {
            destinationStack = ((BackgroundModel) model).getStackModel();
            destinationIndex = destinationStack.getIndexOfBackground(model.getId(context));
            return new Destination(destinationStack, destinationIndex);
        }

        // Part is the stack itself
        else if (model instanceof StackModel) {
            return new Destination((StackModel) model, ((StackModel) model).getCurrentCardIndex());
        }

        // Part was null or something bogus
        return null;
    }

    private VisualEffectSpecifier getVisualEffect(ExecutionContext context) throws HtException {
        if (visualEffectExp == null) {
            return context.getStackFrame().getVisualEffect();
        } else {
            return visualEffectExp.factor(context, VisualEffectExp.class, new HtSemanticException("Not a visual effect.")).effectSpecifier;
        }
    }

    private class Destination {
        final StackModel stack;
        final int cardIndex;

        Destination(StackModel stackModel, int cardIndex) {
            this.stack = stackModel;
            this.cardIndex = cardIndex;
        }
    }

}
