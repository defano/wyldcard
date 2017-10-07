package com.defano.hypertalk.ast.commands;

import com.defano.hypercard.runtime.context.ExecutionContext;
import com.defano.hypercard.runtime.context.FileContext;
import com.defano.hypertalk.ast.common.Value;
import com.defano.hypertalk.ast.expressions.Expression;
import com.defano.hypertalk.ast.statements.Command;
import com.defano.hypertalk.exception.HtException;
import com.defano.hypertalk.exception.HtSemanticException;
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
    public void onExecute() throws HtException {
        try {
            String contents;
            String filename = file.evaluate().stringValue();
            FileContext.FileHandle handle = FileContext.getInstance().getFileHandle(filename);

            if (handle == null) {
                throw new HtSemanticException("Cannot read from file " + filename + " because it is not open.");
            }

            // 'read file x until y'
            if (until != null) {
                contents = handle.readUntil(until.evaluate().stringValue(), true);
            }

            // 'read file x at y for z
            else if (at != null && count != null) {
                contents = handle.readAt(at.evaluate().integerValue(), at.evaluate().integerValue(), true);
            }

            // 'read file x for y
            else if (count != null) {
                contents = handle.readFor(count.evaluate().integerValue(), true);
            }

            // 'read file x'
            else {
                contents = handle.readAll(true);
            }

            ExecutionContext.getContext().setResult(new Value());
            ExecutionContext.getContext().setIt(contents);

        } catch (HtSemanticException e) {
            ExecutionContext.getContext().setResult(new Value(e.getMessage()));
        }
    }
}
