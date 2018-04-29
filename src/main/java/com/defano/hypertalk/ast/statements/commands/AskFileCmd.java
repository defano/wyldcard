package com.defano.hypertalk.ast.statements.commands;

import com.defano.hypertalk.ast.expressions.Expression;
import com.defano.hypertalk.ast.model.Value;
import com.defano.hypertalk.ast.preemptions.Preemption;
import com.defano.hypertalk.ast.statements.Command;
import com.defano.hypertalk.exception.HtException;
import com.defano.wyldcard.parts.stack.StackModel;
import com.defano.wyldcard.runtime.context.ExecutionContext;
import com.defano.wyldcard.window.WindowManager;
import org.antlr.v4.runtime.ParserRuleContext;

import java.awt.*;
import java.io.File;

public class AskFileCmd extends Command {

    private final Expression promptExpression;
    private final Expression fileExpression;

    public AskFileCmd(ParserRuleContext context, Expression prompt) {
        this(context, prompt, null);
    }

    public AskFileCmd(ParserRuleContext context, Expression prompt, Expression file) {
        super(context, "ask");
        this.promptExpression = prompt;
        this.fileExpression = file;
    }

    @Override
    protected void onExecute(ExecutionContext context) throws HtException {
        String prompt = promptExpression.evaluate(context).stringValue();
        FileDialog fd = new FileDialog(WindowManager.getInstance().getWindowForStack(context.getCurrentStack()).getWindow(), prompt, FileDialog.SAVE);

        if (fileExpression != null) {
            fd.setFile(fileExpression.evaluate(context).stringValue());
        }

        fd.setVisible(true);
        if (fd.getFiles().length > 0) {
            File f = fd.getFiles()[0];
            String path = f.getAbsolutePath().endsWith(StackModel.FILE_EXTENSION) ?
                    f.getAbsolutePath() :
                    f.getAbsolutePath() + StackModel.FILE_EXTENSION;

            context.setIt(new Value(path));
            context.setResult(new Value());
        } else {
            context.setResult(new Value("Cancel"));
        }
    }
}
