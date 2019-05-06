package com.defano.hypertalk.ast.statements.commands;

import com.defano.hypertalk.ast.expressions.Expression;
import com.defano.hypertalk.ast.model.specifiers.WindowNameSpecifier;
import com.defano.hypertalk.ast.preemptions.Preemption;
import com.defano.hypertalk.ast.statements.Command;
import com.defano.hypertalk.exception.HtException;
import com.defano.wyldcard.WyldCard;
import com.defano.wyldcard.runtime.ExecutionContext;
import org.antlr.v4.runtime.ParserRuleContext;

public class CloseWindowCmd extends Command {

    private final Expression windowNameExpr;

    public CloseWindowCmd(ParserRuleContext context, Expression windowNameExpr) {
        super(context, "close");
        this.windowNameExpr = windowNameExpr;
    }

    @Override
    protected void onExecute(ExecutionContext context) throws HtException, Preemption {
        String windowName = windowNameExpr.evaluate(context).toString();
        WyldCard.getInstance().getWindowManager().findWindow(context, new WindowNameSpecifier(windowName)).setVisible(false);
    }
}
