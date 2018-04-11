package com.defano.wyldcard.parts.field;

import com.defano.hypertalk.utils.Range;
import com.defano.wyldcard.runtime.context.ExecutionContext;

public interface Searchable {

    void clearSearchHilights();
    void applySearchHilight(Range range);

    String getText(ExecutionContext context);
}
