package com.defano.hypercard.parts.util;

import com.defano.hypercard.parts.Part;
import com.defano.hypercard.parts.model.PartModel;

import java.util.Comparator;

public class ZOrderComparator implements Comparator<Part> {
    @Override
    public int compare(Part o1, Part o2) {
        return new Integer(o1.getPartModel().getKnownProperty(PartModel.PROP_ZORDER).integerValue())
                .compareTo(o2.getPartModel().getKnownProperty(PartModel.PROP_ZORDER).integerValue());
    }
}
