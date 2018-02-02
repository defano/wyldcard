package com.defano.hypertalk.ast.model.specifiers;

import com.defano.hypertalk.ast.model.Owner;
import com.defano.hypertalk.ast.model.PartType;
import com.defano.hypertalk.ast.expressions.containers.PartExp;
import com.defano.hypertalk.exception.HtException;

/**
 * Specifies a button or field part that is not on the current card. For example, 'button 3 of card 19'
 */
public class CompositePartSpecifier implements PartSpecifier {

    private final PartSpecifier part;      // The button or field
    private final PartExp owningPart;      // The card the button or field can be found on

    public CompositePartSpecifier(PartSpecifier part, PartExp owningPart) {
        this.part = part;
        this.owningPart = owningPart;
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
    public String getHyperTalkIdentifier() {
        try {
            return part.getHyperTalkIdentifier() + " of " + owningPart.evaluateAsSpecifier().getHyperTalkIdentifier();
        } catch (Exception e) {
            return part.getHyperTalkIdentifier();
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        CompositePartSpecifier that = (CompositePartSpecifier) o;

        try {
            return part.equals(that.part) && owningPart.evaluateAsSpecifier().equals(that.owningPart.evaluateAsSpecifier());
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
