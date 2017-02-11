package hypertalk.ast.statements;

import hypercard.gui.util.MouseManager;
import hypertalk.ast.common.ExpressionList;
import hypertalk.ast.common.Value;
import hypertalk.ast.expressions.Expression;
import hypertalk.exception.HtSemanticException;

import java.awt.*;

public class StatClickCmd extends Statement {

    private final Expression clickLoc;
    private final ExpressionList modifierKeys;

    public StatClickCmd (Expression clickLoc) {
        this.clickLoc = clickLoc;
        this.modifierKeys = null;
    }

    public StatClickCmd (Expression clickLoc, ExpressionList modifierKeys) {
        this.clickLoc = clickLoc;
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

        Value clickLoc = this.clickLoc.evaluate();

        if (clickLoc.isPoint()) {
            int xLoc = clickLoc.getItems().get(0).integerValue();
            int yLoc = clickLoc.getItems().get(1).integerValue();

            MouseManager.clickAt(new Point(xLoc, yLoc), withShift, withOption, withCommand);
        } else {
            throw new HtSemanticException(clickLoc.stringValue() + " is not a valid location.");
        }
    }
}
