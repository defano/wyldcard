package com.defano.hypertalk.ast.statement.command;

import com.defano.hypertalk.ast.expression.Expression;
import com.defano.hypertalk.ast.expression.LiteralExp;
import com.defano.hypertalk.ast.preemption.Preemption;
import com.defano.hypertalk.ast.statement.Command;
import com.defano.hypertalk.exception.HtException;
import com.defano.wyldcard.runtime.ExecutionContext;
import com.defano.wyldcard.util.Hashable;
import com.defano.wyldcard.window.DialogManager;
import com.defano.wyldcard.window.DialogResponse;
import com.google.inject.Inject;
import org.antlr.v4.runtime.ParserRuleContext;

public class AskPasswordCmd extends Command implements Hashable {

    private final boolean isClear;
    private final Expression promptExpr;
    private final Expression passwordExpr;

    @Inject
    private DialogManager dialogManager;

    public AskPasswordCmd(ParserRuleContext context, boolean isClear, Expression promptExpr) {
        this(context, isClear, promptExpr, new LiteralExp(null));
    }

    public AskPasswordCmd(ParserRuleContext context, boolean isClear, Expression promptExpr, Expression passwordExpr) {
        super(context, "ask");
        this.isClear = isClear;
        this.promptExpr = promptExpr;
        this.passwordExpr = passwordExpr;
    }

    @Override
    protected void onExecute(ExecutionContext context) throws HtException, Preemption {
        DialogResponse response = dialogManager.askPassword(context, promptExpr.evaluate(context), passwordExpr.evaluate(context), !isClear);

        context.setResult(response.getButtonResponse());
        context.setIt(response.getFieldResponse());
    }
}
