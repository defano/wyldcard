package com.defano.hypertalk.ast.model.specifier;

import com.defano.hypertalk.ast.expression.container.PartExp;
import com.defano.hypertalk.ast.model.enums.Owner;
import com.defano.hypertalk.ast.model.enums.PartType;
import com.defano.hypertalk.exception.HtException;
import com.defano.hypertalk.exception.HtNoSuchPartException;
import com.defano.wyldcard.part.bkgnd.BackgroundModel;
import com.defano.wyldcard.part.card.CardLayerPartModel;
import com.defano.wyldcard.part.card.CardModel;
import com.defano.wyldcard.part.finder.LayeredPartFinder;
import com.defano.wyldcard.part.model.PartModel;
import com.defano.wyldcard.part.stack.StackModel;
import com.defano.wyldcard.runtime.ExecutionContext;

/**
 * Specifies a part in the context of an owning part. For example, 'cd btn 1 of the next card' or 'the third card of
 * the second bkgnd of stack "My Other Stack"'
 */
public class CompositePartSpecifier implements PartSpecifier {

    private final PartSpecifier part;           // The part we're looking for
    private final PartExp owningPart;           // Where we're looking for it
    private final ExecutionContext context;

    public CompositePartSpecifier(ExecutionContext context, PartSpecifier part, PartExp owningPart) {
        this.part = part;
        this.owningPart = owningPart;
        this.context = context;
    }

    @Override
    public PartModel findInStack(ExecutionContext context, StackModel stack) throws HtNoSuchPartException {
        try {
            PartModel foundPart;
            PartSpecifier owningPartSpecifier = getOwningPartExp().evaluateAsSpecifier(context);

            // Special case: This finder assumes the stack portion of the specifier expression refers to the current
            // stack, but does not verify that's true; it simply ignores the "owning stack" and finds the part itself
            if (owningPartSpecifier.getType() == PartType.STACK) {
                return stack.findPart(context, getPart());
            }

            // Recursively find the card or background containing the requested part
            PartModel owningPartModel = stack.findPart(context, owningPartSpecifier);

            // Looking for a background button or field on a remote card or background
            if (isSpecifyingBackgroundPart()) {
                if (owningPartModel instanceof CardModel) {
                    foundPart = stack.findPart(context, getPart(), ((CardModel) owningPartModel).getBackgroundModel().getPartsInDisplayOrder(context, getOwner()));
                } else {
                    foundPart = stack.findPart(context, getPart(), ((BackgroundModel) owningPartModel).getPartsInDisplayOrder(context, getOwner()));
                }
            }

            // Looking for button or field on a remote card
            else if (isSpecifyingCardPart()) {
                foundPart = stack.findPart(context, getPart(), ((CardModel) owningPartModel).getPartsInDisplayOrder(context));
            }

            // Looking for a card in a remote background
            else {
                foundPart = stack.findPart(context, getPart(), ((LayeredPartFinder) owningPartModel).getPartsInDisplayOrder(context));
            }

            // Special case: Field needs to be evaluated in the context of the requested card
            if (foundPart instanceof CardLayerPartModel) {
                ((CardLayerPartModel) foundPart).setCurrentCardId(owningPartModel.getId());
            }

            return foundPart;

        } catch (HtException e) {
            throw new HtNoSuchPartException(e);
        }
    }

    public PartExp getOwningPartExp() {
        return owningPart;
    }

    public PartSpecifier getPart() {
        return part;
    }

    @Override
    public Object getValue() {
        return part.getValue();
    }

    @Override
    public Owner getOwner() {
        return part.getOwner();
    }

    @Override
    public PartType getType() {
        return part.getType();
    }

    @Override
    public String getHyperTalkIdentifier(ExecutionContext context) {
        try {
            if (part instanceof PartIdSpecifier || part instanceof PartMessageSpecifier) {
                return part.getHyperTalkIdentifier(context);
            } else {
                return part.getHyperTalkIdentifier(context) + " of " + owningPart.evaluateAsSpecifier(context).getHyperTalkIdentifier(context);
            }
        } catch (Exception e) {
            return part.getHyperTalkIdentifier(context);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        CompositePartSpecifier that = (CompositePartSpecifier) o;

        try {
            return part.equals(that.part) && owningPart.evaluateAsSpecifier(context).equals(that.owningPart.evaluateAsSpecifier(context));
        } catch (HtException e) {
            return false;
        }
    }

    @Override
    public String toString() {
        return "CompositePartSpecifier{" +
                "part=" + part +
                ", owningPart=" + owningPart +
                '}';
    }
}
