package com.defano.hypercard.patterns;

import com.defano.hypercard.runtime.context.ToolsContext;

/**
 * Utility class responsible for listening to changes to the set of available patterns and invaliding the pattern cache
 * as required.
 */
public class PatternManager {

    private final static PatternManager instance = new PatternManager();

    private PatternManager() {}

    public static PatternManager getInstance() {
        return instance;
    }

    public void start() {
        ToolsContext.getInstance().getForegroundColorProvider().subscribe(color -> HyperCardPatternFactory.getInstance().invalidatePatternCache());
        ToolsContext.getInstance().getBackgroundColorProvider().subscribe(color -> HyperCardPatternFactory.getInstance().invalidatePatternCache());
    }
}
