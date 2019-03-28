package com.defano.hypertalk.ast.model.specifiers;

import com.defano.hypertalk.ast.model.Owner;
import com.defano.hypertalk.ast.model.PartType;
import com.defano.wyldcard.parts.PartException;
import com.defano.wyldcard.parts.finder.FindInCollectionSpecifier;
import com.defano.wyldcard.parts.model.PartModel;
import com.defano.wyldcard.runtime.context.ExecutionContext;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Specifies a button, field, card or background part by cardinal number. For example, 'card 14' or 'button 2'
 */
public class PartNumberSpecifier implements FindInCollectionSpecifier {

    private final PartType type;
    private final Owner layer;
    private final int number;

    public PartNumberSpecifier(Owner layer, PartType type, int number) {
        this.layer = layer;
        this.number = number;
        this.type = type;
    }

    public PartModel findInCollection(ExecutionContext context, List<PartModel> parts) throws PartException {
        List<PartModel> foundParts = parts.stream()
                .filter(p -> getType() == null || p.getType() == getType())
                .filter(p -> getOwner() == null || p.getOwner() == getOwner())
                .collect(Collectors.toList());

        int partIndex = (int) getValue() - 1;

        if (partIndex >= foundParts.size() || partIndex < 0) {
            throw new PartException("No " + getHyperTalkIdentifier(context) + " found.");
        } else {
            return foundParts.get(partIndex);
        }
    }

    @Override
    public Object getValue() {
        return number;
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
            return type.toString().toLowerCase() + " " + number;
        } else if (type == null) {
            return getOwner().name().toLowerCase() + " part " + number;
        } else {
            return getOwner().name().toLowerCase() + " " + type.toString().toLowerCase() + " " + number;
        }
    }

    @Override
    public String toString() {
        return "PartNumberSpecifier{" +
                "type=" + type +
                ", layer=" + layer +
                ", number=" + number +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        PartNumberSpecifier that = (PartNumberSpecifier) o;

        if (number != that.number) return false;
        if (type != that.type) return false;
        return layer == that.layer;
    }

    @Override
    public int hashCode() {
        int result = type != null ? type.hashCode() : 0;
        result = 31 * result + (layer != null ? layer.hashCode() : 0);
        result = 31 * result + number;
        return result;
    }
}
