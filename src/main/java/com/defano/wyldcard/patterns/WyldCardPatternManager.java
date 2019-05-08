package com.defano.wyldcard.patterns;

import com.defano.wyldcard.WyldCard;
import com.google.inject.Singleton;

/**
 * Utility class responsible for listening to changes to the set of available patterns and invaliding the pattern cache
 * as required.
 */
@Singleton
public class WyldCardPatternManager implements PatternManager {

    @Override
    @SuppressWarnings({"ResultOfMethodCallIgnored", "DuplicateExpressions"})
    public void start() {
        WyldCard.getInstance().getPaintManager().getForegroundColorProvider().subscribe(color -> WyldCardPatternFactory.getInstance().invalidatePatternCache());
        WyldCard.getInstance().getPaintManager().getBackgroundColorProvider().subscribe(color -> WyldCardPatternFactory.getInstance().invalidatePatternCache());
    }
}
