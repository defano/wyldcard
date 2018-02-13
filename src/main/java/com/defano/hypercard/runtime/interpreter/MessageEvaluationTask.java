package com.defano.hypercard.runtime.interpreter;

import com.defano.hypercard.HyperCard;
import com.defano.hypercard.runtime.context.ExecutionContext;
import com.defano.hypertalk.ast.breakpoints.Breakpoint;
import com.defano.hypertalk.ast.model.specifiers.PartMessageSpecifier;
import com.defano.hypertalk.ast.statements.ExpressionStatement;
import com.defano.hypertalk.ast.statements.StatementList;
import com.defano.hypertalk.exception.HtException;
import com.defano.hypertalk.exception.HtSemanticException;

import java.util.concurrent.Callable;

public class MessageEvaluationTask implements Callable<String> {

    private final String messageText;

    public MessageEvaluationTask(String messageText) {
        this.messageText = messageText;
    }

    @Override
    public String call() throws HtException {
        StatementList statements = Interpreter.blockingCompileScriptlet(messageText).getStatements();

        // All message evaluations share the same context (stack frame); create one only once
        if (ExecutionContext.getContext().getFrame() == null) {
            ExecutionContext.getContext().pushContext();
            ExecutionContext.getContext().pushMe(new PartMessageSpecifier());
        }

        ExecutionContext.getContext().setTarget(HyperCard.getInstance().getActiveStackDisplayedCard().getCardModel().getPartSpecifier());

        try {
            statements.execute();
        } catch (Breakpoint e) {
            HyperCard.getInstance().showErrorDialog(new HtSemanticException("Cannot exit from here."));
        }

        // When the evaluated message is an expression, the result should be displayed in the message box
        if (statements.list.get(0) instanceof ExpressionStatement) {
            return ExecutionContext.getContext().getIt().stringValue();
        } else {
            return null;
        }
    }

}
