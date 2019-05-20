package com.defano.wyldcard.part.wyldcard;

import com.defano.hypertalk.ast.ASTNode;
import com.defano.hypertalk.ast.model.Script;
import com.defano.hypertalk.ast.model.Value;
import com.defano.hypertalk.ast.model.specifier.PartSpecifier;
import com.defano.hypertalk.ast.preemption.Preemption;
import com.defano.hypertalk.ast.statement.Command;
import com.defano.hypertalk.ast.statement.Statement;
import com.defano.hypertalk.exception.HtException;
import com.defano.hypertalk.exception.HtSemanticException;
import com.defano.hypertalk.exception.HtUncheckedSemanticException;
import com.defano.wyldcard.WyldCard;
import com.defano.wyldcard.cursor.HyperCardCursor;
import com.defano.wyldcard.menu.dispatcher.MenuMessageDispatcher;
import com.defano.wyldcard.message.EvaluatedMessage;
import com.defano.wyldcard.message.LiteralMessage;
import com.defano.wyldcard.message.Message;
import com.defano.wyldcard.part.field.DeferredKeyEventListener;
import com.defano.wyldcard.part.model.PartModel;
import com.defano.wyldcard.pattern.BasicBrushResolver;
import com.defano.wyldcard.runtime.ExecutionContext;
import com.defano.wyldcard.runtime.compiler.CompilationUnit;
import com.defano.wyldcard.runtime.compiler.ScriptCompiler;
import com.defano.wyldcard.runtime.executor.observer.MessageCompletionObserver;
import com.defano.wyldcard.window.layout.ScriptEditor;
import com.google.inject.Singleton;

import javax.annotation.PostConstruct;
import java.awt.event.KeyEvent;

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

        define(PROP_ITEMDELIMITER).asValue(",");
        define(PROP_SCRIPTTEXTFONT).asValue("Monaco");
        define(PROP_SCRIPTTEXTSIZE).asValue(12);
        define(PROP_LOCKMESSAGES).asValue(false);
        define(PROP_TEXTARROWS).asValue(true);

        define(PROP_USERLEVEL).asValue(5);              // TODO: Not implemented
        define(PROP_BLINDTYPING).asValue(true);         // TODO: Not implemented
        define(PROP_POWERKEYS).asValue(true);           // TODO: Not implemented
        define(PROP_ADDRESS).asConstant("127.0.0.1");   // Not supported: Inet4Address can take many seconds to return on some systems

        define(PROP_THEMS).asComputedReadOnlyValue((context, model) -> Value.ofItems(WyldCard.getInstance().getWindowManager().getThemeNames()));

        define(PROP_THEME).asComputedValue()
                .withSetter((context, model, value) -> WyldCard.getInstance().getWindowManager().setTheme(WyldCard.getInstance().getWindowManager().getThemeClassForName(value.toString())))
                .withGetter((context, model) -> new Value(WyldCard.getInstance().getWindowManager().getCurrentThemeName()));

        define(PROP_TEXTFONT).asComputedValue()
                .withSetter((context, model, value) -> WyldCard.getInstance().getFontManager().setSelectedFontFamily(value.toString()))
                .withGetter((context, model) -> new Value(WyldCard.getInstance().getFontManager().getSelectedFontFamily()));

        define(PROP_TEXTSTYLE).asComputedValue()
                .withSetter((context, model, value) -> WyldCard.getInstance().getFontManager().setSelectedFontStyle(value))
                .withGetter((context, model) -> new Value(WyldCard.getInstance().getFontManager().getSelectedFontStyle()));

        define(PROP_TEXTSIZE).asComputedValue()
                .withSetter((context, model, value) -> WyldCard.getInstance().getFontManager().setSelectedFontSize(value.integerValue()))
                .withGetter((context, model) -> new Value(WyldCard.getInstance().getFontManager().getSelectedFontSize()));

        define(PROP_BRUSH).asComputedValue()
                .withSetter((context, model, value) -> WyldCard.getInstance().getPaintManager().setSelectedBrush(BasicBrushResolver.basicBrushOfValue(value)))
                .withGetter((context, model) -> BasicBrushResolver.valueOfBasicBrush(WyldCard.getInstance().getPaintManager().getSelectedBrush()));

        define(PROP_LINESIZE).asComputedValue()
                .withSetter((context, model, value) -> WyldCard.getInstance().getPaintManager().setLineWidth(value.integerValue()))
                .withGetter((context, model) -> new Value(WyldCard.getInstance().getPaintManager().getLineWidth()));

        define(PROP_FILLED).asComputedValue()
                .withSetter((context, model, value) -> WyldCard.getInstance().getPaintManager().setShapesFilled(value.booleanValue()))
                .withGetter((context, model) -> new Value(WyldCard.getInstance().getPaintManager().isShapesFilled()));

        define(PROP_CENTERED).asComputedValue()
                .withSetter((context, model, value) -> WyldCard.getInstance().getPaintManager().setDrawCentered(value.booleanValue()))
                .withGetter((context, model) -> new Value(WyldCard.getInstance().getPaintManager().isDrawCentered()));

        define(PROP_MULTIPLE).asComputedValue()
                .withSetter((context, model, value) -> WyldCard.getInstance().getPaintManager().setDrawMultiple(value.booleanValue()))
                .withGetter((context, model) -> new Value(WyldCard.getInstance().getPaintManager().isDrawMultiple()));

        define(PROP_CURSOR).asComputedValue()
                .withSetter((context, model, value) -> WyldCard.getInstance().getCursorManager().setActiveCursor(value))
                .withGetter((context, model) -> new Value(WyldCard.getInstance().getCursorManager().getActiveCursor().hyperTalkName));

        define(PROP_GRID).asComputedValue()
                .withSetter((context, model, value) -> WyldCard.getInstance().getPaintManager().setGridSpacing(value.booleanValue() ? 8 : 1))
                .withGetter((context, model) -> new Value(WyldCard.getInstance().getPaintManager().getGridSpacing() > 1));

        define(PROP_POLYSIDES).asComputedValue()
                .withSetter((context, model, value) -> WyldCard.getInstance().getPaintManager().setShapeSides(value.integerValue()))
                .withGetter((context, model) -> new Value(WyldCard.getInstance().getPaintManager().getShapeSides()));

        define(PROP_PATTERN).asComputedValue()
                .withSetter((context, model, value) -> {
                    if (value.integerValue() >= 1 && value.integerValue() <= 40) {
                        WyldCard.getInstance().getPaintManager().setFillPattern(value.integerValue() - 1);
                    }
                })
                .withGetter((context, model) -> new Value(WyldCard.getInstance().getPaintManager().getFillPattern() + 1));

        define(PROP_LOCKSCREEN).asComputedValue()
                .withGetter((context, model) -> new Value(WyldCard.getInstance().getStackManager().getFocusedStack().getCurtainManager().isScreenLocked()))
                .withSetter((context, model, value) -> {
                    if (value.booleanValue()) {
                        WyldCard.getInstance().getStackManager().getFocusedStack().getCurtainManager().lockScreen(new ExecutionContext());
                    } else {
                        WyldCard.getInstance().getStackManager().getFocusedStack().getCurtainManager().unlockScreen(new ExecutionContext(), context.getVisualEffect());
                    }
                });
    }

    @PostConstruct
    public void postConstruct() {
        // TODO: @PostConstruct is not implemented yet
        WyldCard.getInstance().getPeriodicMessageManager().addIdleObserver(() -> resetProperties(new ExecutionContext()));
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
    public void receiveMessage(ExecutionContext context, ASTNode initiator, Message message, MessageCompletionObserver onCompletion) {
        processMessage(context, message);

        if (onCompletion != null) {
            onCompletion.onMessagePassed(message, false, null);
        }
    }

    @Override
    public void receiveAndDeferKeyEvent(ExecutionContext context, Message message, KeyEvent e, DeferredKeyEventListener c) {
        // Nothing to do
    }

    @Override
    public Value invokeFunction(ExecutionContext context, ASTNode initiator, Message message) throws HtException {
        // TODO: This would be a good place to hook XFCN-like functionality
        throw new HtException("No such function " + message.getMessageName() + ".");
    }

    @Override
    public void resetProperties(ExecutionContext context) {
        set(context, PROP_ITEMDELIMITER, new Value(","));
        set(context, PROP_LOCKSCREEN, new Value(false));
        set(context, PROP_LOCKMESSAGES, new Value(false));

        WyldCard.getInstance().getCursorManager().setActiveCursor(HyperCardCursor.HAND);
    }

    @Override
    public boolean isTextArrows() {
        return get(new ExecutionContext(), PROP_TEXTARROWS).booleanValue();
    }

    @Override
    public boolean isLockMessages() {
        return get(new ExecutionContext(), PROP_LOCKMESSAGES).booleanValue();
    }

    @Override
    public ScriptEditor editScript(ExecutionContext context) {
        throw new HtUncheckedSemanticException(new HtSemanticException("Cannot edit the script of WyldCard."));
    }

    @Override
    public ScriptEditor editScript(ExecutionContext context, Integer caretPosition) {
        throw new HtUncheckedSemanticException(new HtSemanticException("Cannot edit the script of WyldCard."));
    }

    private void processMessage(ExecutionContext context, Message message) {
        // TODO: This would be a good place to hook XCMD-like functionality

        try {
            EvaluatedMessage evaluatedMessage = message.toEvaluatedMessage(context);

            if (MenuMessageDispatcher.getInstance().test(evaluatedMessage)) {
                MenuMessageDispatcher.getInstance().handleMessage(context, evaluatedMessage);
            } else if (message instanceof LiteralMessage) {
                String messageString = ((LiteralMessage) message).getMessageLiteral();
                Script script = (Script) ScriptCompiler.blockingCompile(CompilationUnit.SCRIPTLET, messageString);
                Statement s = script.getStatements().list.get(0);

                if (s instanceof Command) {
                    s.execute(context);
                }
            }
        } catch (HtException | Preemption e) {
            // Nothing to do
        }
    }
}
