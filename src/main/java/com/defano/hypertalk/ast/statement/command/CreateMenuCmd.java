package com.defano.hypertalk.ast.statement.command;

import com.defano.wyldcard.WyldCard;
import com.defano.hypertalk.ast.expression.Expression;
import com.defano.hypertalk.ast.statement.Command;
import com.defano.hypertalk.exception.HtException;
import com.defano.wyldcard.runtime.ExecutionContext;
import org.antlr.v4.runtime.ParserRuleContext;

public class CreateMenuCmd extends Command {

    private final Expression menuName;

    public CreateMenuCmd(ParserRuleContext context, Expression menuName) {
        super(context, "create");
        this.menuName = menuName;
    }

    @Override
    public void onExecute(ExecutionContext context) throws HtException {
        WyldCard.getInstance().getWyldCardMenuBar().createMenu(menuName.evaluate(context).toString());
    }
}
