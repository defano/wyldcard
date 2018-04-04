package com.defano.wyldcard.parts.util;

import com.defano.wyldcard.parts.card.CardLayerPartModel;
import com.defano.wyldcard.parts.model.PartModel;
import com.defano.wyldcard.runtime.context.ExecutionContext;

import java.util.Comparator;

public class ZOrderComparator implements Comparator<PartModel> {

    private final ExecutionContext context;

    public ZOrderComparator(ExecutionContext context) {
        this.context = context;
    }

    @Override
    public int compare(PartModel o1, PartModel o2) {
        return Integer.compare(
                o1.getKnownProperty(context, CardLayerPartModel.PROP_ZORDER).integerValue(),
                o2.getKnownProperty(context, CardLayerPartModel.PROP_ZORDER).integerValue()
        );
    }
}
