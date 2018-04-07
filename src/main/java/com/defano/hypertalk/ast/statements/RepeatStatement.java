package com.defano.hypertalk.ast.statements;

import com.defano.wyldcard.runtime.context.ExecutionContext;
import com.defano.hypertalk.ast.breakpoints.Breakpoint;
import com.defano.hypertalk.ast.breakpoints.TerminateIterationBreakpoint;
import com.defano.hypertalk.ast.breakpoints.TerminateLoopBreakpoint;
import com.defano.hypertalk.ast.model.Value;
import com.defano.hypertalk.ast.statements.loop.*;
import com.defano.hypertalk.exception.HtException;
import com.defano.hypertalk.exception.HtSemanticException;
import org.antlr.v4.runtime.ParserRuleContext;

import javax.swing.*;
import java.lang.reflect.InvocationTargetException;
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
    @SuppressWarnings("InfiniteLoopStatement")
    public void onExecute(ExecutionContext context) throws HtException, Breakpoint {
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
        } catch (TerminateLoopBreakpoint breakpoint) {
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


    private void executeRepeatWith(ExecutionContext context) throws HtException, Breakpoint {
        RepeatWith with = (RepeatWith) range;
        String symbol = with.symbol;
        RepeatRange range = with.range;

        Value fromValue = range.from.evaluate(context);
        Value toValue = range.to.evaluate(context);

        if (!fromValue.isInteger())
            throw new HtSemanticException("Start of repeat range is not an integer value: '" + fromValue + "'");
        if (!toValue.isInteger())
            throw new HtSemanticException("End of repeat range is not an integer value: '" + toValue + "'");

        int from = fromValue.integerValue();
        int to = toValue.integerValue();

        if (range.polarity == RepeatRange.POLARITY_UPTO) {

            if (from > to)
                throw new HtSemanticException("Start of repeat range is greater than end: " + from + " > " + to);

            for (int index = from; index <= to; index++) {
                context.setVariable(symbol, new Value(index));
                iterate(context);
            }
        }

        else if (range.polarity == RepeatRange.POLARITY_DOWNTO) {
            if (to > from)
                throw new HtSemanticException("End of repeat range is less than start: " + to + " > " + from);

            for (int index = from; index >= to; index--) {
                context.setVariable(symbol, new Value(index));
                iterate(context);
            }
        }
    }

    private void executeRepeatDuration(ExecutionContext context) throws HtException, Breakpoint {
        RepeatDuration duration = (RepeatDuration) range;

        // While loop
        if (duration.polarity == RepeatDuration.POLARITY_WHILE) {
            while (duration.condition.evaluate(context).checkedBooleanValue()) {
                iterate(context);
            }
        }

        // Until loop
        if (duration.polarity == RepeatDuration.POLARITY_UNTIL) {
            while (!duration.condition.evaluate(context).checkedBooleanValue()) {
                iterate(context);
            }
        }
    }

    private void executeRepeatCount(ExecutionContext context) throws HtException, Breakpoint {
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
    private void executeRepeatForever(ExecutionContext context) throws HtException, Breakpoint {
        while (true) {
            iterate(context);
        }
    }

    private void iterate(ExecutionContext context) throws HtException, Breakpoint {

        try {
            statements.execute(context);
            rest(context);
        } catch (TerminateIterationBreakpoint e) {
            // Nothing to do; keep repeating
        }
    }

    private void rest(ExecutionContext context) throws HtException {

        if (context.didAbort()) {
            throw new HtSemanticException("Script aborted.");
        } else {
            try {
                // Flush the Swing UI event queue
                SwingUtilities.invokeAndWait(() -> {});
            } catch (InterruptedException ie) {
                Thread.currentThread().interrupt();
            } catch (InvocationTargetException e) {
                // Nothing to do
            }
        }
    }
}
