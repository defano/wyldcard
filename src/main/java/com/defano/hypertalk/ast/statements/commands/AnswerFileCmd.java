package com.defano.hypertalk.ast.statements.commands;

import com.defano.hypertalk.ast.expressions.Expression;
import com.defano.hypertalk.ast.model.Value;
import com.defano.hypertalk.ast.preemptions.Preemption;
import com.defano.hypertalk.ast.statements.Command;
import com.defano.hypertalk.exception.HtException;
import com.defano.wyldcard.WyldCard;
import com.defano.wyldcard.runtime.context.ExecutionContext;
import org.antlr.v4.runtime.ParserRuleContext;

import java.awt.*;

public class AnswerFileCmd extends Command {

    private final Expression promptExpr;
    private final Expression filterExpr;

    public AnswerFileCmd(ParserRuleContext context, Expression promptExpr) {
        this(context, promptExpr, null);
    }

    public AnswerFileCmd(ParserRuleContext context, Expression promptExpr, Expression filterExpr) {
        super(context, "answer");
        this.promptExpr = promptExpr;
        this.filterExpr = filterExpr;
    }

    @Override
    protected void onExecute(ExecutionContext context) throws HtException, Preemption {
        String promptString = promptExpr.evaluate(context).toString();

        FileDialog fd = new FileDialog(WyldCard.getInstance().getWindowManager().getWindowForStack(context, context.getCurrentStack()).getWindow(), promptString, FileDialog.LOAD);
        fd.setMultipleMode(false);

        // TODO: Support for file types and signatures, not just extensions
        if (filterExpr != null) {
            String fileExtension = filterExpr.evaluate(context).toString();
            fd.setFilenameFilter((dir, name) -> name.endsWith(fileExtension));
        }

        fd.setVisible(true);

        if (fd.getFiles().length > 0) {
            context.setIt(new Value(fd.getFiles()[0].getAbsolutePath()));
            context.setResult(new Value());
        } else {
            context.setIt(new Value());
            context.setResult(new Value("Cancel"));
        }

    }
}
