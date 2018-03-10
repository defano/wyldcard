package com.defano.wyldcard.parts.card;

import com.defano.wyldcard.parts.model.PartModel;

public interface CardModelObserver {
    void onPartRemoved(PartModel removedPart);
}
