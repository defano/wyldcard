package com.defano.hypertalk.ast.statements;

import com.defano.hypertalk.ast.breakpoints.Breakpoint;
import com.defano.hypertalk.exception.HtException;
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
        append(s);
    }

    public StatementList append (Statement s) {
        list.add(0, s);
        return this;
    }

    public void onExecute() throws HtException, Breakpoint {
        for (Statement s : list) {
            s.execute();
        }
    }
}
