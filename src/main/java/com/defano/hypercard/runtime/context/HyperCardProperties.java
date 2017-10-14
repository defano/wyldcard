package com.defano.hypercard.runtime.context;

import com.defano.hypercard.awt.MouseManager;
import com.defano.hypercard.cursor.CursorManager;
import com.defano.hypercard.cursor.HyperCardCursor;
import com.defano.hypercard.fx.CurtainManager;
import com.defano.hypercard.paint.FontContext;
import com.defano.hypercard.paint.ToolsContext;
import com.defano.hypercard.parts.model.PropertiesModel;
import com.defano.hypercard.sound.SoundPlayer;
import com.defano.hypercard.window.WindowManager;
import com.defano.hypertalk.ast.common.Value;
import com.defano.hypertalk.exception.HtSemanticException;

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
    public final static String PROP_TEXTFONT = "textfont";
    public final static String PROP_TEXTSIZE = "textsize";
    public final static String PROP_TEXTSTYLE = "textstyle";

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
        defineProperty(PROP_LOCKSCREEN, new Value("false"), false);
        defineProperty(PROP_CLICKTEXT, new Value(""), true);
        defineProperty(PROP_MOUSEH, new Value(0), true);
        defineProperty(PROP_MOUSEV, new Value(0), true);
        defineProperty(PROP_SCREENRECT, new Value("0,0,0,0"), true);
        defineProperty(PROP_CLICKLOC, new Value("0, 0"), true);
        defineProperty(PROP_CLICKH, new Value("0"), true);
        defineProperty(PROP_CLICKV, new Value("0"), true);
        defineProperty(PROP_SOUND, new Value("done"), true);

        defineComputedSetterProperty(PROP_TEXTFONT, (model, propertyName, value) -> FontContext.getInstance().setSelectedFontFamily(value.stringValue()));
        defineComputedGetterProperty(PROP_TEXTFONT, (model, propertyName) -> new Value(FontContext.getInstance().getSelectedFontFamilyProvider().get()));

        defineComputedSetterProperty(PROP_TEXTSTYLE, (model, propertyName, value) -> FontContext.getInstance().setSelectedFontStyle(value));
        defineComputedGetterProperty(PROP_TEXTSTYLE, (model, propertyName) -> new Value(FontContext.getInstance().getSelectedFontStyleProvider().get()));

        defineComputedSetterProperty(PROP_TEXTSIZE, (model, propertyName, value) -> FontContext.getInstance().setSelectedFontSize(value.integerValue()));
        defineComputedGetterProperty(PROP_TEXTSIZE, (model, propertyName) -> new Value(FontContext.getInstance().getSelectedFontSizeProvider().get()));

        defineComputedSetterProperty(PROP_FILLED, (model, propertyName, value) -> ToolsContext.getInstance().getShapesFilledProvider().set(value.booleanValue()));
        defineComputedGetterProperty(PROP_FILLED, (model, propertyName) -> new Value(ToolsContext.getInstance().isShapesFilled()));

        defineComputedSetterProperty(PROP_CENTERED, (model, propertyName, value) -> ToolsContext.getInstance().getDrawCenteredProvider().set(value.booleanValue()));
        defineComputedGetterProperty(PROP_CENTERED, (model, propertyName) -> new Value(ToolsContext.getInstance().getDrawCenteredProvider().get()));

        defineComputedSetterProperty(PROP_MULTIPLE, (model, propertyName, value) -> ToolsContext.getInstance().getDrawMultipleProvider().set(value.booleanValue()));
        defineComputedGetterProperty(PROP_MULTIPLE, (model, propertyName) -> new Value(ToolsContext.getInstance().getDrawMultipleProvider().get()));

        defineComputedSetterProperty(PROP_CURSOR, (model, propertyName, value) -> CursorManager.getInstance().setActiveCursor(value));
        defineComputedGetterProperty(PROP_CURSOR, (model, propertyName) -> new Value (CursorManager.getInstance().getActiveCursor().hyperTalkName));

        defineComputedSetterProperty(PROP_GRID, (model, propertyName, value) -> ToolsContext.getInstance().getGridSpacingProvider().set(value.booleanValue() ? 8 : 1));
        defineComputedGetterProperty(PROP_GRID, (model, propertyName) -> new Value (ToolsContext.getInstance().getGridSpacingProvider().get() > 1));

        defineComputedSetterProperty(PROP_POLYSIDES, (model, propertyName, value) -> ToolsContext.getInstance().getShapeSidesProvider().set(value.integerValue()));
        defineComputedGetterProperty(PROP_POLYSIDES, (model, propertyName) -> new Value (ToolsContext.getInstance().getShapeSidesProvider().get()));

        defineComputedSetterProperty(PROP_PATTERN, (model, propertyName, value) -> {
            if (value.integerValue() >= 0 || value.integerValue() < 40) {
                ToolsContext.getInstance().getFillPatternProvider().set(value.integerValue());
            }
        });
        defineComputedGetterProperty(PROP_PATTERN, (model, propertyName) -> new Value (ToolsContext.getInstance().getFillPatternProvider().get()));

        defineComputedGetterProperty(PROP_MOUSEH, (model, propertyName) -> new Value(MouseManager.getMouseLoc().x));
        defineComputedGetterProperty(PROP_MOUSEV, (model, propertyName) -> new Value(MouseManager.getMouseLoc().y));
        defineComputedGetterProperty(PROP_SCREENRECT, (model, propertyName) -> new Value(WindowManager.getStackWindow().getWindow().getGraphicsConfiguration().getBounds()));
        defineComputedGetterProperty(PROP_CLICKLOC, (model, propertyName) -> new Value(MouseManager.getClickLoc()));
        defineComputedGetterProperty(PROP_CLICKH, (model, propertyName) -> new Value(MouseManager.getClickLoc().x));
        defineComputedGetterProperty(PROP_CLICKV, (model, propertyName) -> new Value(MouseManager.getClickLoc().y));
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
        setKnownProperty(PROP_LOCKSCREEN, new Value("false"));

        CursorManager.getInstance().setActiveCursor(HyperCardCursor.HAND);
    }
}
