package com.defano.wyldcard.parts.hypercard;

import com.defano.hypertalk.ast.model.Script;
import com.defano.hypertalk.ast.model.Value;
import com.defano.hypertalk.ast.model.specifiers.PartSpecifier;
import com.defano.hypertalk.ast.preemptions.Preemption;
import com.defano.hypertalk.ast.statements.Command;
import com.defano.hypertalk.ast.statements.Statement;
import com.defano.hypertalk.exception.HtException;
import com.defano.wyldcard.message.Message;
import com.defano.wyldcard.parts.DeferredKeyEventComponent;
import com.defano.wyldcard.parts.model.PartModel;
import com.defano.wyldcard.runtime.compiler.CompilationUnit;
import com.defano.wyldcard.runtime.compiler.Compiler;
import com.defano.wyldcard.runtime.compiler.MessageCompletionObserver;
import com.defano.wyldcard.runtime.context.ExecutionContext;

import java.awt.event.KeyEvent;

public class HyperCardPart extends PartModel {

    private final static HyperCardPart instance = new HyperCardPart();

    private HyperCardPart() {
        super(null, null, null);
    }

    public static HyperCardPart getInstance() {
        return instance;
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
        throw new HtException("No such function " + message.getMessageName(context) + ".");
    }

    private void processMessage(ExecutionContext context, Message message) throws HtException {
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
