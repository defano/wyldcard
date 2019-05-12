package com.defano.hypertalk.ast.statement.command;

import com.defano.hypertalk.ast.model.specifier.PartSpecifier;
import com.defano.wyldcard.runtime.ExecutionContext;
import com.defano.wyldcard.runtime.executor.ScriptExecutor;
import com.defano.hypertalk.ast.expression.Expression;
import com.defano.hypertalk.ast.statement.Command;
import com.defano.hypertalk.exception.HtException;
import org.antlr.v4.runtime.ParserRuleContext;

public class DoCmd extends Command {

    public final Expression script;
    
    public DoCmd(ParserRuleContext context, Expression script) {
        super(context, "do");
        this.script = script;
    }
    
    public void onExecute(ExecutionContext context) throws HtException {
        PartSpecifier target = context.getCurrentStack().getDisplayedCard().getPartModel().getPartSpecifier(context);
        ScriptExecutor.asyncExecuteString(context, target, script.evaluate(context).toString()).checkedGet();
    }
}
