package com.defano.wyldcard.parts.wyldcard;

import com.defano.hypertalk.ast.model.Script;
import com.defano.hypertalk.ast.model.Value;
import com.defano.hypertalk.ast.model.specifiers.PartSpecifier;
import com.defano.hypertalk.ast.preemptions.Preemption;
import com.defano.hypertalk.ast.statements.Command;
import com.defano.hypertalk.ast.statements.Statement;
import com.defano.hypertalk.exception.HtException;
import com.defano.hypertalk.exception.HtSemanticException;
import com.defano.hypertalk.exception.HtUncheckedSemanticException;
import com.defano.wyldcard.WyldCard;
import com.defano.wyldcard.cursor.HyperCardCursor;
import com.defano.wyldcard.message.Message;
import com.defano.wyldcard.parts.DeferredKeyEventComponent;
import com.defano.wyldcard.parts.model.PartModel;
import com.defano.wyldcard.patterns.BasicBrushResolver;
import com.defano.wyldcard.runtime.compiler.CompilationUnit;
import com.defano.wyldcard.runtime.compiler.Compiler;
import com.defano.wyldcard.runtime.compiler.MessageCompletionObserver;
import com.defano.wyldcard.runtime.context.ExecutionContext;
import com.defano.wyldcard.window.layouts.ScriptEditor;
import com.google.inject.Singleton;

import java.awt.event.KeyEvent;
import java.net.Inet4Address;
import java.net.UnknownHostException;

/**
 * This object represents the part known as 'HyperCard' (also addressable as 'WyldCard'); it provides a set of system-
 * wide HyperTalk addressable properties and acts as a message sink for messages that are not trapped by other parts in
 * the messaging passing hierarchy.
 */
@Singleton
public class WyldCardPart extends PartModel implements WyldCardProperties {

    public WyldCardPart() {
        super(null, null, null);
        super.clear();

        newProperty(PROP_ITEMDELIMITER, new Value(","), false);
        newProperty(PROP_LOCKSCREEN, new Value(false), false);
        newProperty(PROP_SCRIPTTEXTFONT, new Value("Monaco"), false);
        newProperty(PROP_SCRIPTTEXTSIZE, new Value(12), false);
        newProperty(PROP_LOCKMESSAGES, new Value(false), false);
        newProperty(PROP_TEXTARROWS, new Value(true), false);

        newProperty(PROP_USERLEVEL, new Value(5), false);       // TODO: Not implemented
        newProperty(PROP_BLINDTYPING, new Value(true), false);  // TODO: Not implemented
        newProperty(PROP_POWERKEYS, new Value(true), false);    // TODO: Not implemented

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
    public void relinkParentPartModel(PartModel parentPartModel) {
        // Nothing to do
    }

    @Override
    public Script getScript(ExecutionContext context) {
        throw new IllegalStateException("Bug! WyldCard does not have a script.");
    }

    @Override
    public PartSpecifier getMe(ExecutionContext context) {
        throw new IllegalStateException("Bug! WyldCard cannot refer to itself.");
    }

    @Override
    public void receiveMessage(ExecutionContext context, Message message) {
        receiveMessage(context, message, null);
    }

    @Override
    public void receiveMessage(ExecutionContext context, Message message, MessageCompletionObserver onCompletion) {
        try {
            processMessage(context, message);
        } catch (HtException e) {
            // Nothing to do
        }

        if (onCompletion != null) {
            onCompletion.onMessagePassed(message, false, null);
        }
    }

    @Override
    public void receiveAndDeferKeyEvent(ExecutionContext context, Message message, KeyEvent e, DeferredKeyEventComponent c) {
        // Nothing to do
    }

    @Override
    public Value invokeFunction(ExecutionContext context, Message message) throws HtException {
        // TODO: This would be a good place to hook XFCN-like functionality

        throw new HtException("No such function " + message.getMessageName(context) + ".");
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

    @Override
    public ScriptEditor editScript(ExecutionContext context) {
        throw new HtUncheckedSemanticException(new HtSemanticException("Cannot edit the script of WyldCard."));
    }

    @Override
    public ScriptEditor editScript(ExecutionContext context, Integer caretPosition) {
        throw new HtUncheckedSemanticException(new HtSemanticException("Cannot edit the script of WyldCard."));
    }

    @Override
    public void editProperties(ExecutionContext context) {
        throw new HtUncheckedSemanticException(new HtSemanticException("Cannot edit the properties of WyldCard."));
    }

    private void processMessage(ExecutionContext context, Message message) throws HtException {
        // TODO: This would be a good place to hook XCMD-like functionality

        String messageString = message.toMessageString(context);
        try {
            Script script = (Script) Compiler.blockingCompile(CompilationUnit.SCRIPTLET, messageString);
            Statement s = script.getStatements().list.get(0);

            if (s instanceof Command) {
                s.execute(context);
            }
        } catch (HtException | Preemption e) {
            // Nothing to do
        }
    }
}
