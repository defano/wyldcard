package com.defano.wyldcard.parts.util;

import com.defano.wyldcard.parts.card.CardLayerPartModel;
import com.defano.wyldcard.parts.model.PartModel;

import java.util.Comparator;

public class ZOrderComparator implements Comparator<PartModel> {
    @Override
    public int compare(PartModel o1, PartModel o2) {
        return Integer.compare(
                o1.getKnownProperty(CardLayerPartModel.PROP_ZORDER).integerValue(),
                o2.getKnownProperty(CardLayerPartModel.PROP_ZORDER).integerValue()
        );
    }
}
