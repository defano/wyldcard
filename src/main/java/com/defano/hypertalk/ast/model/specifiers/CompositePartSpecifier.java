package com.defano.hypertalk.ast.model.specifiers;

import com.defano.hypertalk.ast.expressions.containers.PartExp;
import com.defano.hypertalk.ast.model.Owner;
import com.defano.hypertalk.ast.model.PartType;
import com.defano.hypertalk.exception.HtException;
import com.defano.wyldcard.parts.PartException;
import com.defano.wyldcard.parts.bkgnd.BackgroundModel;
import com.defano.wyldcard.parts.card.CardLayerPartModel;
import com.defano.wyldcard.parts.card.CardModel;
import com.defano.wyldcard.parts.finder.LayeredPartFinder;
import com.defano.wyldcard.parts.finder.StackPartFinder;
import com.defano.wyldcard.parts.finder.StackPartFindingSpecifier;
import com.defano.wyldcard.parts.model.PartModel;
import com.defano.wyldcard.runtime.context.ExecutionContext;

/**
 * Specifies a button or field part that is not on the current card. For example, 'button 3 of card 19'
 */
public class CompositePartSpecifier implements PartSpecifier, StackPartFindingSpecifier {

    private final PartSpecifier part;      // The button or field
    private final PartExp owningPart;      // The card the button or field can be found on
    private final ExecutionContext context;

    public CompositePartSpecifier(ExecutionContext context, PartSpecifier part, PartExp owningPart) {
        this.part = part;
        this.owningPart = owningPart;
        this.context = context;
    }

    @Override
    public PartModel find(ExecutionContext context, StackPartFinder finder) throws PartException {
        try {
            PartModel foundPart;
            PartSpecifier owningPartSpecifier = getOwningPartExp().evaluateAsSpecifier(context);

            // Special case: This finder assumes the stack portion of the specifier expression refers to the current
            // stack, but does not verify that's true; it simply ignores the "owning stack" and finds the part itself
            if (owningPartSpecifier.getType() == PartType.STACK) {
                return finder.findPart(context, getPart());
            }

            // Recursively find the card or background containing the requested part
            PartModel owningPart = finder.findPart(context, owningPartSpecifier);

            // Looking for a background button or field on a remote card or background
            if (isBackgroundPartSpecifier()) {
                if (owningPart instanceof CardModel) {
                    foundPart = finder.findPart(context, getPart(), ((CardModel) owningPart).getBackgroundModel().getPartsInDisplayOrder(context, getOwner()));
                } else {
                    foundPart = finder.findPart(context, getPart(), ((BackgroundModel) owningPart).getPartsInDisplayOrder(context, getOwner()));
                }
            }

            // Looking for button or field on a remote card
            else if (isCardPartSpecifier()) {
                foundPart = finder.findPart(context, getPart(), ((CardModel) owningPart).getPartsInDisplayOrder(context));
            }

            // Looking for a card in a remote background
            else {
                foundPart = finder.findPart(context, getPart(), ((LayeredPartFinder) owningPart).getPartsInDisplayOrder(context));
            }

            // Special case: Field needs to be evaluated in the context of the requested card
            if (foundPart instanceof CardLayerPartModel) {
                ((CardLayerPartModel) foundPart).setCurrentCardId(owningPart.getId(context));
            }

            return foundPart;

        } catch (HtException e) {
            throw new PartException(e);
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
