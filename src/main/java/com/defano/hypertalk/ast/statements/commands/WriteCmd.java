package com.defano.hypertalk.ast.statements.commands;

import com.defano.wyldcard.runtime.context.ExecutionContext;
import com.defano.wyldcard.runtime.context.FileContext;
import com.defano.hypertalk.ast.model.Value;
import com.defano.hypertalk.ast.expressions.Expression;
import com.defano.hypertalk.ast.statements.Command;
import com.defano.hypertalk.exception.HtException;
import com.defano.hypertalk.exception.HtSemanticException;
import org.antlr.v4.runtime.ParserRuleContext;

public class WriteCmd extends Command {

    private final Expression file;
    private final Expression data;
    private final Expression at;
    private final boolean append;

    private WriteCmd(ParserRuleContext context, Expression data, Expression file, Expression at, boolean append) {
        super(context, "write");

        this.data = data;
        this.file = file;
        this.at = at;
        this.append = append;
    }

    public static WriteCmd writeFile(ParserRuleContext context, Expression data, Expression file) {
        return new WriteCmd(context, data, file, null, false);
    }

    public static WriteCmd writeFileAt(ParserRuleContext context, Expression data, Expression file, Expression at) {
        return new WriteCmd(context, data, file, at, false);
    }

    public static WriteCmd appendFile(ParserRuleContext context, Expression data, Expression file) {
        return new WriteCmd(context, data, file, null, true);
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
