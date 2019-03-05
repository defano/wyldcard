package com.defano.hypertalk.ast.statements.commands;

import com.defano.hypertalk.ast.expressions.Expression;
import com.defano.hypertalk.ast.model.Value;
import com.defano.hypertalk.ast.statements.Command;
import com.defano.hypertalk.exception.HtException;
import com.defano.wyldcard.StackManager;
import com.defano.wyldcard.parts.stack.StackModel;
import com.defano.wyldcard.runtime.context.ExecutionContext;
import com.google.inject.Inject;
import org.antlr.v4.runtime.ParserRuleContext;

import java.io.File;

public class SaveStackCmd extends Command {

    private final Expression stackExpr;
    private final Expression fileExpr;

    @Inject
    private StackManager stackManager;

    public SaveStackCmd(ParserRuleContext context, Expression stackExpr, Expression fileExpr) {
        super(context, "save");

        this.stackExpr = stackExpr;
        this.fileExpr = fileExpr;
    }

    public SaveStackCmd(ParserRuleContext context, Expression fileExpr) {
        this(context, null, fileExpr);
    }

    @Override
    protected void onExecute(ExecutionContext context) throws HtException {

        File destinationFile = evaluateAsStackFile(context, fileExpr);

        if (stackExpr == null) {
            stackManager.saveStack(context, context.getCurrentStack().getStackModel(), destinationFile);
        } else {

            File sourceFile = evaluateAsStackFile(context, stackExpr);
            StackModel stack = stackManager.loadStack(context, sourceFile);

            if (stack == null) {
                context.setResult(new Value("No such stack"));
            } else {
                stackManager.saveStack(context, stack, destinationFile);
            }
        }
    }

    private File evaluateAsStackFile(ExecutionContext context, Expression expr) throws HtException {
        String fileName = expr.evaluate(context).toString();

        if (!fileName.endsWith(StackModel.FILE_EXTENSION)) {
            fileName = fileName + StackModel.FILE_EXTENSION;
        }

        return new File(fileName);
    }
}
