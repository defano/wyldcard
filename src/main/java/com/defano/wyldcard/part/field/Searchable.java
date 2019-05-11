package com.defano.wyldcard.part.field;

import com.defano.hypertalk.util.Range;
import com.defano.wyldcard.runtime.ExecutionContext;

public interface Searchable {

    void clearSearchHilites();
    void applySearchHilite(Range range);

    String getText(ExecutionContext context);
}
