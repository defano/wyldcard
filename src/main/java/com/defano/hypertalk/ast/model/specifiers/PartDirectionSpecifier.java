package com.defano.hypertalk.ast.model.specifiers;

import com.defano.hypertalk.ast.model.Destination;
import com.defano.hypertalk.ast.model.Direction;
import com.defano.hypertalk.ast.model.Owner;
import com.defano.hypertalk.ast.model.PartType;
import com.defano.wyldcard.WyldCard;
import com.defano.wyldcard.parts.model.PartModel;
import com.defano.wyldcard.runtime.context.ExecutionContext;

public class PartDirectionSpecifier implements PartSpecifier {

    private final Direction direction;

    public PartDirectionSpecifier(Direction direction) {
        this.direction = direction;
    }

    public PartModel find() {
        Destination destination;

        switch (getValue()) {
            case BACK:
                destination = WyldCard.getInstance().getNavigationManager().getNavigationStack().peekBack();
                break;
            case FORTH:
                destination = WyldCard.getInstance().getNavigationManager().getNavigationStack().peekForward();
                break;
            default:
                throw new IllegalStateException("Bug! Unimplemented direction: " + getValue());
        }

        return destination.getStack().getCardModel(destination.getCardIndex());
    }

    @Override
    public Direction getValue() {
        return direction;
    }

    @Override
    public Owner getOwner() {
        return Owner.STACK;
    }

    @Override
    public PartType getType() {
        return PartType.CARD;
    }

    @Override
    public String getHyperTalkIdentifier(ExecutionContext context) {
        switch (direction) {
            case BACK:
                return "recent card";
            case FORTH:
                return "forth";
            default:
                throw new IllegalStateException("Bug! Unimplemented direction: " + direction);
        }
    }
}
