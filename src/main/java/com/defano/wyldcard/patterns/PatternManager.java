package com.defano.wyldcard.patterns;

import com.defano.wyldcard.WyldCard;
import com.defano.wyldcard.runtime.context.DefaultToolsManager;

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
        WyldCard.getInstance().getToolsManager().getForegroundColorProvider().subscribe(color -> WyldCardPatternFactory.getInstance().invalidatePatternCache());
        WyldCard.getInstance().getToolsManager().getBackgroundColorProvider().subscribe(color -> WyldCardPatternFactory.getInstance().invalidatePatternCache());
    }
}
