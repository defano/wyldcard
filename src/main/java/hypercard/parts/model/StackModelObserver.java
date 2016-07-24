package hypercard.parts.model;

import hypercard.parts.CardPart;

public interface StackModelObserver {
    void onCurrentCardChanged(CardPart newCard);
}
