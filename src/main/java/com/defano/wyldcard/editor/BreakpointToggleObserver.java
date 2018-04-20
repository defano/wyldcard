package com.defano.wyldcard.editor;

import java.util.List;

public interface BreakpointToggleObserver {
    void onBookmarkToggle(List<Integer> breakpoints);
}
