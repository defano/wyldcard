/**
 * StatRepeat.java
 *
 * @author matt.defano@gmail.com
 * <p>
 * Encapsulation of a repeat statement
 */

package hypertalk.ast.statements;

import hypercard.context.GlobalContext;
import hypercard.gui.util.ModifierKeyListener;
import hypertalk.ast.common.Value;
import hypertalk.ast.constructs.RepeatCount;
import hypertalk.ast.constructs.RepeatDuration;
import hypertalk.ast.constructs.RepeatForever;
import hypertalk.ast.constructs.RepeatRange;
import hypertalk.ast.constructs.RepeatSpecifier;
import hypertalk.ast.constructs.RepeatWith;
import hypertalk.exception.HtException;
import hypertalk.exception.HtSemanticException;

public class StatRepeat extends Statement {

    public final RepeatSpecifier range;
    public final StatementList statements;

    public StatRepeat(RepeatSpecifier range, StatementList statements) {
        this.range = range;
        this.statements = statements;
    }

    public void execute() throws HtException {
        if (range instanceof RepeatForever) {
            while (true) {
                statements.execute();
                rest();
            }
        } else if (range instanceof RepeatCount) {
            RepeatCount count = (RepeatCount) range;
            Value countValue = count.count.evaluate();

            if (!countValue.isNatural())
                throw new HtSemanticException("Repeat range must be a natural number, got '" + countValue + "' instead.");

            int countIndex = countValue.integerValue();
            while (countIndex-- > 0) {
                statements.execute();
                rest();
            }
        } else if (range instanceof RepeatDuration) {
            RepeatDuration duration = (RepeatDuration) range;

            // While loop
            if (duration.polarity == RepeatDuration.POLARITY_WHILE) {
                while (duration.condition.evaluate().booleanValue()) {
                    statements.execute();
                    rest();
                }
            }

            // Until loop
            if (duration.polarity == RepeatDuration.POLARITY_UNTIL) {
                while (!duration.condition.evaluate().booleanValue()) {
                    statements.execute();
                    rest();
                }
            }
        } else if (range instanceof RepeatWith) {
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
                    throw new HtSemanticException("Start of repeat range is greater then end: " + from + " > " + to);

                for (int index = from; index <= to; index++) {
                    GlobalContext.getContext().set(symbol, new Value(index));
                    statements.execute();
                    rest();
                }
            } else { // RepeatRange.POLARITY_DOWNTO

                if (to > from)
                    throw new HtSemanticException("End of repeat range is greater then start: " + to + " > " + from);

                for (int index = to; index >= from; index--) {
                    GlobalContext.getContext().set(symbol, new Value(index));
                    statements.execute();
                    rest();
                }
            }
        } else
            throw new RuntimeException("Unknown repeat type");
    }

    private void rest() throws HtException {
        if (ModifierKeyListener.isBreakSequence) {
            throw new HtSemanticException("Script aborted.");
        } else {
            try {
                Thread.sleep(1);
            } catch (InterruptedException e) {
                Thread.interrupted();
            }
        }
    }
}
