package com.defano.wyldcard.editor;

import org.fife.ui.rtextarea.GutterIconInfo;

import java.util.List;

public interface BreakpointToggleObserver {
    void onBookmarkToggle(List<Integer> breakpoints);
}
