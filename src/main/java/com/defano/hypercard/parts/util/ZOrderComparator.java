package com.defano.hypercard.parts.util;

import com.defano.hypercard.parts.card.CardLayerPartModel;
import com.defano.hypercard.parts.model.PartModel;

import java.util.Comparator;

public class ZOrderComparator implements Comparator<PartModel> {
    @Override
    public int compare(PartModel o1, PartModel o2) {
        return Integer.compare(o1.getKnownProperty(CardLayerPartModel.PROP_ZORDER).integerValue(), o2.getKnownProperty(CardLayerPartModel.PROP_ZORDER).integerValue());
    }
}
