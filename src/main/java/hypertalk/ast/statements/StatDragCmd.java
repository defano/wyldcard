package hypertalk.ast.statements;

import hypercard.gui.util.MouseManager;
import hypertalk.ast.common.ExpressionList;
import hypertalk.ast.common.Value;
import hypertalk.ast.expressions.Expression;
import hypertalk.exception.HtSemanticException;

import java.awt.*;

public class StatDragCmd extends Statement {

    private final Expression from;
    private final Expression to;
    private final ExpressionList modifierKeys;

    public StatDragCmd(Expression from, Expression to) {
        this.from = from;
        this.to = to;
        this.modifierKeys = null;
    }

    public StatDragCmd(Expression from, Expression to, ExpressionList modifierKeys) {
        this.from = from;
        this.to = to;
        this.modifierKeys = modifierKeys;
    }

    @Override
    public void execute() throws HtSemanticException {
        boolean withShift = false;
        boolean withOption = false;
        boolean withCommand = false;

        if (modifierKeys != null) {
            for (Value thisModifier : modifierKeys.evaluate()) {
                withShift = thisModifier.equals(new Value("shiftKey")) || withShift;
                withOption = thisModifier.equals(new Value("optionKey")) || withOption;
                withCommand = (thisModifier.equals(new Value("commandKey")) || thisModifier.contains(new Value("cmdKey"))) || withCommand;
            }
        }

        Value from = this.from.evaluate();
        Value to = this.to.evaluate();

        if (!from.isPoint()) {
            throw new HtSemanticException(from.stringValue() + " is not a valid location.");
        }

        if (!to.isPoint()) {
            throw new HtSemanticException(to.stringValue() + " is not a valid location.");
        }

        int x1 = from.listValue().get(0).integerValue();
        int y1 = from.listValue().get(1).integerValue();
        int x2 = to.listValue().get(0).integerValue();
        int y2 = to.listValue().get(1).integerValue();

        MouseManager.dragFrom(new Point(x1, y1), new Point(x2, y2), withShift, withOption, withCommand);
    }
}
