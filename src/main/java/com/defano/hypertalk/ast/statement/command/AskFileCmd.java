package com.defano.hypertalk.ast.statement.command;

import com.defano.hypertalk.ast.expression.Expression;
import com.defano.hypertalk.ast.model.Value;
import com.defano.hypertalk.ast.statement.Command;
import com.defano.hypertalk.exception.HtException;
import com.defano.wyldcard.runtime.ExecutionContext;
import com.defano.wyldcard.window.DialogManager;
import com.defano.wyldcard.window.DialogResponse;
import com.google.inject.Inject;
import org.antlr.v4.runtime.ParserRuleContext;

public class AskFileCmd extends Command {

    @Inject
    private DialogManager dialogManager;

    private final Expression promptExpression;
    private final Expression fileExpression;

    public AskFileCmd(ParserRuleContext context, Expression prompt) {
        this(context, prompt, null);
    }

    public AskFileCmd(ParserRuleContext context, Expression prompt, Expression file) {
        super(context, "ask");
        this.promptExpression = prompt;
        this.fileExpression = file;
    }

    @Override
    protected void onExecute(ExecutionContext context) throws HtException {
        Value prompt = promptExpression.evaluate(context);
        Value file = fileExpression != null ? fileExpression.evaluate(context) : null;

        DialogResponse response = dialogManager.askFile(context, prompt, file);

        context.setResult(response.getButtonResponse());
        if (response.getFieldResponse() != null) {
            context.setIt(response.getFieldResponse());
        }
    }
}
