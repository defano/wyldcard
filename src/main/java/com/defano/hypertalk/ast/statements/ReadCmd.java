package com.defano.hypertalk.ast.statements;

import com.defano.hypercard.context.ExecutionContext;
import com.defano.hypercard.context.FileContext;
import com.defano.hypertalk.ast.common.Value;
import com.defano.hypertalk.ast.expressions.Expression;
import com.defano.hypertalk.exception.HtException;
import com.defano.hypertalk.exception.HtSemanticException;

public class ReadCmd extends Statement {

    private final Expression file;
    private Expression count = null;
    private Expression at = null;
    private Expression until = null;

    private ReadCmd(Expression file) {
        this.file = file;
    }

    public static ReadCmd ofFile(Expression file) {
        return new ReadCmd(file);
    }

    public static ReadCmd ofFileFor(Expression file, Expression count) {
        ReadCmd readCmd = new ReadCmd(file);
        readCmd.count = count;
        return readCmd;
    }

    public static ReadCmd ofFileAt(Expression file, Expression at, Expression count) {
        ReadCmd readCmd = new ReadCmd(file);
        readCmd.at = at;
        readCmd.count = count;
        return readCmd;
    }

    public static ReadCmd ofFileUntil(Expression file, Expression until) {
        ReadCmd readCmd = new ReadCmd(file);
        readCmd.until = until;
        return readCmd;
    }

    @Override
    public void execute() throws HtException {
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
