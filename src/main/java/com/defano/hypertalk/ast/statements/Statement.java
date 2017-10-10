package com.defano.hypertalk.ast.statements;

import com.defano.hypertalk.ast.ASTNode;
import com.defano.hypertalk.ast.breakpoints.Breakpoint;
import com.defano.hypertalk.exception.HtException;
import org.antlr.v4.runtime.ParserRuleContext;

public abstract class Statement extends ASTNode {

    public Statement(ParserRuleContext context) {
        super(context);
    }

    protected abstract void onExecute() throws HtException, Breakpoint;

    public void execute() throws HtException, Breakpoint {
        try {
            onExecute();
        } catch (HtException e) {
            rethrowContextualizedException(e);
        }
    }
}
