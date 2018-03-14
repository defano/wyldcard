package com.defano.wyldcard.runtime;

import com.defano.wyldcard.awt.MouseManager;
import com.defano.wyldcard.cursor.CursorManager;
import com.defano.wyldcard.cursor.HyperCardCursor;
import com.defano.wyldcard.fx.CurtainManager;
import com.defano.wyldcard.parts.model.PropertiesModel;
import com.defano.wyldcard.patterns.BasicBrushResolver;
import com.defano.wyldcard.runtime.context.FontContext;
import com.defano.wyldcard.runtime.context.SelectionContext;
import com.defano.wyldcard.runtime.context.ToolsContext;
import com.defano.wyldcard.sound.SoundPlayer;
import com.defano.wyldcard.window.WindowManager;
import com.defano.hypertalk.ast.model.Value;
import com.defano.hypertalk.exception.HtSemanticException;

/**
 * A model of global, HyperCard properties. Note that this model is not part of a stack and is therefore never saved.
 * Changes to these properties reset to their default on application startup (and some, like 'itemDelimiter' reset to
 * their default value whenever their are no scripts executing).
 */
public class HyperCardProperties extends PropertiesModel {

    public final static String PROP_ITEMDELIMITER = "itemdelimiter";
    public final static String PROP_SELECTEDTEXT = "selectedtext";
    public final static String PROP_SELECTEDCHUNK = "selectedchunk";
    public final static String PROP_SELECTEDFIELD = "selectedfield";
    public final static String PROP_SELECTEDLINE = "selectedline";
    public final static String PROP_CLICKTEXT = "clicktext";
    public final static String PROP_LOCKSCREEN = "lockscreen";
    public final static String PROP_MOUSEH = "mouseh";
    public final static String PROP_MOUSEV = "mousev";
    public final static String PROP_SCREENRECT = "screenrect";
    public final static String PROP_CLICKLOC = "clickloc";
    public final static String PROP_CLICKH = "clickh";
    public final static String PROP_CLICKV = "clickv";
    public final static String PROP_SOUND = "sound";
    public final static String PROP_CURSOR = "cursor";
    public final static String PROP_FILLED = "filled";
    public final static String PROP_CENTERED = "centered";
    public final static String PROP_MULTIPLE = "multiple";
    public final static String PROP_GRID = "grid";
    public final static String PROP_POLYSIDES = "polysides";
    public final static String PROP_PATTERN = "pattern";
    public final static String PROP_LINESIZE = "linesize";
    public final static String PROP_BRUSH = "brush";
    public final static String PROP_TEXTFONT = "textfont";
    public final static String PROP_TEXTSIZE = "textsize";
    public final static String PROP_TEXTSTYLE = "textstyle";
    public final static String PROP_SCRIPTTEXTFONT = "scripttextfont";
    public final static String PROP_SCRIPTTEXTSIZE = "scripttextsize";
    public final static String PROP_SYSTEMVERSION = "systemversion";
    public final static String PROP_FOUNDCHUNK = "foundchunk";
    public final static String PROP_FOUNDFIELD = "foundfield";
    public final static String PROP_FOUNDLINE = "foundline";
    public final static String PROP_FOUNDTEXT = "foundtext";
    public final static String PROP_LOCKMESSAGES = "lockmessages";

    private final static HyperCardProperties instance = new HyperCardProperties();

    public static HyperCardProperties getInstance() {
        return instance;
    }

    private HyperCardProperties() {
        super();

        defineProperty(PROP_ITEMDELIMITER, new Value(","), false);
        defineProperty(PROP_SELECTEDTEXT, new Value(), true);
        defineProperty(PROP_SELECTEDCHUNK, new Value(), true);
        defineProperty(PROP_SELECTEDFIELD, new Value(), true);
        defineProperty(PROP_SELECTEDLINE, new Value(), true);
        defineProperty(PROP_LOCKSCREEN, new Value(false), false);
        defineProperty(PROP_CLICKTEXT, new Value(""), true);
        defineProperty(PROP_MOUSEH, new Value(0), true);
        defineProperty(PROP_MOUSEV, new Value(0), true);
        defineProperty(PROP_SCREENRECT, new Value("0,0,0,0"), true);
        defineProperty(PROP_CLICKLOC, new Value("0, 0"), true);
        defineProperty(PROP_CLICKH, new Value("0"), true);
        defineProperty(PROP_CLICKV, new Value("0"), true);
        defineProperty(PROP_SOUND, new Value("done"), true);
        defineProperty(PROP_SCRIPTTEXTFONT, new Value("Monaco"), false);
        defineProperty(PROP_SCRIPTTEXTSIZE, new Value(12), false);
        defineProperty(PROP_FOUNDCHUNK, new Value(), true);
        defineProperty(PROP_FOUNDFIELD, new Value(), true);
        defineProperty(PROP_FOUNDLINE, new Value(), true);
        defineProperty(PROP_FOUNDTEXT, new Value(), true);
        defineProperty(PROP_LOCKMESSAGES, new Value(true), false);

        defineComputedReadOnlyProperty(PROP_SYSTEMVERSION, (model, propertyName) -> new Value(System.getProperty("java.version")));

        defineComputedSetterProperty(PROP_TEXTFONT, (model, propertyName, value) -> FontContext.getInstance().setSelectedFontFamily(value.stringValue()));
        defineComputedGetterProperty(PROP_TEXTFONT, (model, propertyName) -> new Value(FontContext.getInstance().getSelectedFontFamily()));

        defineComputedSetterProperty(PROP_TEXTSTYLE, (model, propertyName, value) -> FontContext.getInstance().setSelectedFontStyle(value));
        defineComputedGetterProperty(PROP_TEXTSTYLE, (model, propertyName) -> new Value(FontContext.getInstance().getSelectedFontStyle()));

        defineComputedSetterProperty(PROP_TEXTSIZE, (model, propertyName, value) -> FontContext.getInstance().setSelectedFontSize(value.integerValue()));
        defineComputedGetterProperty(PROP_TEXTSIZE, (model, propertyName) -> new Value(FontContext.getInstance().getSelectedFontSize()));

        defineComputedGetterProperty(PROP_BRUSH, (model, propertyName) -> BasicBrushResolver.valueOfBasicBrush(ToolsContext.getInstance().getSelectedBrush()));
        defineComputedSetterProperty(PROP_BRUSH, (model, propertyName, value) -> ToolsContext.getInstance().setSelectedBrush(BasicBrushResolver.basicBrushOfValue(value)));

        defineComputedSetterProperty(PROP_LINESIZE, (model, propertyName, value) -> ToolsContext.getInstance().setLineWidth(value.integerValue()));
        defineComputedGetterProperty(PROP_LINESIZE, (model, propertyName) -> new Value(ToolsContext.getInstance().getLineWidth()));

        defineComputedSetterProperty(PROP_FILLED, (model, propertyName, value) -> ToolsContext.getInstance().setShapesFilled(value.booleanValue()));
        defineComputedGetterProperty(PROP_FILLED, (model, propertyName) -> new Value(ToolsContext.getInstance().isShapesFilled()));

        defineComputedSetterProperty(PROP_CENTERED, (model, propertyName, value) -> ToolsContext.getInstance().setDrawCentered(value.booleanValue()));
        defineComputedGetterProperty(PROP_CENTERED, (model, propertyName) -> new Value(ToolsContext.getInstance().isDrawCentered()));

        defineComputedSetterProperty(PROP_MULTIPLE, (model, propertyName, value) -> ToolsContext.getInstance().setDrawMultiple(value.booleanValue()));
        defineComputedGetterProperty(PROP_MULTIPLE, (model, propertyName) -> new Value(ToolsContext.getInstance().isDrawMultiple()));

        defineComputedSetterProperty(PROP_CURSOR, (model, propertyName, value) -> CursorManager.getInstance().setActiveCursor(value));
        defineComputedGetterProperty(PROP_CURSOR, (model, propertyName) -> new Value (CursorManager.getInstance().getActiveCursor().hyperTalkName));

        defineComputedSetterProperty(PROP_GRID, (model, propertyName, value) -> ToolsContext.getInstance().setGridSpacing(value.booleanValue() ? 8 : 1));
        defineComputedGetterProperty(PROP_GRID, (model, propertyName) -> new Value (ToolsContext.getInstance().getGridSpacing() > 1));

        defineComputedSetterProperty(PROP_POLYSIDES, (model, propertyName, value) -> ToolsContext.getInstance().setShapeSides(value.integerValue()));
        defineComputedGetterProperty(PROP_POLYSIDES, (model, propertyName) -> new Value (ToolsContext.getInstance().getShapeSides()));

        defineComputedSetterProperty(PROP_PATTERN, (model, propertyName, value) -> {
            if (value.integerValue() >= 0 && value.integerValue() < 40) {
                ToolsContext.getInstance().setFillPattern(value.integerValue());
            }
        });
        defineComputedGetterProperty(PROP_PATTERN, (model, propertyName) -> new Value (ToolsContext.getInstance().getFillPattern()));

        defineComputedGetterProperty(PROP_MOUSEH, (model, propertyName) -> new Value(MouseManager.getInstance().getMouseLoc().x));
        defineComputedGetterProperty(PROP_MOUSEV, (model, propertyName) -> new Value(MouseManager.getInstance().getMouseLoc().y));
        defineComputedGetterProperty(PROP_SCREENRECT, (model, propertyName) -> new Value(WindowManager.getInstance().getStackWindow().getWindow().getGraphicsConfiguration().getBounds()));
        defineComputedGetterProperty(PROP_CLICKLOC, (model, propertyName) -> new Value(MouseManager.getInstance().getClickLoc()));
        defineComputedGetterProperty(PROP_CLICKH, (model, propertyName) -> new Value(MouseManager.getInstance().getClickLoc().x));
        defineComputedGetterProperty(PROP_CLICKV, (model, propertyName) -> new Value(MouseManager.getInstance().getClickLoc().y));
        defineComputedGetterProperty(PROP_SOUND, (model, propertyName) -> new Value(SoundPlayer.getSound()));

        defineComputedGetterProperty(PROP_SELECTEDLINE, (model, propertyName) -> {
            try {
                return SelectionContext.getInstance().getManagedSelection().getSelectedLineExpression();
            } catch (HtSemanticException e) {
                return new Value();
            }
        });

        defineComputedGetterProperty(PROP_SELECTEDFIELD, (model, propertyName) -> {
            try {
                return SelectionContext.getInstance().getManagedSelection().getSelectedFieldExpression();
            } catch (HtSemanticException e) {
                return new Value();
            }
        });

        defineComputedGetterProperty(PROP_SELECTEDCHUNK, (model, propertyName) -> {
            try {
                return SelectionContext.getInstance().getManagedSelection().getSelectedChunkExpression();
            } catch (HtSemanticException e) {
                return new Value();
            }
        });

        defineComputedGetterProperty(PROP_SELECTEDTEXT, (model, propertyName) -> {
            try {
                return SelectionContext.getInstance().getSelection();
            } catch (HtSemanticException e) {
                return new Value();
            }
        });

        addPropertyWillChangeObserver((property, oldValue, newValue) -> {
            switch (property.toLowerCase()) {
                case PROP_LOCKSCREEN:
                    CurtainManager.getInstance().setScreenLocked(newValue.booleanValue());
                    break;
            }
        });
    }

    public void resetProperties() {
        setKnownProperty(PROP_ITEMDELIMITER, new Value(","));
        setKnownProperty(PROP_LOCKSCREEN, new Value(false));
        setKnownProperty(PROP_LOCKMESSAGES, new Value(false));

        CursorManager.getInstance().setActiveCursor(HyperCardCursor.HAND);
    }
}
