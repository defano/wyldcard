package com.defano.hypertalk.ast.statement.command;

import com.defano.hypertalk.ast.expression.Expression;
import com.defano.hypertalk.ast.expression.LiteralExp;
import com.defano.hypertalk.ast.model.Value;
import com.defano.hypertalk.ast.statement.Command;
import com.defano.hypertalk.exception.HtException;
import com.defano.wyldcard.runtime.ExecutionContext;
import com.defano.wyldcard.window.DialogManager;
import com.defano.wyldcard.window.DialogResponse;
import com.google.inject.Inject;
import org.antlr.v4.runtime.ParserRuleContext;

public class AskCmd extends Command {

    @Inject
    private DialogManager dialogManager;

    private final Expression question;
    private final Expression suggestion;
    
    public AskCmd(ParserRuleContext context, Expression question, Expression suggestion) {
        super(context, "ask");

        this.question = question;
        this.suggestion = suggestion;
    }
    
    public AskCmd(ParserRuleContext context,  Expression question) {
        super(context, "ask");

        this.question = question;
        this.suggestion = new LiteralExp(context, "");
    }
    
    public void onExecute(ExecutionContext context) throws HtException {
        DialogResponse response;

        if (suggestion != null) {
            response = dialogManager.ask(context, question.evaluate(context), suggestion.evaluate(context));
        } else {
            response = dialogManager.ask(context, question.evaluate(context), new Value());
        }

        context.setIt(response.getFieldResponse());
        context.setResult(response.getButtonResponse());
    }
}
