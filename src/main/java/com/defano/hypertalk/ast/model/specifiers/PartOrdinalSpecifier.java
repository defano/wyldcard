package com.defano.hypertalk.ast.model.specifiers;

import com.defano.hypertalk.ast.model.Ordinal;
import com.defano.hypertalk.ast.model.Owner;
import com.defano.hypertalk.ast.model.PartType;
import com.defano.wyldcard.runtime.context.ExecutionContext;

/**
 * Specifies a button, field, card or background part by ordinal number. For example, 'the second card' or 'the fifth
 * card field'
 */
public class PartOrdinalSpecifier implements PartSpecifier {

    private final PartType type;
    private final Owner layer;
    private final Ordinal ordinal;

    public PartOrdinalSpecifier(Owner layer, PartType type, Ordinal ordinal) {
        this.type = type;
        this.layer = layer;
        this.ordinal = ordinal;
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
