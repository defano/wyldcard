package com.defano.wyldcard.parts.finder;

import com.defano.hypertalk.ast.model.Destination;
import com.defano.hypertalk.ast.model.specifiers.*;
import com.defano.hypertalk.exception.HtException;
import com.defano.wyldcard.WyldCard;
import com.defano.wyldcard.parts.PartException;
import com.defano.wyldcard.parts.model.PartModel;
import com.defano.wyldcard.parts.model.WindowProxyPartModel;
import com.defano.wyldcard.parts.stack.StackModel;
import com.defano.wyldcard.parts.stack.StackPart;
import com.defano.wyldcard.runtime.context.ExecutionContext;

public interface PartFinder {

    /**
     * Finds and returns the model associated with any in-scope part (windows, the message, stacks, parts of stacks,
     * etc.).
     *
     * @param context The execution context in which to find the part (identifies the current stack)
     * @param ps The PartSpecifier identifying the part to find.
     * @return The model associated with the specified part.
     * @throws PartException Thrown if the requested part does not exist or if the specification is otherwise invalid.
     */
    default PartModel findPart(ExecutionContext context, PartSpecifier ps) throws PartException {
        if (ps instanceof WindowSpecifier) {
            return new WindowProxyPartModel(WyldCard.getInstance().getWindowManager().findWindow(context, (WindowSpecifier) ps));
        } else if (ps instanceof PartDirectionSpecifier) {
            return findPartByDirection((PartDirectionSpecifier) ps);
        } else if (ps instanceof PartMessageSpecifier) {
            return WyldCard.getInstance().getWindowManager().getMessageWindow().getPartModel();
        } else if (ps instanceof StackPartSpecifier) {
            return findStackPart(context, (StackPartSpecifier) ps);
        } else {
            return getStackInScope(context, ps).findPart(context, ps);
        }
    }

    /**
     * Returns the model associated with the stack referred to (explicitly or implicitly) by the given part specifier.
     *
     * For example, the stack associated with 'btn 1 of card 1' would return the current stack identified by the
     * execution context; however, 'btn 1 of card 1 of stack "Yo Mama"' would return the StackModel associated with the
     * "Yo Mama" stack.
     *
     * @param context The execution context in which the specifier is being evaluated
     * @param ps The part specifier
     * @return The referred stack model
     * @throws PartException Thrown if the stack does not exist or if the specification is otherwise invalid.
     */
    default StackModel getStackInScope(ExecutionContext context, PartSpecifier ps) throws PartException {
        if (ps instanceof CompositePartSpecifier) {
            try {
                PartSpecifier root = ps.getRootOwningPartSpecifier(context);
                if (root instanceof StackPartSpecifier) {
                    return findStackPart(context, (StackPartSpecifier) ps.getRootOwningPartSpecifier(context));
                }
            } catch (HtException e) {
                throw new PartException(e);
            }
        }

        return context.getCurrentStack().getStackModel();
    }

    /**
     * Finds a card based on navigation direction (i.e., back, forth or recent).
     *
     * @param ps The direction specifier
     * @return The card in the specified direction
     */
    default PartModel findPartByDirection(PartDirectionSpecifier ps) {
        Destination destination;

        switch (ps.getValue()) {
            case BACK:
                destination = WyldCard.getInstance().getStackManager().getBackstack().peekBack();
                break;
            case FORTH:
                destination = WyldCard.getInstance().getStackManager().getBackstack().peekForward();
                break;
            default:
                throw new IllegalStateException("Bug! Unimplemented direction: " + ps.getValue());
        }

        return destination.getStack().getCardModel(destination.getCardId());
    }

    /**
     * Finds the model associated with the identified stack.
     *
     * @param context The execution context
     * @param ps A stack specifier
     * @return The found stack model
     * @throws PartException Thrown if the requested part does not exist or if the specification is otherwise invalid.
     */
    default StackModel findStackPart(ExecutionContext context, StackPartSpecifier ps) throws PartException {
        if (ps.isThisStack()) {
            return context.getCurrentStack().getStackModel();
        } else {
            String stackName = String.valueOf(ps.getValue());

            for (StackPart thisOpenStack : WyldCard.getInstance().getStackManager().getOpenStacks()) {
                String shortName = thisOpenStack.getStackModel().getShortName(context);
                String abbrevName = thisOpenStack.getStackModel().getAbbreviatedName(context);
                String longName = thisOpenStack.getStackModel().getLongName(context);

                if (stackName.equalsIgnoreCase(shortName) || stackName.equalsIgnoreCase(longName) || stackName.equalsIgnoreCase(abbrevName)) {
                    return thisOpenStack.getStackModel();
                }
            }
        }

        throw new PartException("No such stack.");
    }

}
