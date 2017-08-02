package com.defano.hypercard.parts.util;

import com.defano.hypercard.parts.model.CardLayerPartModel;
import com.defano.hypercard.parts.model.PartModel;

import java.util.Comparator;

public class ZOrderComparator implements Comparator<PartModel> {
    @Override
    public int compare(PartModel o1, PartModel o2) {
        return new Integer(o1.getKnownProperty(CardLayerPartModel.PROP_ZORDER).integerValue())
                .compareTo(o2.getKnownProperty(CardLayerPartModel.PROP_ZORDER).integerValue());
    }
}
