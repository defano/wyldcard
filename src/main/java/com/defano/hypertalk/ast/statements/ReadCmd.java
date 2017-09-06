package com.defano.hypertalk.ast.statements;

import com.defano.hypercard.context.ExecutionContext;
import com.defano.hypercard.context.FileTable;
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
        ReadCmd readCmd = new ReadCmd(file);
        return readCmd;
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

    public static ReadCmd ofFileUntil(Expression file, Expression at, Expression until) {
        ReadCmd readCmd = new ReadCmd(file);
        readCmd.at = at;
        readCmd.at = until;
        return readCmd;
    }

    @Override
    public void execute() throws HtException {
        try {
            String filename = file.evaluate().stringValue();
            FileTable.FileHandle handle = FileTable.getInstance().getOpenFileHandle(filename);
            String contents;

            if (at != null && until != null) {
                contents = handle.readUntil(at.evaluate().integerValue(), until.evaluate().stringValue());
            }

            else if (at != null && count != null) {
                contents = handle.readAt(at.evaluate().integerValue(), at.evaluate().integerValue());
            }

            else if (count != null) {
                contents = handle.readFor(count.evaluate().integerValue());
            }

            else {
                contents = handle.readAll();
            }

            ExecutionContext.getContext().setResult(new Value());
            ExecutionContext.getContext().setIt(contents);

        } catch (HtSemanticException e) {
            ExecutionContext.getContext().setResult(new Value(e.getMessage()));
        }
    }
}
