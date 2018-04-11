package com.defano.hypertalk.ast.statements;

import com.defano.hypertalk.ast.preemptions.Preemption;
import com.defano.wyldcard.runtime.context.ExecutionContext;
import com.defano.hypertalk.exception.HtException;
import com.defano.hypertalk.exception.HtSemanticException;

import java.util.ArrayList;
import java.util.Collection;

public class StatementList extends Statement {

    public final ArrayList<Statement> list;

    public StatementList() {
        super(null);
        list = new ArrayList<>();
    }

    public StatementList(Statement s) {
        super(null);
        list = new ArrayList<>();
        prepend(s);
    }

    public StatementList prepend(Statement s) {
        list.add(0, s);
        return this;
    }

    public void onExecute(ExecutionContext context) throws HtException, Preemption {
        for (Statement s : list) {
            if (context.didAbort()) {
                throw new HtSemanticException("Script aborted.");
            }
            s.execute(context);
        }
    }

    @Override
    public Collection<Statement> findStatementsOnLine(int line) {
        ArrayList<Statement> statements = new ArrayList<>();
        for (Statement thisStatement : list) {
            statements.addAll(thisStatement.findStatementsOnLine(line));
        }
        return statements;
    }

}