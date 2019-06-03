package com.defano.hypertalk.ast.statement.command;

import com.defano.hypertalk.ast.expression.Expression;
import com.defano.hypertalk.ast.model.Value;
import com.defano.hypertalk.ast.statement.Command;
import com.defano.hypertalk.exception.HtException;
import com.defano.wyldcard.runtime.ExecutionContext;
import com.defano.wyldcard.window.DialogManager;
import com.google.inject.Inject;
import org.antlr.v4.runtime.ParserRuleContext;

public class AnswerFileCmd extends Command {

    @Inject
    private DialogManager dialogManager;

    private final Expression promptExpr;
    private final Expression filterExpr;

    public AnswerFileCmd(ParserRuleContext context, Expression promptExpr) {
        this(context, promptExpr, null);
    }

    public AnswerFileCmd(ParserRuleContext context, Expression promptExpr, Expression filterExpr) {
        super(context, "answer");
        this.promptExpr = promptExpr;
        this.filterExpr = filterExpr;
    }

    @Override
    protected void onExecute(ExecutionContext context) throws HtException {
        Value promptString = promptExpr.evaluate(context);
        Value fileFilter = filterExpr == null ? null : filterExpr.evaluate(context);
        Value selection = dialogManager.answerFile(context, promptString, fileFilter);

        if (selection != null) {
            context.setIt(selection);
            context.setResult(new Value());
        } else {
            context.setIt(new Value());
            context.setResult(new Value("Cancel"));
        }
    }
}
