package com.defano.wyldcard.runtime.interpreter;

import com.defano.wyldcard.WyldCard;
import com.defano.wyldcard.runtime.context.ExecutionContext;
import com.defano.hypertalk.ast.breakpoints.Breakpoint;
import com.defano.hypertalk.ast.model.specifiers.PartMessageSpecifier;
import com.defano.hypertalk.ast.statements.ExpressionStatement;
import com.defano.hypertalk.ast.statements.StatementList;
import com.defano.hypertalk.exception.HtException;
import com.defano.hypertalk.exception.HtSemanticException;

import java.util.concurrent.Callable;

public class MessageEvaluationTask implements Callable<String> {

    private final static ExecutionContext context = new ExecutionContext();
    private final String messageText;

    public MessageEvaluationTask(String messageText) {
        this.messageText = messageText;
    }

    @Override
    public String call() throws HtException {
        StatementList statements = Interpreter.blockingCompileScriptlet(messageText).getStatements();

        // All message evaluations share the same context (stack frame); create one only once
        if (context.getStackFrame() == null) {
            context.pushStackFrame();
            context.pushMe(new PartMessageSpecifier());
        } else {
            // Set the creation time to allow script abort to work correctly
            context.getStackFrame().setCreationTime(System.currentTimeMillis());
        }

        context.setTarget(WyldCard.getInstance().getActiveStackDisplayedCard().getCardModel().getPartSpecifier(context));

        try {
            statements.execute(context);
        } catch (Breakpoint e) {
            WyldCard.getInstance().showErrorDialog(new HtSemanticException("Cannot exit from here."));
        }

        // When the evaluated message is an expression, the result should be displayed in the message box
        if (statements.list.get(0) instanceof ExpressionStatement) {
            return context.getIt().stringValue();
        } else {
            return null;
        }
    }

}
