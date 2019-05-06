package com.defano.hypertalk.ast.model;

import com.defano.hypertalk.ast.model.specifiers.StackPartSpecifier;
import com.defano.wyldcard.WyldCard;
import com.defano.wyldcard.parts.bkgnd.BackgroundModel;
import com.defano.wyldcard.parts.card.CardModel;
import com.defano.wyldcard.parts.model.PartModel;
import com.defano.wyldcard.parts.stack.StackModel;
import com.defano.wyldcard.runtime.ExecutionContext;

import java.util.Objects;

public class Destination {

    private final StackModel stack;
    private final int cardIndex;

    public Destination(StackModel stackModel, int cardIndex) {
        this.stack = stackModel;
        this.cardIndex = cardIndex;
    }

    public static Destination ofStack(ExecutionContext context, String stackName, RemoteNavigationOptions navigationOptions) {
        StackModel model = WyldCard.getInstance().getStackManager().findStack(context, new StackPartSpecifier(stackName), navigationOptions);
        return ofPart(context, model);
    }

    public static Destination ofPart(ExecutionContext context, PartModel model) {
        Integer destinationIndex;
        StackModel destinationStack;

        // Part is a card in a stack
        if (model instanceof CardModel) {
            destinationStack = ((CardModel) model).getStackModel();
            return new Destination(destinationStack, model.getId());
        }

        // Part is a background in a stack
        else if (model instanceof BackgroundModel) {
            destinationStack = ((BackgroundModel) model).getStackModel();
            destinationIndex = destinationStack.getIndexOfBackground(model.getId());
            return new Destination(destinationStack, destinationStack.getCardModels().get(destinationIndex).getId());
        }

        // Part is the stack itself
        else if (model instanceof StackModel) {
            return new Destination((StackModel) model, ((StackModel) model).getCardModels().get(((StackModel) model).getCurrentCardIndex()).getId());
        }

        // Part model was null or otherwise can't resolve destination
        return null;
    }

    public StackModel getStack() {
        return stack;
    }

    public int getCardIndex() {
        return cardIndex;
    }

    public String getHypertalkIdentifier(ExecutionContext context) {
        return "card " + (cardIndex + 1) + " of " + stack.getLongName(context);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Destination that = (Destination) o;
        return cardIndex == that.cardIndex &&
                Objects.equals(stack, that.stack);
    }

    @Override
    public int hashCode() {
        return Objects.hash(stack, cardIndex);
    }
}
