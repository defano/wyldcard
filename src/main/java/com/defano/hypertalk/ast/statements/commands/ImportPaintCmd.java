package com.defano.hypertalk.ast.statements.commands;

import com.defano.hypercard.paint.ArtVandelay;
import com.defano.hypertalk.ast.expressions.Expression;
import com.defano.hypertalk.ast.statements.Command;
import com.defano.hypertalk.exception.HtException;
import org.antlr.v4.runtime.ParserRuleContext;

import java.io.File;

public class ImportPaintCmd extends Command {

    private final Expression fileExpression;

    public ImportPaintCmd(ParserRuleContext context, Expression fileExpression) {
        super(context, "import");
        this.fileExpression = fileExpression;
    }

    @Override
    protected void onExecute() throws HtException {
        ArtVandelay.importPaint(new File(fileExpression.evaluate().stringValue()));
    }
}
