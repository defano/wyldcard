package com.defano.hypercard.parts.card;

import com.defano.hypercard.parts.model.PartModel;

public interface CardModelObserver {
    void onPartRemoved(PartModel removedPart);
}
