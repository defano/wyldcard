package com.defano.hypertalk.ast.commands;

import com.defano.hypercard.runtime.context.ExecutionContext;
import com.defano.hypercard.runtime.context.FileContext;
import com.defano.hypertalk.ast.common.Value;
import com.defano.hypertalk.ast.expressions.Expression;
import com.defano.hypertalk.ast.statements.Command;
import com.defano.hypertalk.exception.HtException;
import com.defano.hypertalk.exception.HtSemanticException;

public class WriteCmd extends Command {

    private final Expression file;
    private final Expression data;
    private final Expression at;
    private final boolean append;

    private WriteCmd(Expression data, Expression file, Expression at, boolean append) {
        super("write");

        this.data = data;
        this.file = file;
        this.at = at;
        this.append = append;
    }

    public static WriteCmd writeFile(Expression data, Expression file) {
        return new WriteCmd(data, file, null, false);
    }

    public static WriteCmd writeFileAt(Expression data, Expression file, Expression at) {
        return new WriteCmd(data, file, at, false);
    }

    public static WriteCmd appendFile(Expression data, Expression file) {
        return new WriteCmd(data, file, null, true);
    }

    @Override
    public void onExecute() throws HtException {
        try {
            String filename = file.evaluate().stringValue();
            FileContext.FileHandle handle = FileContext.getInstance().getFileHandle(filename);

            if (handle == null) {
                throw new HtSemanticException("Cannot write to file " + file + " because it is not open.");
            }

            // 'write x to file y at end'
            if (append) {
                handle.writeAtTail(data.evaluate().stringValue(), true);
            }

            // 'write x to file y'
            else if (at == null) {
                handle.write(data.evaluate().stringValue(), true);
            }

            // 'write x to file y at z'
            else {
                handle.writeAt(data.evaluate().stringValue(), at.evaluate().integerValue(), true);
            }

            ExecutionContext.getContext().setResult(new Value());

        } catch (HtSemanticException e) {
            ExecutionContext.getContext().setResult(new Value(e.getMessage()));
        }
    }
}
