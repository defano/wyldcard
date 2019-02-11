package com.defano.hypertalk.ast.statements.commands;

import com.defano.wyldcard.WyldCard;
import com.defano.wyldcard.runtime.context.ExecutionContext;
import com.defano.hypertalk.ast.model.Value;
import com.defano.hypertalk.ast.expressions.Expression;
import com.defano.hypertalk.ast.statements.Command;
import com.defano.hypertalk.exception.HtException;
import com.defano.hypertalk.exception.HtSemanticException;
import com.defano.wyldcard.runtime.context.FileHandle;
import org.antlr.v4.runtime.ParserRuleContext;

public class ReadCmd extends Command {

    private final Expression file;
    private Expression count = null;
    private Expression at = null;
    private Expression until = null;

    private ReadCmd(ParserRuleContext context, Expression file) {
        super(context, "read");
        this.file = file;
    }

    public static ReadCmd ofFile(ParserRuleContext context, Expression file) {
        return new ReadCmd(context, file);
    }

    public static ReadCmd ofFileFor(ParserRuleContext context, Expression file, Expression count) {
        ReadCmd readCmd = new ReadCmd(context, file);
        readCmd.count = count;
        return readCmd;
    }

    public static ReadCmd ofFileAt(ParserRuleContext context, Expression file, Expression at, Expression count) {
        ReadCmd readCmd = new ReadCmd(context, file);
        readCmd.at = at;
        readCmd.count = count;
        return readCmd;
    }

    public static ReadCmd ofFileUntil(ParserRuleContext context, Expression file, Expression until) {
        ReadCmd readCmd = new ReadCmd(context, file);
        readCmd.until = until;
        return readCmd;
    }

    @Override
    public void onExecute(ExecutionContext context) throws HtException {
        try {
            String contents;
            String filename = file.evaluate(context).toString();
            FileHandle file = WyldCard.getInstance().getFileManager().getFileHandle(filename);

            if (file == null) {
                throw new HtSemanticException("Cannot read from file " + filename + " because it is not open.");
            }

            // 'read file x until y'
            if (until != null) {
                contents = file.readUntil(until.evaluate(context).toString(), true);
            }

            // 'read file x at y for z
            else if (at != null && count != null) {
                contents = file.readAt(at.evaluate(context).integerValue(), at.evaluate(context).integerValue(), true);
            }

            // 'read file x for y
            else if (count != null) {
                contents = file.readFor(count.evaluate(context).integerValue(), true);
            }

            // 'read file x'
            else {
                contents = file.readAll(true);
            }

            context.setResult(new Value());
            context.setIt(contents);

        } catch (HtSemanticException e) {
            context.setResult(new Value(e.getMessage()));
        }
    }
}
