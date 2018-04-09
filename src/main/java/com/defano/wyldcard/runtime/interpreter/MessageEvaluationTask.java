package com.defano.wyldcard.runtime.interpreter;

import com.defano.hypertalk.ast.preemptions.Preemption;
import com.defano.wyldcard.WyldCard;
import com.defano.wyldcard.runtime.context.ExecutionContext;
import com.defano.hypertalk.ast.model.specifiers.PartMessageSpecifier;
import com.defano.hypertalk.ast.statements.ExpressionStatement;
import com.defano.hypertalk.ast.statements.StatementList;
import com.defano.hypertalk.exception.HtException;
import com.defano.hypertalk.exception.HtSemanticException;

import java.util.concurrent.Callable;

public class MessageEvaluationTask implements Callable<String> {

    private final ExecutionContext context;
    private final String messageText;

    public MessageEvaluationTask(ExecutionContext staticContext, String messageText) {
        this.context = staticContext;
        this.messageText = messageText;
    }

    @Override
    public String call() throws HtException {
        StatementList statements = Interpreter.blockingCompileScriptlet(messageText).getStatements();

        context.setTarget(WyldCard.getInstance().getActiveStackDisplayedCard().getCardModel().getPartSpecifier(context));

        try {
            statements.execute(context);
        } catch (Preemption e) {
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
