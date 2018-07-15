package com.defano.hypertalk.ast.model;

import com.defano.wyldcard.parts.stack.StackModel;

import java.util.Objects;

public class Destination {
    private final StackModel stack;
    private final int cardId;

    public Destination(StackModel stackModel, int cardIndex) {
        this.stack = stackModel;
        this.cardId = cardIndex;
    }

    public StackModel getStack() {
        return stack;
    }

    public int getCardId() {
        return cardId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Destination that = (Destination) o;
        return cardId == that.cardId &&
                Objects.equals(stack, that.stack);
    }

    @Override
    public int hashCode() {
        return Objects.hash(stack, cardId);
    }
}
