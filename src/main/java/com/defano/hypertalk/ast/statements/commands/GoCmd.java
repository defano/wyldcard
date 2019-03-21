package com.defano.hypertalk.ast.statements.commands;

import com.defano.hypertalk.ast.expressions.Expression;
import com.defano.hypertalk.ast.expressions.VisualEffectExp;
import com.defano.hypertalk.ast.expressions.parts.CompositePartExp;
import com.defano.hypertalk.ast.expressions.parts.StackPartExp;
import com.defano.hypertalk.ast.model.Destination;
import com.defano.hypertalk.ast.model.RemoteNavigationOptions;
import com.defano.hypertalk.ast.model.Value;
import com.defano.hypertalk.ast.model.specifiers.CompositePartSpecifier;
import com.defano.hypertalk.ast.model.specifiers.PartSpecifier;
import com.defano.hypertalk.ast.model.specifiers.StackPartSpecifier;
import com.defano.hypertalk.ast.model.specifiers.VisualEffectSpecifier;
import com.defano.hypertalk.ast.statements.Command;
import com.defano.hypertalk.exception.HtException;
import com.defano.hypertalk.exception.HtSemanticException;
import com.defano.wyldcard.NavigationManager;
import com.defano.wyldcard.WyldCard;
import com.defano.wyldcard.parts.bkgnd.BackgroundModel;
import com.defano.wyldcard.parts.card.CardModel;
import com.defano.wyldcard.parts.card.CardPart;
import com.defano.wyldcard.parts.stack.StackModel;
import com.defano.wyldcard.runtime.context.ExecutionContext;
import com.google.inject.Inject;
import org.antlr.v4.runtime.ParserRuleContext;

public class GoCmd extends Command {

    private final Expression destinationExp;
    private final RemoteNavigationOptions navigationOptions;

    @Inject
    private NavigationManager navigationManager;

    public GoCmd(ParserRuleContext context, Expression destinationExp, RemoteNavigationOptions navigationOptions) {
        super(context, "go");

        this.destinationExp = destinationExp;
        this.navigationOptions = navigationOptions;
    }

    public void onExecute(ExecutionContext context) throws HtException {

        // Special case: No destination means 'go back'
        if (destinationExp == null) {
            navigationManager.goPopCard(context, context.getCurrentStack());
            return;
        }

        // Case 1: Navigate to a stack ('go to stack "My Stack"')
        StackPartExp stackPartExp = destinationExp.factor(context, StackPartExp.class);
        if (stackPartExp != null) {
            StackModel model = WyldCard.getInstance().getStackManager().findStack(context, (StackPartSpecifier) stackPartExp.evaluateAsSpecifier(context), navigationOptions);
            Destination destination = Destination.ofPart(context, model);

            if (destination != null) {
                navigationManager.goDestination(context, destination);
                return;
            }

            context.setResult(new Value("No such stack."));
        }

        // Case 2: Navigate to a card in this stack ('go to card 3', 'go to card 3 of next bg')
        Destination destination = Destination.ofPart(context, destinationExp.partFactor(context, CardModel.class));
        if (destination != null) {
            navigationManager.goDestination(context, destination);
            return;
        }

        // Case 3: Navigate to a background in this stack ('go to next background')
        destination = Destination.ofPart(context, destinationExp.partFactor(context, BackgroundModel.class));
        if (destination != null) {
            navigationManager.goDestination(context, destination);
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
                StackModel model = WyldCard.getInstance().getStackManager().findStack(context, (StackPartSpecifier) rps, navigationOptions);
                if (model == null) {
                    context.setResult(new Value("No such stack."));
                    return;
                }

                // We found the remote stack, now try to find the card
                destination = Destination.ofPart(context, model.findPart(context, cps));
                if (destination != null) {
                    navigationManager.goDestination(context, destination);
                    return;
                } else {
                    context.setResult(new Value("No such card."));
                    return;
                }
            }
        }

        context.setResult(new Value("No such card."));
    }

}
