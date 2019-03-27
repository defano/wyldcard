package com.defano.hypertalk.ast.model.specifiers;

import com.defano.hypertalk.ast.model.Owner;
import com.defano.hypertalk.ast.model.PartType;
import com.defano.hypertalk.ast.model.Position;
import com.defano.wyldcard.parts.PartException;
import com.defano.wyldcard.parts.finder.BackgroundFinder;
import com.defano.wyldcard.parts.finder.StackPartFinder;
import com.defano.wyldcard.parts.finder.StackPartFindingSpecifier;
import com.defano.wyldcard.parts.model.PartModel;
import com.defano.wyldcard.parts.stack.StackModel;
import com.defano.wyldcard.runtime.context.ExecutionContext;

/**
 * Specifies a card or background in positional relationship to the current card (next, previous or this). For example,
 * 'the next card' or 'prev background'
 */
public class PartPositionSpecifier implements PartSpecifier, BackgroundFinder, StackPartFindingSpecifier {

    private final Position position;
    private final Owner layer;
    private final PartType type;
    private final boolean marked;

    public PartPositionSpecifier(Owner layer, PartType type, Position position, boolean marked) {
        this.position = position;
        this.layer = layer;
        this.type = type;
        this.marked = marked;
    }

    @Override
    public PartModel find(ExecutionContext context, StackPartFinder finder) throws PartException {
        // Bail if request to find any kind of part other than a card or background
        if (getType() != PartType.BACKGROUND && getType() != PartType.CARD) {
            throw new PartException("Cannot find " + getType().toString().toLowerCase() + " by position.");
        }

        StackModel stackModel = finder.getStackModel();
        int thisCard = context.getCurrentCard().getPartModel().getCardIndexInStack();

        try {
            if (getType() == PartType.CARD) {
                switch (getPosition()) {
                    case NEXT:
                        return stackModel.getCardModel(thisCard + 1);
                    case PREV:
                        return stackModel.getCardModel(thisCard - 1);
                    case THIS:
                        return stackModel.getCardModel(thisCard);
                }
            }

            if (getType() == PartType.BACKGROUND) {
                switch (getPosition()) {
                    case NEXT:
                        return stackModel.getBackground(findNextBackground(stackModel).getBackgroundId());
                    case PREV:
                        return stackModel.getBackground(findPrevBackground(stackModel).getBackgroundId());
                    case THIS:
                        return stackModel.getCurrentCard().getBackgroundModel();
                }
            }

        } catch (Throwable t) {
            throw new PartException("No such " + getType().name().toLowerCase() + ".");
        }

        throw new PartException("Bug! Unhandled positional part type: " + getType());
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

    public Position getPosition() {
        return position;
    }

    @Override
    public String getHyperTalkIdentifier(ExecutionContext context) {
        if (layer == null) {
            return position.name().toLowerCase() + " " + type.toString().toLowerCase();
        } else if (type == null) {
            return position.name().toLowerCase() + " " + getOwner().name() + " part";
        } else {
            return position.name().toLowerCase() + " " + getOwner().name() + " " + type.toString().toLowerCase();
        }
    }

    @Override
    public String toString() {
        return "PartPositionSpecifier{" +
                "position=" + position +
                ", layer=" + layer +
                ", type=" + type +
                '}';
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
