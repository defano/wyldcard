package com.defano.hypertalk.ast.statements;

import com.defano.wyldcard.runtime.context.ExecutionContext;
import com.defano.hypertalk.ast.breakpoints.Breakpoint;
import com.defano.hypertalk.exception.HtException;
import com.defano.hypertalk.exception.HtSemanticException;
import org.antlr.v4.runtime.ParserRuleContext;

import java.util.ArrayList;

public class StatementList extends Statement {

    public final ArrayList<Statement> list;

    public StatementList (ParserRuleContext context) {
        super(context);
        list = new ArrayList<>();
    }
    
    public StatementList (ParserRuleContext context, Statement s) {
        super(context);
        list = new ArrayList<>();
        prepend(s);
    }

    public StatementList prepend(Statement s) {
        list.add(0, s);
        return this;
    }

    public void onExecute() throws HtException, Breakpoint {
        for (Statement s : list) {
            if (ExecutionContext.getContext().didAbort()) {
                throw new HtSemanticException("Script aborted.");
            }
            s.execute();
        }
    }
}
