package com.defano.wyldcard.runtime;

import com.defano.wyldcard.WyldCard;
import com.defano.wyldcard.cursor.HyperCardCursor;
import com.defano.wyldcard.parts.model.WyldCardPropertiesModel;
import com.defano.wyldcard.patterns.BasicBrushResolver;
import com.defano.wyldcard.runtime.context.ExecutionContext;
import com.defano.hypertalk.ast.model.Value;
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
        newProperty(PROP_LOCKSCREEN, new Value(false), false);
        newProperty(PROP_SCRIPTTEXTFONT, new Value("Monaco"), false);
        newProperty(PROP_SCRIPTTEXTSIZE, new Value(12), false);
        newProperty(PROP_LOCKMESSAGES, new Value(true), false);
        newProperty(PROP_TEXTARROWS, new Value(true), false);
        newProperty(PROP_USERLEVEL, new Value(5), false);       // Has no effect

        newComputedReadOnlyProperty(PROP_ADDRESS, (context, model, propertyName) -> {
            try {
                return new Value(Inet4Address.getLocalHost().getHostAddress());
            } catch (UnknownHostException e) {
                return new Value();
            }
        });

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

        newComputedSetterProperty(PROP_MULTIPLE, (context, model, propg4ertyName, value) -> WyldCard.getInstance().getToolsManager().setDrawMultiple(value.booleanValue()));
        newComputedGetterProperty(PROP_MULTIPLE, (context, model, propertyName) -> new Value(WyldCard.getInstance().getToolsManager().isDrawMultiple()));

        newComputedSetterProperty(PROP_CURSOR, (context, model, propertyName, value) -> WyldCard.getInstance().getCursorManager().setActiveCursor(value));
        newComputedGetterProperty(PROP_CURSOR, (context, model, propertyName) -> new Value (WyldCard.getInstance().getCursorManager().getActiveCursor().hyperTalkName));

        newComputedSetterProperty(PROP_GRID, (context, model, propertyName, value) -> WyldCard.getInstance().getToolsManager().setGridSpacing(value.booleanValue() ? 8 : 1));
        newComputedGetterProperty(PROP_GRID, (context, model, propertyName) -> new Value (WyldCard.getInstance().getToolsManager().getGridSpacing() > 1));

        newComputedSetterProperty(PROP_POLYSIDES, (context, model, propertyName, value) -> WyldCard.getInstance().getToolsManager().setShapeSides(value.integerValue()));
        newComputedGetterProperty(PROP_POLYSIDES, (context, model, propertyName) -> new Value (WyldCard.getInstance().getToolsManager().getShapeSides()));

        newComputedSetterProperty(PROP_PATTERN, (context, model, propertyName, value) -> {
            if (value.integerValue() >= 1 && value.integerValue() <= 40) {
                WyldCard.getInstance().getToolsManager().setFillPattern(value.integerValue() - 1);
            }
        });
        newComputedGetterProperty(PROP_PATTERN, (context, model, propertyName) -> new Value (WyldCard.getInstance().getToolsManager().getFillPattern() + 1));

        addPropertyWillChangeObserver((context, property, oldValue, newValue) -> {
            if (PROP_LOCKSCREEN.equals(property.toLowerCase())) {
                if (newValue.booleanValue()) {
                    WyldCard.getInstance().getStackManager().getFocusedStack().getCurtainManager().lockScreen(new ExecutionContext());
                } else {
                    WyldCard.getInstance().getStackManager().getFocusedStack().getCurtainManager().unlockScreen(new ExecutionContext(), context.getVisualEffect());
                }
            }
        });
    }

    @Override
    public void resetProperties(ExecutionContext context) {
        setKnownProperty(context, PROP_ITEMDELIMITER, new Value(","));
        setKnownProperty(context, PROP_LOCKSCREEN, new Value(false));
        setKnownProperty(context, PROP_LOCKMESSAGES, new Value(false));

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
