package com.defano.wyldcard.parts.field;

import com.defano.hypertalk.utils.Range;

public interface Searchable {

    void clearSearchHilights();
    void applySearchHilight(Range range);

    String getText();
}
