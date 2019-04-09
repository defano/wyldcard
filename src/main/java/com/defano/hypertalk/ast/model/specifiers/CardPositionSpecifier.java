package com.defano.hypertalk.ast.model.specifiers;

import com.defano.hypertalk.ast.model.Owner;
import com.defano.hypertalk.ast.model.PartType;
import com.defano.hypertalk.ast.model.Position;
import com.defano.wyldcard.parts.PartException;
import com.defano.wyldcard.parts.bkgnd.BackgroundModel;
import com.defano.wyldcard.parts.card.CardModel;
import com.defano.wyldcard.parts.finder.FindInCollectionSpecifier;
import com.defano.wyldcard.parts.model.PartModel;
import com.defano.wyldcard.parts.stack.StackModel;
import com.defano.wyldcard.runtime.context.ExecutionContext;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Specifies a card or background in positional relationship to the current card (next, previous or this). For example,
 * 'the next card' or 'the prev background'
 */
public class CardPositionSpecifier implements FindInCollectionSpecifier, PartSpecifier {

    private final Position position;
    private final Owner layer;
    private final PartType type;
    private final boolean marked;

    public CardPositionSpecifier(Owner layer, PartType type, Position position, boolean marked) {
        this.position = position;
        this.layer = layer;
        this.type = type;
        this.marked = marked;

        if (type != PartType.CARD && type != PartType.BACKGROUND) {
            throw new IllegalArgumentException("Bug! Cannot find this by position: " + type);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public PartModel findInCollection(ExecutionContext context, List<PartModel> collection) throws PartException {
        CardModel currentCard = context.getCurrentCard().getPartModel();
        BackgroundModel currentBkgnd = context.getCurrentCard().getPartModel().getBackgroundModel();

        List<PartModel> eligibleParts = collection.stream()
                .filter(pm -> pm instanceof CardModel)
                .map(pm -> (CardModel) pm)
                .filter(pm -> !marked || (pm.isMarked(context) || pm.equals(currentCard)))
                .map(pm -> type == PartType.BACKGROUND ? pm.getBackgroundModel() : pm)
                .distinct()
                .collect(Collectors.toList());

        int indexOfThis = type == PartType.CARD ? eligibleParts.indexOf(currentCard) : eligibleParts.indexOf(currentBkgnd);

        if (indexOfThis >= 0) {
            switch (position) {
                case NEXT:
                    if (indexOfThis < eligibleParts.size() - 1) {
                        return eligibleParts.get(indexOfThis + 1);
                    } else {
                        return eligibleParts.get(0);
                    }
                case PREV:
                    if (indexOfThis > 0) {
                        return eligibleParts.get(indexOfThis - 1);
                    } else {
                        return eligibleParts.get(eligibleParts.size() - 1);
                    }
                case THIS:
                    return eligibleParts.get(indexOfThis);
            }
        }

        throw new PartException("No such card.");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public PartModel findInStack(ExecutionContext context, StackModel stackModel) throws PartException {
        return findInCollection(context, stackModel.getCardModels().stream().map(m -> (PartModel) m).collect(Collectors.toList()));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Object getValue() {
        return position;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Owner getOwner() {
        return layer;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public PartType getType() {
        return type;
    }

    public Position getPosition() {
        return position;
    }

    /**
     * {@inheritDoc}
     */
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

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        CardPositionSpecifier that = (CardPositionSpecifier) o;

        if (position != that.position) return false;
        if (layer != that.layer) return false;
        return type == that.type;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        int result = position != null ? position.hashCode() : 0;
        result = 31 * result + (layer != null ? layer.hashCode() : 0);
        result = 31 * result + (type != null ? type.hashCode() : 0);
        return result;
    }
}
