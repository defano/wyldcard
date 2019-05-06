package com.defano.wyldcard.parts.field;

import com.defano.hypertalk.utils.Range;
import com.defano.wyldcard.runtime.ExecutionContext;

public interface Searchable {

    void clearSearchHilites();
    void applySearchHilite(Range range);

    String getText(ExecutionContext context);
}
