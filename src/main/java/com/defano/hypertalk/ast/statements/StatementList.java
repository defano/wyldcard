package com.defano.hypertalk.ast.statements;

import com.defano.hypertalk.ast.breakpoints.Breakpoint;
import com.defano.hypertalk.exception.HtException;

import java.util.Vector;

public class StatementList {

    public final Vector<Statement> list;

    public StatementList () {
        list = new Vector<>();
    }
    
    public StatementList (Statement s) {
        list = new Vector<>();
        append(s);
    }

    public StatementList append (Statement s) {
        list.add(s);
        return this;
    }

    public void execute() throws HtException, Breakpoint {
        for (Statement s : list) {
            s.execute();
        }
    }
}
