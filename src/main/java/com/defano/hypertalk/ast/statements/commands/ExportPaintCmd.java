package com.defano.hypertalk.ast.statements.commands;

import com.defano.wyldcard.paint.ArtVandelay;
import com.defano.hypertalk.ast.expressions.Expression;
import com.defano.hypertalk.ast.statements.Command;
import com.defano.hypertalk.exception.HtException;
import com.defano.wyldcard.runtime.context.ExecutionContext;
import org.antlr.v4.runtime.ParserRuleContext;

import java.io.File;

public class ExportPaintCmd extends Command {

    private final Expression fileExpression;

    public ExportPaintCmd(ParserRuleContext context, Expression fileExpression) {
        super(context, "export");
        this.fileExpression = fileExpression;
    }

    @Override
    protected void onExecute(ExecutionContext context) throws HtException {
        ArtVandelay.exportPaint(new File(fileExpression.evaluate(context).toString()));
    }
}
