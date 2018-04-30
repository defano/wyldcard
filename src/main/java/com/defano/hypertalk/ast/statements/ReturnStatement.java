package com.defano.hypertalk.ast.statements;

import com.defano.hypertalk.ast.model.Value;
import com.defano.hypertalk.ast.preemptions.TerminateHandlerPreemption;
import com.defano.wyldcard.runtime.context.ExecutionContext;
import com.defano.hypertalk.ast.expressions.Expression;
import com.defano.hypertalk.ast.expressions.LiteralExp;
import com.defano.hypertalk.exception.HtException;
import org.antlr.v4.runtime.ParserRuleContext;

public class ReturnStatement extends Statement {

    public final Expression returnValue;
    
    public ReturnStatement(ParserRuleContext context) {
        super(context);
        this.returnValue = new LiteralExp(null, "");
    }
    
    public ReturnStatement(ParserRuleContext context, Expression returnValue) {
        super(context);
        this.returnValue = returnValue;
    }

    public void onExecute(ExecutionContext context) throws HtException, TerminateHandlerPreemption {
        Value evaluatedReturnValue = returnValue.evaluate(context);
        context.getStackFrame().setReturnValue(evaluatedReturnValue);
        context.setResult(evaluatedReturnValue);
        throw new TerminateHandlerPreemption(null);
    }
}
