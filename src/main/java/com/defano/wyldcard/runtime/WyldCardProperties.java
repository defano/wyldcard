package com.defano.wyldcard.runtime;

import com.defano.wyldcard.parts.model.PropertiesModel;

public interface WyldCardProperties extends PropertiesModel {

    String PROP_ADDRESS = "address";
    String PROP_ITEMDELIMITER = "itemdelimiter";
    String PROP_LOCKSCREEN = "lockscreen";
    String PROP_CURSOR = "cursor";
    String PROP_FILLED = "filled";
    String PROP_CENTERED = "centered";
    String PROP_MULTIPLE = "multiple";
    String PROP_GRID = "grid";
    String PROP_POLYSIDES = "polysides";
    String PROP_PATTERN = "pattern";
    String PROP_LINESIZE = "linesize";
    String PROP_BRUSH = "brush";
    String PROP_TEXTFONT = "textfont";
    String PROP_TEXTSIZE = "textsize";
    String PROP_TEXTSTYLE = "textstyle";
    String PROP_SCRIPTTEXTFONT = "scripttextfont";
    String PROP_SCRIPTTEXTSIZE = "scripttextsize";
    String PROP_LOCKMESSAGES = "lockmessages";
    String PROP_THEME = "theme";
    String PROP_THEMS = "themes";
    String PROP_TEXTARROWS = "textarrows";

    /**
     * Resets those properties whose value is ephemeral (like the itemDelimiter) back to their default values.
     *
     * TODO: Ephemeral properties should not be global, as this allows cross-thread side effects. Move to ExecutionContext instead?
     */
    void resetProperties();

    /**
     * Gets the state of the textArrows property.
     * @return The state of the textArrows property.
     */
    boolean isTextArrows();

    /**
     * Gets the state of the lockMessages property.
     * @return The state of the lockMessages property.
     */
    boolean isLockMessages();
}
