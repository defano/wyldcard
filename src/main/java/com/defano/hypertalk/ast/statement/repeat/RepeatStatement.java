package com.defano.hypertalk.ast.statement.repeat;

import com.defano.hypertalk.ast.model.Value;
import com.defano.hypertalk.ast.preemption.Preemption;
import com.defano.hypertalk.ast.preemption.TerminateIterationPreemption;
import com.defano.hypertalk.ast.preemption.TerminateLoopPreemption;
import com.defano.hypertalk.ast.statement.Statement;
import com.defano.hypertalk.ast.statement.StatementList;
import com.defano.hypertalk.exception.HtException;
import com.defano.hypertalk.exception.HtSemanticException;
import com.defano.wyldcard.runtime.ExecutionContext;
import org.antlr.v4.runtime.ParserRuleContext;

import java.util.ArrayList;
import java.util.Collection;

public class RepeatStatement extends Statement {

    public final RepeatSpecifier range;
    public final StatementList statements;

    public RepeatStatement(ParserRuleContext context, RepeatSpecifier range, StatementList statements) {
        super(context);
        this.range = range;
        this.statements = statements;
    }

    @Override
    public void onExecute(ExecutionContext context) throws HtException, Preemption {
        try {
            if (range instanceof RepeatForever) {
                executeRepeatForever(context);
            } else if (range instanceof RepeatCount) {
                executeRepeatCount(context);
            } else if (range instanceof RepeatDuration) {
                executeRepeatDuration(context);
            } else if (range instanceof RepeatWith) {
                executeRepeatWith(context);
            } else {
                throw new IllegalStateException("Bug! Unknown repeat type.");
            }
        } catch (TerminateLoopPreemption breakpoint) {
            // Nothing to do except stop repeating
        }
    }

    @Override
    public Collection<Statement> findStatementsOnLine(int line) {
        ArrayList<Statement> foundStatements = new ArrayList<>();
        foundStatements.addAll(super.findStatementsOnLine(line));
        foundStatements.addAll(statements.findStatementsOnLine(line));
        return foundStatements;
    }


    private void executeRepeatWith(ExecutionContext context) throws HtException, Preemption {
        RepeatWith with = (RepeatWith) range;
        String symbol = with.symbol;
        RepeatRange withRange = with.range;

        Value fromValue = withRange.from.evaluate(context);
        Value toValue = withRange.to.evaluate(context);

        if (!fromValue.isInteger())
            throw new HtSemanticException("Start of repeat range is not an integer value: '" + fromValue + "'");
        if (!toValue.isInteger())
            throw new HtSemanticException("End of repeat range is not an integer value: '" + toValue + "'");

        int from = fromValue.integerValue();
        int to = toValue.integerValue();

        if (withRange.polarity == RepeatRange.POLARITY_UPTO) {

            if (from > to)
                throw new HtSemanticException("Start of repeat range is greater than end: " + from + " > " + to);

            for (int index = from; index <= to; index++) {
                context.setVariable(symbol, new Value(index));
                iterate(context);
            }
        }

        else if (withRange.polarity == RepeatRange.POLARITY_DOWNTO) {
            if (to > from)
                throw new HtSemanticException("End of repeat range is less than start: " + to + " > " + from);

            for (int index = from; index >= to; index--) {
                context.setVariable(symbol, new Value(index));
                iterate(context);
            }
        }
    }

    private void executeRepeatDuration(ExecutionContext context) throws HtException, Preemption {
        RepeatDuration duration = (RepeatDuration) range;

        // While loop
        if (duration.polarity == RepeatDuration.POLARITY_WHILE) {
            while (duration.condition.evaluate(context).booleanValueOrError(new HtSemanticException("Repeat condition expects a true or false value."))) {
                iterate(context);
            }
        }

        // Until loop
        if (duration.polarity == RepeatDuration.POLARITY_UNTIL) {
            while (!duration.condition.evaluate(context).booleanValueOrError(new HtSemanticException("Repeat condition expects a true or false value."))) {
                iterate(context);
            }
        }
    }

    private void executeRepeatCount(ExecutionContext context) throws HtException, Preemption {
        RepeatCount count = (RepeatCount) range;
        Value countValue = count.count.evaluate(context);

        if (!countValue.isNatural())
            throw new HtSemanticException("Repeat range must be a natural number, got '" + countValue + "' instead.");

        int countIndex = countValue.integerValue();
        while (countIndex-- > 0) {
            iterate(context);
        }
    }

    @SuppressWarnings("InfiniteLoopStatement")
    private void executeRepeatForever(ExecutionContext context) throws HtException, Preemption {
        while (!context.didAbort()) {
            iterate(context);
        }
    }

    private void iterate(ExecutionContext context) throws HtException, Preemption {

        try {
            statements.execute(context);
        } catch (TerminateIterationPreemption e) {
            // Nothing to do; keep repeating
        }
    }
}
