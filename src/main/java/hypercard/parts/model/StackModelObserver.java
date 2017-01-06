package hypercard.parts.model;

import hypercard.parts.CardPart;

public interface StackModelObserver {
    void onCardClosing(CardPart oldCard);
    void onCardOpening(CardPart newCard);
    void onCardOpened(CardPart newCard);
}
