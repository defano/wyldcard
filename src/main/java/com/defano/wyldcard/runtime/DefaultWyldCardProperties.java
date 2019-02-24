package com.defano.wyldcard.runtime;

import com.defano.wyldcard.WyldCard;
import com.defano.wyldcard.cursor.HyperCardCursor;
import com.defano.wyldcard.parts.model.ComputedGetter;
import com.defano.wyldcard.parts.model.WyldCardPropertiesModel;
import com.defano.wyldcard.patterns.BasicBrushResolver;
import com.defano.wyldcard.runtime.context.ExecutionContext;
import com.defano.hypertalk.ast.model.Value;
import com.defano.hypertalk.exception.HtSemanticException;
import com.google.inject.Singleton;

import java.net.Inet4Address;
import java.net.UnknownHostException;

/**
 * A model of global, HyperCard properties. Note that this model is not part of a stack and is therefore never saved.
 * Changes to these properties reset to their default on application startup (and some, like 'itemDelimiter' reset to
 * their default value whenever their are no scripts executing).
 */
@Singleton
public class DefaultWyldCardProperties extends WyldCardPropertiesModel implements WyldCardProperties {

    public DefaultWyldCardProperties() {
        super();

        newProperty(PROP_ITEMDELIMITER, new Value(","), false);
        newProperty(PROP_SELECTEDTEXT, new Value(), true);
        newProperty(PROP_SELECTEDCHUNK, new Value(), true);
        newProperty(PROP_SELECTEDFIELD, new Value(), true);
        newProperty(PROP_SELECTEDLINE, new Value(), true);
        newProperty(PROP_LOCKSCREEN, new Value(false), false);
        newProperty(PROP_CLICKTEXT, new Value(""), true);
        newProperty(PROP_MOUSEH, new Value(0), true);
        newProperty(PROP_MOUSEV, new Value(0), true);
        newProperty(PROP_SCREENRECT, new Value("0,0,0,0"), true);
        newProperty(PROP_CLICKLOC, new Value("0, 0"), true);
        newProperty(PROP_CLICKH, new Value("0"), true);
        newProperty(PROP_CLICKV, new Value("0"), true);
        newProperty(PROP_SOUND, new Value("done"), true);
        newProperty(PROP_SCRIPTTEXTFONT, new Value("Monaco"), false);
        newProperty(PROP_SCRIPTTEXTSIZE, new Value(12), false);
        newProperty(PROP_FOUNDCHUNK, new Value(), true);
        newProperty(PROP_FOUNDFIELD, new Value(), true);
        newProperty(PROP_FOUNDLINE, new Value(), true);
        newProperty(PROP_FOUNDTEXT, new Value(), true);
        newProperty(PROP_LOCKMESSAGES, new Value(true), false);
        newProperty(PROP_TEXTARROWS, new Value(true), false);

        newComputedReadOnlyProperty(PROP_ADDRESS, (context, model, propertyName) -> {
            try {
                return new Value(Inet4Address.getLocalHost().getHostAddress());
            } catch (UnknownHostException e) {
                return new Value();
            }
        });

        newComputedReadOnlyProperty(PROP_SYSTEMVERSION, (context, model, propertyName) -> new Value(System.getProperty("java.version")));

        newComputedReadOnlyProperty(PROP_THEMS, (context, model, propertyName) -> Value.ofItems(WyldCard.getInstance().getWindowManager().getThemeNames()));
        newComputedGetterProperty(PROP_THEME, (context, model, propertyName) -> new Value(WyldCard.getInstance().getWindowManager().getCurrentThemeName()));
        newComputedSetterProperty(PROP_THEME, (context, model, propertyName, value) -> WyldCard.getInstance().getWindowManager().setTheme(WyldCard.getInstance().getWindowManager().getThemeClassForName(value.toString())));

        newComputedSetterProperty(PROP_TEXTFONT, (context, model, propertyName, value) -> WyldCard.getInstance().getFontManager().setSelectedFontFamily(value.toString()));
        newComputedGetterProperty(PROP_TEXTFONT, (context, model, propertyName) -> new Value(WyldCard.getInstance().getFontManager().getSelectedFontFamily()));

        newComputedSetterProperty(PROP_TEXTSTYLE, (context, model, propertyName, value) -> WyldCard.getInstance().getFontManager().setSelectedFontStyle(value));
        newComputedGetterProperty(PROP_TEXTSTYLE, (context, model, propertyName) -> new Value(WyldCard.getInstance().getFontManager().getSelectedFontStyle()));

        newComputedSetterProperty(PROP_TEXTSIZE, (context, model, propertyName, value) -> WyldCard.getInstance().getFontManager().setSelectedFontSize(value.integerValue()));
        newComputedGetterProperty(PROP_TEXTSIZE, (context, model, propertyName) -> new Value(WyldCard.getInstance().getFontManager().getSelectedFontSize()));

        newComputedGetterProperty(PROP_BRUSH, (context, model, propertyName) -> BasicBrushResolver.valueOfBasicBrush(WyldCard.getInstance().getToolsManager().getSelectedBrush()));
        newComputedSetterProperty(PROP_BRUSH, (context, model, propertyName, value) -> WyldCard.getInstance().getToolsManager().setSelectedBrush(BasicBrushResolver.basicBrushOfValue(value)));

        newComputedSetterProperty(PROP_LINESIZE, (context, model, propertyName, value) -> WyldCard.getInstance().getToolsManager().setLineWidth(value.integerValue()));
        newComputedGetterProperty(PROP_LINESIZE, (context, model, propertyName) -> new Value(WyldCard.getInstance().getToolsManager().getLineWidth()));

        newComputedSetterProperty(PROP_FILLED, (context, model, propertyName, value) -> WyldCard.getInstance().getToolsManager().setShapesFilled(value.booleanValue()));
        newComputedGetterProperty(PROP_FILLED, (context, model, propertyName) -> new Value(WyldCard.getInstance().getToolsManager().isShapesFilled()));

        newComputedSetterProperty(PROP_CENTERED, (context, model, propertyName, value) -> WyldCard.getInstance().getToolsManager().setDrawCentered(value.booleanValue()));
        newComputedGetterProperty(PROP_CENTERED, (context, model, propertyName) -> new Value(WyldCard.getInstance().getToolsManager().isDrawCentered()));

        newComputedSetterProperty(PROP_MULTIPLE, (context, model, propertyName, value) -> WyldCard.getInstance().getToolsManager().setDrawMultiple(value.booleanValue()));
        newComputedGetterProperty(PROP_MULTIPLE, (context, model, propertyName) -> new Value(WyldCard.getInstance().getToolsManager().isDrawMultiple()));

        newComputedSetterProperty(PROP_CURSOR, (context, model, propertyName, value) -> WyldCard.getInstance().getCursorManager().setActiveCursor(value));
        newComputedGetterProperty(PROP_CURSOR, (context, model, propertyName) -> new Value (WyldCard.getInstance().getCursorManager().getActiveCursor().hyperTalkName));

        newComputedSetterProperty(PROP_GRID, (context, model, propertyName, value) -> WyldCard.getInstance().getToolsManager().setGridSpacing(value.booleanValue() ? 8 : 1));
        newComputedGetterProperty(PROP_GRID, (context, model, propertyName) -> new Value (WyldCard.getInstance().getToolsManager().getGridSpacing() > 1));

        newComputedSetterProperty(PROP_POLYSIDES, (context, model, propertyName, value) -> WyldCard.getInstance().getToolsManager().setShapeSides(value.integerValue()));
        newComputedGetterProperty(PROP_POLYSIDES, (context, model, propertyName) -> new Value (WyldCard.getInstance().getToolsManager().getShapeSides()));

        newComputedSetterProperty(PROP_PATTERN, (context, model, propertyName, value) -> {
            if (value.integerValue() >= 0 && value.integerValue() < 40) {
                WyldCard.getInstance().getToolsManager().setFillPattern(value.integerValue());
            }
        });
        newComputedGetterProperty(PROP_PATTERN, (context, model, propertyName) -> new Value (WyldCard.getInstance().getToolsManager().getFillPattern()));

        newComputedGetterProperty(PROP_MOUSEH, (context, model, propertyName) -> new Value(WyldCard.getInstance().getMouseManager().getMouseLoc(context).x));
        newComputedGetterProperty(PROP_MOUSEV, (context, model, propertyName) -> new Value(WyldCard.getInstance().getMouseManager().getMouseLoc(context).y));
        newComputedGetterProperty(PROP_SCREENRECT, (context, model, propertyName) -> new Value(WyldCard.getInstance().getWindowManager().getWindowForStack(context, context.getCurrentStack()).getWindow().getGraphicsConfiguration().getBounds()));
        newComputedGetterProperty(PROP_CLICKLOC, (context, model, propertyName) -> new Value(WyldCard.getInstance().getMouseManager().getClickLoc()));
        newComputedGetterProperty(PROP_CLICKH, (context, model, propertyName) -> new Value(WyldCard.getInstance().getMouseManager().getClickLoc().x));
        newComputedGetterProperty(PROP_CLICKV, (context, model, propertyName) -> new Value(WyldCard.getInstance().getMouseManager().getClickLoc().y));
        newComputedGetterProperty(PROP_SOUND, (context, model, propertyName) -> new Value(WyldCard.getInstance().getSoundManager().getSound()));

        newComputedGetterProperty(PROP_SELECTEDLINE, (context, model, propertyName) -> {
            try {
                return WyldCard.getInstance().getSelectionManager().getManagedSelection(context).getSelectedLineExpression(context);
            } catch (HtSemanticException e) {
                return new Value();
            }
        });

        newComputedGetterProperty(PROP_SELECTEDFIELD, (context, model, propertyName) -> {
            try {
                return WyldCard.getInstance().getSelectionManager().getManagedSelection(context).getSelectedFieldExpression(context);
            } catch (HtSemanticException e) {
                return new Value();
            }
        });

        newComputedGetterProperty(PROP_SELECTEDCHUNK, (context, model, propertyName) -> {
            try {
                return WyldCard.getInstance().getSelectionManager().getManagedSelection(context).getSelectedChunkExpression(context);
            } catch (HtSemanticException e) {
                return new Value();
            }
        });

        newComputedGetterProperty(PROP_SELECTEDTEXT, (context, model, propertyName) -> {
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

    @Override
    public boolean isTextArrows() {
        return getKnownProperty(new ExecutionContext(), PROP_TEXTARROWS).booleanValue();
    }

    @Override
    public boolean isLockMessages() {
        return getKnownProperty(new ExecutionContext(), PROP_LOCKMESSAGES).booleanValue();
    }
}
