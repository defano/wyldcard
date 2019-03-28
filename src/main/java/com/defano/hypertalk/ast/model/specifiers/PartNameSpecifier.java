package com.defano.hypertalk.ast.model.specifiers;

import com.defano.hypertalk.ast.model.Owner;
import com.defano.hypertalk.ast.model.PartType;
import com.defano.wyldcard.parts.PartException;
import com.defano.wyldcard.parts.card.CardModel;
import com.defano.wyldcard.parts.finder.FindInCollectionSpecifier;
import com.defano.wyldcard.parts.model.PartModel;
import com.defano.wyldcard.runtime.context.ExecutionContext;

import java.util.List;
import java.util.Optional;

/**
 * Specifies a button, field, card or background by name. For example, 'card "Nifty Card"' or 'card button 'Press me!"'
 * Note that names are not unique; multiple parts can have the same name. HyperCard assumes the user is referring to
 * the part with the lowest number when ambiguously referring to parts by name.
 */
public class PartNameSpecifier implements FindInCollectionSpecifier {

    private final Owner layer;
    private final PartType type;
    private final String name;
    private final boolean marked;

    public PartNameSpecifier (Owner layer, PartType type, String name, boolean marked) {
        this.layer = layer;
        this.type = type;
        this.name = name;
        this.marked = marked;

        if (marked && type != PartType.CARD) {
            throw new IllegalStateException("Bug! Can only find marked cards; other parts disallowed.");
        }
    }

    public PartModel findInCollection(ExecutionContext context, List<PartModel> parts) throws PartException {
        Optional<PartModel> foundPart = parts.stream()
                .filter(p -> getType() == null || p.getType() == getType())
                .filter(p -> getOwner() == null || p.getOwner() == getOwner())
                .filter(p -> p.getName(context).equalsIgnoreCase(getValue()))
                .filter(p -> !marked || ( ((CardModel) p).isMarked(context) || p.equals(context.getCurrentCard().getPartModel())))
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
    public String getValue() {
        return name;
    }

    @Override
    public String getHyperTalkIdentifier(ExecutionContext context) {
        StringBuilder builder = new StringBuilder();
        if (getOwner() != null) {
            builder.append(getOwner().name().toLowerCase());
            builder.append(" ");
        }

        if (type != null) {
            builder.append(type.toString().toLowerCase());
            builder.append(" ");
        }

        if (name != null) {
            builder.append("\"");
            builder.append(name);
            builder.append("\"");
        }

        return builder.toString();
    }

    @Override
    public String toString() {
        return "PartNameSpecifier{" +
                "layer=" + layer +
                ", type=" + type +
                ", name='" + name + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        PartNameSpecifier that = (PartNameSpecifier) o;

        if (layer != that.layer) return false;
        if (type != that.type) return false;
        return name != null ? name.equals(that.name) : that.name == null;
    }

    @Override
    public int hashCode() {
        int result = layer != null ? layer.hashCode() : 0;
        result = 31 * result + (type != null ? type.hashCode() : 0);
        result = 31 * result + (name != null ? name.hashCode() : 0);
        return result;
    }
}
