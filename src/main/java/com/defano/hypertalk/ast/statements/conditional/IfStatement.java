package com.defano.hypertalk.ast.statements.conditional;

import com.defano.hypertalk.ast.preemptions.Preemption;
import com.defano.hypertalk.ast.expressions.Expression;
import com.defano.hypertalk.ast.statements.Statement;
import com.defano.hypertalk.exception.HtException;
import com.defano.hypertalk.exception.HtSemanticException;
import com.defano.wyldcard.runtime.context.ExecutionContext;
import org.antlr.v4.runtime.ParserRuleContext;

import java.util.ArrayList;
import java.util.Collection;

public class IfStatement extends Statement {

    public final Expression condition;
    public final ThenElseBlock then;
    
    public IfStatement(ParserRuleContext context, Expression condition, ThenElseBlock then) {
        super(context);
        this.condition = condition;
        this.then = then;
    }

    @Override
    public void onExecute(ExecutionContext context) throws HtException, Preemption {
        if (condition.evaluate(context).booleanValueOrError(new HtSemanticException("Condition expects a true or false value."))) {
            then.thenBranch.execute(context);
        } else if (then.elseBranch != null) {
            then.elseBranch.execute(context);
        }
    }

    @Override
    public Collection<Statement> findStatementsOnLine(int line) {
        ArrayList<Statement> foundStatements = new ArrayList<>();

        foundStatements.addAll(super.findStatementsOnLine(line));
        foundStatements.addAll(then.thenBranch.findStatementsOnLine(line));

        if (then.elseBranch != null) {
            foundStatements.addAll(then.elseBranch.findStatementsOnLine(line));
        }

        return foundStatements;
    }
}
