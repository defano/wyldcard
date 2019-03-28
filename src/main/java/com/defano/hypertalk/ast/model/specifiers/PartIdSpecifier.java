package com.defano.hypertalk.ast.model.specifiers;

import com.defano.hypertalk.ast.model.Owner;
import com.defano.hypertalk.ast.model.PartType;
import com.defano.wyldcard.parts.PartException;
import com.defano.wyldcard.parts.finder.FindInCollectionSpecifier;
import com.defano.wyldcard.parts.model.PartModel;
import com.defano.wyldcard.runtime.context.ExecutionContext;

import java.util.List;
import java.util.Optional;

/**
 * Specifies a button, field, card or background by its ID. For example, 'card id 13' or 'bg field id 11'
 */
public class PartIdSpecifier implements FindInCollectionSpecifier {

    private final Owner layer;
    private final PartType type;
    private final int id;

    public PartIdSpecifier(Owner layer, PartType type, int id) {
        this.layer = layer;
        this.type = type;
        this.id = id;
    }

    public PartModel findInCollection(ExecutionContext context, List<PartModel> parts) throws PartException {
        Optional<PartModel> foundPart = parts.stream()
                .filter(p -> getType() == null || p.getType() == getType())
                .filter(p -> getOwner() == null || p.getOwner() == getOwner())
                .filter(p -> p.getId(context) == getValue())
                .findFirst();

        if (foundPart.isPresent()) {
            return foundPart.get();
        } else {
            throw new PartException("No " + getHyperTalkIdentifier(context) + " found.");
        }
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
    public Integer getValue() {
        return id;
    }

    @Override
    public String getHyperTalkIdentifier(ExecutionContext context) {
        if (getOwner() == null || getOwner() == Owner.STACK) {
            return type.toString().toLowerCase() + " id " + id;
        } else {
            return getOwner().name().toLowerCase() + " " + type.toString().toLowerCase() + " id " + id;
        }
    }

    @Override
    public String toString() {
        return "PartIdSpecifier{" +
                "layer=" + layer +
                ", type=" + type +
                ", id=" + id +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        PartIdSpecifier that = (PartIdSpecifier) o;

        if (id != that.id) return false;
        if (layer != that.layer) return false;
        return type == that.type;
    }

    @Override
    public int hashCode() {
        int result = layer != null ? layer.hashCode() : 0;
        result = 31 * result + (type != null ? type.hashCode() : 0);
        result = 31 * result + id;
        return result;
    }
}
