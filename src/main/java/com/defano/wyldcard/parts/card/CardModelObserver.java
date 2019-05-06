package com.defano.wyldcard.parts.card;

import com.defano.wyldcard.parts.model.PartModel;
import com.defano.wyldcard.runtime.ExecutionContext;

public interface CardModelObserver {
    void onPartRemoved(ExecutionContext context, PartModel removedPart);
}
