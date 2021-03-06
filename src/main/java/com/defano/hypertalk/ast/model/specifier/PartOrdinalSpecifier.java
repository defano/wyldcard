package com.defano.hypertalk.ast.model.specifier;

import com.defano.hypertalk.ast.model.enums.Ordinal;
import com.defano.hypertalk.ast.model.enums.Owner;
import com.defano.hypertalk.ast.model.enums.PartType;
import com.defano.hypertalk.exception.HtNoSuchPartException;
import com.defano.wyldcard.part.card.CardModel;
import com.defano.wyldcard.part.finder.FindInCollectionSpecifier;
import com.defano.wyldcard.part.model.PartModel;
import com.defano.wyldcard.runtime.ExecutionContext;

import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

/**
 * Specifies a button, field, card or background part by ordinal number. For example, 'the second card' or 'the fifth
 * card field'
 */
public class PartOrdinalSpecifier implements FindInCollectionSpecifier {

    private final PartType type;
    private final Owner layer;
    private final Ordinal ordinal;
    private final boolean marked;

    public PartOrdinalSpecifier(Owner layer, PartType type, Ordinal ordinal, boolean marked) {
        this.type = type;
        this.layer = layer;
        this.ordinal = ordinal;
        this.marked = marked;

        if (marked && type != PartType.CARD) {
            throw new IllegalStateException("Bug! Can only find marked cards; other parts disallowed.");
        }
    }

    public PartModel findInCollection(ExecutionContext context, List<PartModel> parts) throws HtNoSuchPartException {
        List<PartModel> foundParts = parts.stream()
                .filter(p -> getType() == null || p.getType() == getType())
                .filter(p -> getOwner() == null || p.getOwner() == getOwner())
                .filter(p -> !marked || ( ((CardModel)p).isMarked(context) || p.equals(context.getCurrentCard().getPartModel())))
                .collect(Collectors.toList());

        int index = ((Ordinal) getValue()).intValue() - 1;

        if (getValue() == Ordinal.LAST) {
            index = foundParts.size() - 1;
        } else if (getValue() == Ordinal.MIDDLE) {
            index = foundParts.size() / 2;
        } else if (getValue() == Ordinal.ANY && foundParts.size() > 0) {
            index = new Random().nextInt(foundParts.size());
        }

        if (index < 0 || index >= foundParts.size()) {
            throw new HtNoSuchPartException("No such " + getHyperTalkIdentifier(context) + ".");
        } else {
            return foundParts.get(index);
        }
    }

    @Override
    public Object getValue() {
        return ordinal;
    }

    @Override
    public Owner getOwner() {
        return layer;
    }

    @Override
    public PartType getType() {
        return type;
    }

    @Override
    public String getHyperTalkIdentifier(ExecutionContext context) {
        if (layer == null) {
            return ordinal.name().toLowerCase() + " " + type.toString().toLowerCase();
        } else if (type == null) {
            return ordinal.name().toLowerCase() + " " + getOwner().name().toLowerCase() + " part";
        } else {
            return ordinal.name().toLowerCase() + " " + getOwner().name().toLowerCase() + " " + type.toString().toLowerCase();
        }
    }

    @Override
    public String toString() {
        return "PartOrdinalSpecifier{" +
                "type=" + type +
                ", layer=" + layer +
                ", ordinal=" + ordinal +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        PartOrdinalSpecifier that = (PartOrdinalSpecifier) o;

        if (type != that.type) return false;
        if (layer != that.layer) return false;
        return ordinal == that.ordinal;
    }

    @Override
    public int hashCode() {
        int result = type != null ? type.hashCode() : 0;
        result = 31 * result + (layer != null ? layer.hashCode() : 0);
        result = 31 * result + (ordinal != null ? ordinal.hashCode() : 0);
        return result;
    }
}
