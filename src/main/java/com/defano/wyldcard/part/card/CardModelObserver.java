package com.defano.wyldcard.part.card;

import com.defano.wyldcard.part.model.PartModel;
import com.defano.wyldcard.runtime.ExecutionContext;

public interface CardModelObserver {
    void onPartRemoved(ExecutionContext context, PartModel removedPart);
}
