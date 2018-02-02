package com.defano.hypertalk.ast.statements;

import com.defano.hypercard.runtime.context.ExecutionContext;
import com.defano.hypercard.awt.KeyboardManager;
import com.defano.hypertalk.ast.breakpoints.Breakpoint;
import com.defano.hypertalk.ast.breakpoints.TerminateIterationBreakpoint;
import com.defano.hypertalk.ast.breakpoints.TerminateLoopBreakpoint;
import com.defano.hypertalk.ast.model.Value;
import com.defano.hypertalk.exception.HtException;
import com.defano.hypertalk.ast.statements.loop.RepeatCount;
import com.defano.hypertalk.ast.statements.loop.RepeatDuration;
import com.defano.hypertalk.ast.statements.loop.RepeatForever;
import com.defano.hypertalk.ast.statements.loop.RepeatRange;
import com.defano.hypertalk.ast.statements.loop.RepeatSpecifier;
import com.defano.hypertalk.ast.statements.loop.RepeatWith;
import com.defano.hypertalk.exception.HtSemanticException;
import org.antlr.v4.runtime.ParserRuleContext;

import javax.swing.*;
import java.lang.reflect.InvocationTargetException;

public class RepeatStatement extends Statement {

    public final RepeatSpecifier range;
    public final StatementList statements;

    public RepeatStatement(ParserRuleContext context, RepeatSpecifier range, StatementList statements) {
        super(context);
        this.range = range;
        this.statements = statements;
    }

    @SuppressWarnings("InfiniteLoopStatement")
    public void onExecute() throws HtException, Breakpoint {
        try {
            if (range instanceof RepeatForever) {
                executeRepeatForever();
            } else if (range instanceof RepeatCount) {
                executeRepeatCount();
            } else if (range instanceof RepeatDuration) {
                executeRepeatDuration();
            } else if (range instanceof RepeatWith) {
                executeRepeatWith();
            } else {
                throw new IllegalStateException("Bug! Unknown repeat type.");
            }
        } catch (TerminateLoopBreakpoint breakpoint) {
            // Nothing to do except stop repeating
        }
    }

    private void executeRepeatWith() throws HtException, Breakpoint {
        RepeatWith with = (RepeatWith) range;
        String symbol = with.symbol;
        RepeatRange range = with.range;

        Value fromValue = range.from.evaluate();
        Value toValue = range.to.evaluate();

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
                ExecutionContext.getContext().setVariable(symbol, new Value(index));
                iterate();
            }
        }

        else if (range.polarity == RepeatRange.POLARITY_DOWNTO) {
            if (to > from)
                throw new HtSemanticException("End of repeat range is less than start: " + to + " > " + from);

            for (int index = from; index >= to; index--) {
                ExecutionContext.getContext().setVariable(symbol, new Value(index));
                iterate();
            }
        }
    }

    private void executeRepeatDuration() throws HtException, Breakpoint {
        RepeatDuration duration = (RepeatDuration) range;

        // While loop
        if (duration.polarity == RepeatDuration.POLARITY_WHILE) {
            while (duration.condition.evaluate().booleanValue()) {
                iterate();
            }
        }

        // Until loop
        if (duration.polarity == RepeatDuration.POLARITY_UNTIL) {
            while (!duration.condition.evaluate().booleanValue()) {
                iterate();
            }
        }
    }

    private void executeRepeatCount() throws HtException, Breakpoint {
        RepeatCount count = (RepeatCount) range;
        Value countValue = count.count.evaluate();

        if (!countValue.isNatural())
            throw new HtSemanticException("Repeat range must be a natural number, got '" + countValue + "' instead.");

        int countIndex = countValue.integerValue();
        while (countIndex-- > 0) {
            iterate();
        }
    }

    @SuppressWarnings("InfiniteLoopStatement")
    private void executeRepeatForever() throws HtException, Breakpoint {
        while (true) {
            iterate();
        }
    }

    private void iterate() throws HtException, Breakpoint {

        try {
            statements.execute();
            rest();
        } catch (TerminateIterationBreakpoint e) {
            // Nothing to do; keep repeating
        }
    }

    private void rest() throws HtException {

        if (KeyboardManager.getInstance().isBreakSequence()) {
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
