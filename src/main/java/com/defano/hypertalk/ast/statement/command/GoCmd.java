package com.defano.hypertalk.ast.statement.command;

import com.defano.hypertalk.ast.expression.Expression;
import com.defano.hypertalk.ast.expression.part.CompositePartExp;
import com.defano.hypertalk.ast.expression.part.PartDirectionExp;
import com.defano.hypertalk.ast.expression.part.StackPartExp;
import com.defano.hypertalk.ast.model.Destination;
import com.defano.hypertalk.ast.model.RemoteNavigationOptions;
import com.defano.hypertalk.ast.model.Value;
import com.defano.hypertalk.ast.model.specifier.CompositePartSpecifier;
import com.defano.hypertalk.ast.model.specifier.PartDirectionSpecifier;
import com.defano.hypertalk.ast.model.specifier.PartSpecifier;
import com.defano.hypertalk.ast.model.specifier.StackPartSpecifier;
import com.defano.hypertalk.ast.statement.Command;
import com.defano.hypertalk.exception.HtException;
import com.defano.hypertalk.exception.HtSemanticException;
import com.defano.wyldcard.NavigationManager;
import com.defano.wyldcard.WyldCard;
import com.defano.wyldcard.part.bkgnd.BackgroundModel;
import com.defano.wyldcard.part.card.CardModel;
import com.defano.wyldcard.part.stack.StackModel;
import com.defano.wyldcard.runtime.ExecutionContext;
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

        boolean success =
                attemptToGoDirection(context) ||
                attemptToGoToCardInThisStack(context) ||
                attemptToGoToBkgndInThisStack(context) ||
                attemptToGoToDestinationInRemoteStack(context) ||
                attemptToGoToStack(context);

        if (!success) {
            context.setResult(new Value("No such card."));
        }
    }

    private boolean attemptToGoToDestinationInRemoteStack(ExecutionContext context) throws HtException {
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
                    return true;
                }

                // We found the remote stack, now try to find the card
                Destination destination = Destination.ofPart(context, model.findPart(context, cps));
                if (destination != null) {
                    navigationManager.goDestination(context, destination);
                    return true;
                } else {
                    context.setResult(new Value("No such card."));
                    return true;
                }
            }
        }

        return false;
    }

    private boolean attemptToGoToBkgndInThisStack(ExecutionContext context) throws HtSemanticException {
        Destination destination = Destination.ofPart(context, destinationExp.partFactor(context, BackgroundModel.class));
        if (destination != null) {
            navigationManager.goDestination(context, destination);
            return true;
        } else {
            return false;
        }
    }

    private boolean attemptToGoToCardInThisStack(ExecutionContext context) throws HtSemanticException {
        Destination destination = Destination.ofPart(context, destinationExp.partFactor(context, CardModel.class));
        if (destination != null) {
            navigationManager.goDestination(context, destination);
            return true;
        } else {
            return false;
        }
    }

    private boolean attemptToGoToStack(ExecutionContext context) throws HtException {

        StackPartSpecifier stackPartSpecifier;
        StackPartExp stackPartExp = destinationExp.factor(context, StackPartExp.class);
        if (stackPartExp != null) {
            stackPartSpecifier = (StackPartSpecifier) stackPartExp.evaluateAsSpecifier(context);
        } else  {
            stackPartSpecifier = new StackPartSpecifier(destinationExp.evaluate(context).toString());
        }

        StackModel model = WyldCard.getInstance().getStackManager().findStack(context, stackPartSpecifier, navigationOptions);
        Destination destination = Destination.ofPart(context, model);

        if (destination != null) {
            navigationManager.goDestination(context, destination);
            return true;
        }

        if (navigationOptions.withoutDialog) {
            context.setResult(new Value("No such stack."));
        } else {
            context.setResult(new Value());
        }

        return true;
    }

    private boolean attemptToGoDirection(ExecutionContext context) {
        PartDirectionExp directionExp = destinationExp.factor(context, PartDirectionExp.class);

        if (directionExp != null) {
            PartDirectionSpecifier ps = directionExp.evaluateAsSpecifier(context);

            switch (ps.getValue()) {
                case BACK:
                    navigationManager.goBack(context);
                    break;
                case FORTH:
                    navigationManager.goForth(context);
                    break;
            }

            return true;
        } else {
            return false;
        }
    }

}
