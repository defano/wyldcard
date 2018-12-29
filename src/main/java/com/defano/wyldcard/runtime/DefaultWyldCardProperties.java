package com.defano.wyldcard.runtime;

import com.defano.wyldcard.WyldCard;
import com.defano.wyldcard.cursor.HyperCardCursor;
import com.defano.wyldcard.parts.model.DefaultPropertiesModel;
import com.defano.wyldcard.patterns.BasicBrushResolver;
import com.defano.wyldcard.runtime.context.ExecutionContext;
import com.defano.hypertalk.ast.model.Value;
import com.defano.hypertalk.exception.HtSemanticException;
import com.google.inject.Singleton;

/**
 * A model of global, HyperCard properties. Note that this model is not part of a stack and is therefore never saved.
 * Changes to these properties reset to their default on application startup (and some, like 'itemDelimiter' reset to
 * their default value whenever their are no scripts executing).
 */
@Singleton
public class DefaultWyldCardProperties extends DefaultPropertiesModel implements WyldCardProperties {

    public DefaultWyldCardProperties() {
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

        defineComputedReadOnlyProperty(PROP_SYSTEMVERSION, (context, model, propertyName) -> new Value(System.getProperty("java.version")));

        defineComputedReadOnlyProperty(PROP_THEMS, (context, model, propertyName) -> Value.ofItems(WyldCard.getInstance().getWindowManager().getThemeNames()));
        defineComputedGetterProperty(PROP_THEME, (context, model, propertyName) -> new Value(WyldCard.getInstance().getWindowManager().getCurrentThemeName()));
        defineComputedSetterProperty(PROP_THEME, (context, model, propertyName, value) -> WyldCard.getInstance().getWindowManager().setTheme(WyldCard.getInstance().getWindowManager().getThemeClassForName(value.toString())));

        defineComputedSetterProperty(PROP_TEXTFONT, (context, model, propertyName, value) -> WyldCard.getInstance().getFontManager().setSelectedFontFamily(value.toString()));
        defineComputedGetterProperty(PROP_TEXTFONT, (context, model, propertyName) -> new Value(WyldCard.getInstance().getFontManager().getSelectedFontFamily()));

        defineComputedSetterProperty(PROP_TEXTSTYLE, (context, model, propertyName, value) -> WyldCard.getInstance().getFontManager().setSelectedFontStyle(value));
        defineComputedGetterProperty(PROP_TEXTSTYLE, (context, model, propertyName) -> new Value(WyldCard.getInstance().getFontManager().getSelectedFontStyle()));

        defineComputedSetterProperty(PROP_TEXTSIZE, (context, model, propertyName, value) -> WyldCard.getInstance().getFontManager().setSelectedFontSize(value.integerValue()));
        defineComputedGetterProperty(PROP_TEXTSIZE, (context, model, propertyName) -> new Value(WyldCard.getInstance().getFontManager().getSelectedFontSize()));

        defineComputedGetterProperty(PROP_BRUSH, (context, model, propertyName) -> BasicBrushResolver.valueOfBasicBrush(WyldCard.getInstance().getToolsManager().getSelectedBrush()));
        defineComputedSetterProperty(PROP_BRUSH, (context, model, propertyName, value) -> WyldCard.getInstance().getToolsManager().setSelectedBrush(BasicBrushResolver.basicBrushOfValue(value)));

        defineComputedSetterProperty(PROP_LINESIZE, (context, model, propertyName, value) -> WyldCard.getInstance().getToolsManager().setLineWidth(value.integerValue()));
        defineComputedGetterProperty(PROP_LINESIZE, (context, model, propertyName) -> new Value(WyldCard.getInstance().getToolsManager().getLineWidth()));

        defineComputedSetterProperty(PROP_FILLED, (context, model, propertyName, value) -> WyldCard.getInstance().getToolsManager().setShapesFilled(value.booleanValue()));
        defineComputedGetterProperty(PROP_FILLED, (context, model, propertyName) -> new Value(WyldCard.getInstance().getToolsManager().isShapesFilled()));

        defineComputedSetterProperty(PROP_CENTERED, (context, model, propertyName, value) -> WyldCard.getInstance().getToolsManager().setDrawCentered(value.booleanValue()));
        defineComputedGetterProperty(PROP_CENTERED, (context, model, propertyName) -> new Value(WyldCard.getInstance().getToolsManager().isDrawCentered()));

        defineComputedSetterProperty(PROP_MULTIPLE, (context, model, propertyName, value) -> WyldCard.getInstance().getToolsManager().setDrawMultiple(value.booleanValue()));
        defineComputedGetterProperty(PROP_MULTIPLE, (context, model, propertyName) -> new Value(WyldCard.getInstance().getToolsManager().isDrawMultiple()));

        defineComputedSetterProperty(PROP_CURSOR, (context, model, propertyName, value) -> WyldCard.getInstance().getCursorManager().setActiveCursor(value));
        defineComputedGetterProperty(PROP_CURSOR, (context, model, propertyName) -> new Value (WyldCard.getInstance().getCursorManager().getActiveCursor().hyperTalkName));

        defineComputedSetterProperty(PROP_GRID, (context, model, propertyName, value) -> WyldCard.getInstance().getToolsManager().setGridSpacing(value.booleanValue() ? 8 : 1));
        defineComputedGetterProperty(PROP_GRID, (context, model, propertyName) -> new Value (WyldCard.getInstance().getToolsManager().getGridSpacing() > 1));

        defineComputedSetterProperty(PROP_POLYSIDES, (context, model, propertyName, value) -> WyldCard.getInstance().getToolsManager().setShapeSides(value.integerValue()));
        defineComputedGetterProperty(PROP_POLYSIDES, (context, model, propertyName) -> new Value (WyldCard.getInstance().getToolsManager().getShapeSides()));

        defineComputedSetterProperty(PROP_PATTERN, (context, model, propertyName, value) -> {
            if (value.integerValue() >= 0 && value.integerValue() < 40) {
                WyldCard.getInstance().getToolsManager().setFillPattern(value.integerValue());
            }
        });
        defineComputedGetterProperty(PROP_PATTERN, (context, model, propertyName) -> new Value (WyldCard.getInstance().getToolsManager().getFillPattern()));

        defineComputedGetterProperty(PROP_MOUSEH, (context, model, propertyName) -> new Value(WyldCard.getInstance().getMouseManager().getMouseLoc(context).x));
        defineComputedGetterProperty(PROP_MOUSEV, (context, model, propertyName) -> new Value(WyldCard.getInstance().getMouseManager().getMouseLoc(context).y));
        defineComputedGetterProperty(PROP_SCREENRECT, (context, model, propertyName) -> new Value(WyldCard.getInstance().getWindowManager().getWindowForStack(context, context.getCurrentStack()).getWindow().getGraphicsConfiguration().getBounds()));
        defineComputedGetterProperty(PROP_CLICKLOC, (context, model, propertyName) -> new Value(WyldCard.getInstance().getMouseManager().getClickLoc()));
        defineComputedGetterProperty(PROP_CLICKH, (context, model, propertyName) -> new Value(WyldCard.getInstance().getMouseManager().getClickLoc().x));
        defineComputedGetterProperty(PROP_CLICKV, (context, model, propertyName) -> new Value(WyldCard.getInstance().getMouseManager().getClickLoc().y));
        defineComputedGetterProperty(PROP_SOUND, (context, model, propertyName) -> new Value(WyldCard.getInstance().getSoundManager().getSound()));

        defineComputedGetterProperty(PROP_SELECTEDLINE, (context, model, propertyName) -> {
            try {
                return WyldCard.getInstance().getSelectionManager().getManagedSelection(context).getSelectedLineExpression(context);
            } catch (HtSemanticException e) {
                return new Value();
            }
        });

        defineComputedGetterProperty(PROP_SELECTEDFIELD, (context, model, propertyName) -> {
            try {
                return WyldCard.getInstance().getSelectionManager().getManagedSelection(context).getSelectedFieldExpression(context);
            } catch (HtSemanticException e) {
                return new Value();
            }
        });

        defineComputedGetterProperty(PROP_SELECTEDCHUNK, (context, model, propertyName) -> {
            try {
                return WyldCard.getInstance().getSelectionManager().getManagedSelection(context).getSelectedChunkExpression(context);
            } catch (HtSemanticException e) {
                return new Value();
            }
        });

        defineComputedGetterProperty(PROP_SELECTEDTEXT, (context, model, propertyName) -> {
            try {
                return WyldCard.getInstance().getSelectionManager().getSelection(context);
            } catch (HtSemanticException e) {
                return new Value();
            }
        });

        addPropertyWillChangeObserver((property, oldValue, newValue) -> {
            switch (property.toLowerCase()) {
                case PROP_LOCKSCREEN:
                    WyldCard.getInstance()
                            .getStackManager().getFocusedStack()
                            .getCurtainManager()
                            .setScreenLocked(new ExecutionContext(), newValue.booleanValue());
                    break;
            }
        });
    }

    @Override
    public void resetProperties() {
        setKnownProperty(new ExecutionContext(), PROP_ITEMDELIMITER, new Value(","));
        setKnownProperty(new ExecutionContext(), PROP_LOCKSCREEN, new Value(false));
        setKnownProperty(new ExecutionContext(), PROP_LOCKMESSAGES, new Value(false));

        WyldCard.getInstance().getCursorManager().setActiveCursor(HyperCardCursor.HAND);
    }
}
