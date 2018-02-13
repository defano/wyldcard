package com.defano.hypertalk.ast.model.specifiers;

import com.defano.hypertalk.ast.model.Owner;
import com.defano.hypertalk.ast.model.PartType;

/**
 * Specifies a button, field, card or background by its ID. For example, 'card id 13' or 'bg field id 11'
 */
public class PartIdSpecifier implements PartSpecifier {

    private final Owner layer;
    private final PartType type;
    private final int id;

    public PartIdSpecifier(Owner layer, PartType type, int id) {
        this.layer = layer;
        this.type = type;
        this.id = id;
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
    public String getHyperTalkIdentifier () {
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
