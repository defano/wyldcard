package com.defano.hypertalk.ast.statements;

import com.defano.wyldcard.runtime.context.ExecutionContext;
import com.defano.hypertalk.ast.breakpoints.TerminateHandlerBreakpoint;
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

    public void onExecute() throws HtException, TerminateHandlerBreakpoint {
        ExecutionContext.getContext().setReturnValue(returnValue.evaluate());
        throw new TerminateHandlerBreakpoint(null);
    }
}
