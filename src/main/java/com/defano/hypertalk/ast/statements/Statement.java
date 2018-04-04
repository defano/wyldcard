package com.defano.hypertalk.ast.statements;

import com.defano.hypertalk.ast.ASTNode;
import com.defano.hypertalk.ast.breakpoints.Breakpoint;
import com.defano.hypertalk.exception.HtException;
import com.defano.wyldcard.runtime.context.ExecutionContext;
import org.antlr.v4.runtime.ParserRuleContext;

public abstract class Statement extends ASTNode {

    public Statement(ParserRuleContext context) {
        super(context);
    }

    protected abstract void onExecute(ExecutionContext context) throws HtException, Breakpoint;

    public void execute(ExecutionContext context) throws HtException, Breakpoint {
        try {
            onExecute(context);
        } catch (HtException e) {
            rethrowContextualizedException(context, e);
        }
    }
}
