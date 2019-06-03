package com.defano.hypertalk.ast.statement.command;

import com.defano.hypertalk.ast.expression.Expression;
import com.defano.hypertalk.ast.model.Value;
import com.defano.hypertalk.ast.statement.Command;
import com.defano.hypertalk.exception.HtException;
import com.defano.wyldcard.runtime.ExecutionContext;
import com.defano.wyldcard.window.DialogManager;
import com.google.inject.Inject;
import org.antlr.v4.runtime.ParserRuleContext;

public class AnswerCmd extends Command {

    public final Expression message;
    public final Expression ch1;
    public final Expression ch2;
    public final Expression ch3;
    @Inject
    private DialogManager dialogManager;

    public AnswerCmd(ParserRuleContext context, Expression message, Expression ch1, Expression ch2, Expression ch3) {
        super(context, "answer");

        this.message = message;
        this.ch1 = ch1;
        this.ch2 = ch2;
        this.ch3 = ch3;
    }

    public AnswerCmd(ParserRuleContext context, Expression message, Expression ch1, Expression ch2) {
        this(context, message, ch1, ch2, null);
    }

    public AnswerCmd(ParserRuleContext context, Expression message, Expression ch1) {
        this(context, message, ch1, null, null);
    }

    public AnswerCmd(ParserRuleContext context, Expression message) {
        this(context, message, null, null, null);
    }

    public void onExecute(ExecutionContext context) throws HtException {
        Value selection;

        if (ch1 != null && ch2 != null && ch3 != null) {
            selection = dialogManager.answer(context, message.evaluate(context), ch1.evaluate(context), ch2.evaluate(context), ch3.evaluate(context));
        } else if (ch1 != null && ch2 != null) {
            selection = dialogManager.answer(context, message.evaluate(context), ch1.evaluate(context), ch2.evaluate(context), null);
        } else if (ch1 != null) {
            selection = dialogManager.answer(context, message.evaluate(context), ch1.evaluate(context), null, null);
        } else {
            selection = dialogManager.answer(context, message.evaluate(context), new Value("OK"), null, null);
        }

        context.setIt(selection);
    }
}
