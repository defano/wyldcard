package com.defano.hypertalk.ast.specifiers;

import com.defano.hypertalk.ast.common.Owner;
import com.defano.hypertalk.ast.common.PartType;
import com.defano.hypertalk.ast.common.Position;

/**
 * Specifies a card or background in positional relationship to the current card (next, previous or this). For example,
 * 'the next card' or 'prev background'
 */
public class PartPositionSpecifier implements PartSpecifier {

    private final Position position;
    private final Owner layer;
    private final PartType type;

    public PartPositionSpecifier(Owner layer, PartType type, Position position) {
        this.position = position;
        this.layer = layer;
        this.type = type;
    }

    @Override
    public Object getValue() {
        return position;
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
    public String getHyperTalkIdentifier() {
        if (layer == null) {
            return position.name().toLowerCase() + " " + type.toString().toLowerCase();
        } else if (type == null) {
            return position.name().toLowerCase() + " " + getOwner().name() + " part";
        } else {
            return position.name().toLowerCase() + " " + getOwner().name() + " " + type.toString().toLowerCase();
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        PartPositionSpecifier that = (PartPositionSpecifier) o;

        if (position != that.position) return false;
        if (layer != that.layer) return false;
        return type == that.type;
    }

    @Override
    public int hashCode() {
        int result = position != null ? position.hashCode() : 0;
        result = 31 * result + (layer != null ? layer.hashCode() : 0);
        result = 31 * result + (type != null ? type.hashCode() : 0);
        return result;
    }
}
