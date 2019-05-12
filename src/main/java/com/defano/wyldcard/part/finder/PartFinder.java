package com.defano.wyldcard.part.finder;

import com.defano.hypertalk.ast.model.specifier.CompositePartSpecifier;
import com.defano.hypertalk.ast.model.specifier.PartSpecifier;
import com.defano.hypertalk.ast.model.specifier.StackPartSpecifier;
import com.defano.hypertalk.ast.model.specifier.WindowSpecifier;
import com.defano.hypertalk.exception.HtException;
import com.defano.wyldcard.WyldCard;
import com.defano.hypertalk.exception.HtNoSuchPartException;
import com.defano.wyldcard.part.model.PartModel;
import com.defano.wyldcard.part.model.WindowProxyPartModel;
import com.defano.wyldcard.part.stack.StackModel;
import com.defano.wyldcard.part.stack.StackPart;
import com.defano.wyldcard.runtime.ExecutionContext;

/**
 * Provides routines for finding any kind of WyldCard part or object, like windows, cards, backgrounds, stacks, buttons
 * and fields.
 */
public interface PartFinder {

    /**
     * Finds and returns the model associated with any in-scope part (windows, the message, stacks, parts of stacks,
     * etc.).
     *
     * @param context The execution context in which to find the part (identifies the current stack)
     * @param ps      The PartSpecifier identifying the part to find.
     * @return The model associated with the specified part.
     * @throws HtNoSuchPartException Thrown if the requested part does not exist or if the specification is otherwise invalid.
     */
    default PartModel findPart(ExecutionContext context, PartSpecifier ps) throws HtNoSuchPartException {

        // Looking for HyperCard itself (i.e., 'send "greeting" to hypercard')
        if (ps.isSpecifyingHyperCard()) {
            return WyldCard.getInstance().getWyldCardPart();
        }

        // Looking for a window
        if (ps.isSpecifyingWindow()) {
            return new WindowProxyPartModel(WyldCard.getInstance().getWindowManager().findWindow(context, (WindowSpecifier) ps));
        }

        // Looking for a stack
        else if (ps.isSpecifyingStack()) {
            return ((StackPartSpecifier) ps).find(context, WyldCard.getInstance().getStackManager().getOpenStacks());
        }

        // Looking for a part in a stack
        else {
            StackModel stackOwningPart = findReferredStack(context, ps);
            return stackOwningPart.findPart(context, ps);
        }
    }

    /**
     * Returns the model associated with the stack referred to (explicitly or implicitly) by the given part specifier.
     * <p>
     * For example, the stack associated with 'btn 1 of card 1' would return the current stack identified by the
     * execution context; however, 'btn 1 of card 1 of stack "Yo Mama"' would return the StackModel associated with the
     * "Yo Mama" stack.
     *
     * @param context The execution context in which the specifier is being evaluated
     * @param ps      The part specifier
     * @return The referred stack model
     * @throws HtNoSuchPartException Thrown if the stack does not exist or if the specification is otherwise invalid.
     */
    default StackModel findReferredStack(ExecutionContext context, PartSpecifier ps) throws HtNoSuchPartException {
        if (ps instanceof CompositePartSpecifier) {
            try {
                PartSpecifier root = ps.getRootOwningPartSpecifier(context);
                if (root instanceof StackPartSpecifier) {
                    return findStack(context, (StackPartSpecifier) ps.getRootOwningPartSpecifier(context));
                }
            } catch (HtException e) {
                throw new HtNoSuchPartException(e);
            }
        }

        return context.getCurrentStack().getStackModel();
    }

    /**
     * Finds the model associated with the identified stack, looking only within the set of open stacks (this method
     * will never prompt to find a stack).
     *
     * @param context The execution context
     * @param ps      A stack specifier
     * @return The found stack model
     * @throws HtNoSuchPartException Thrown if the requested part does not exist or if the specification is otherwise invalid.
     */
    default StackModel findStack(ExecutionContext context, StackPartSpecifier ps) throws HtNoSuchPartException {
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

        throw new HtNoSuchPartException("No such stack.");
    }

}
