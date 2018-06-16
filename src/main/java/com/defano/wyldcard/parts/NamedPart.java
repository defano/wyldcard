package com.defano.wyldcard.parts;

import com.defano.wyldcard.runtime.context.ExecutionContext;

public interface NamedPart {

    /**
     * Gets the "short" name of this part, typically just the name assigned by the user to this part but may return
     * the part's ID if no name has been assigned. For example, 'My Button'
     *
     * @param context The execution context
     * @return The short name of this part
     */
    String getShortName(ExecutionContext context);

    /**
     * Gets the "abbreviated" name of this part, typically the short name of this part, prepended with its part type
     * to produce a valid HyperTalk part expression. For example, 'button "My Button"'
     *
     * @param context The execution context
     * @return The short name of this part
     */
    String getAbbreviatedName(ExecutionContext context);

    /**
     * Gets the "fully qualified" name of this part, typically the long name of the part followed by the long-name of
     * the owning part. For example, 'card button "My Button' of card id 13 of stack "My Stack"'
     *
     * @param context The execution context
     * @return The long name of this part
     */
    String getLongName(ExecutionContext context);
}