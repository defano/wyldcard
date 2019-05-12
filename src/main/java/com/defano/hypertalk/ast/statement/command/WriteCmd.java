package com.defano.hypertalk.ast.statement.command;

import com.defano.wyldcard.WyldCard;
import com.defano.wyldcard.runtime.ExecutionContext;
import com.defano.hypertalk.ast.model.Value;
import com.defano.hypertalk.ast.expression.Expression;
import com.defano.hypertalk.ast.statement.Command;
import com.defano.hypertalk.exception.HtException;
import com.defano.hypertalk.exception.HtSemanticException;
import com.defano.wyldcard.runtime.manager.FileHandle;
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
    public void onExecute(ExecutionContext context) throws HtException {
        try {
            String filename = file.evaluate(context).toString();
            FileHandle handle = WyldCard.getInstance().getFileManager().getFileHandle(filename);

            if (handle == null) {
                throw new HtSemanticException("Cannot write to file " + file + " because it is not open.");
            }

            // 'write x to file y at end'
            if (append) {
                handle.writeAtTail(data.evaluate(context).toString(), true);
            }

            // 'write x to file y'
            else if (at == null) {
                handle.write(data.evaluate(context).toString(), true);
            }

            // 'write x to file y at z'
            else {
                handle.writeAt(data.evaluate(context).toString(), at.evaluate(context).integerValue(), true);
            }

            context.setResult(new Value());

        } catch (HtSemanticException e) {
            context.setResult(new Value(e.getMessage()));
        }
    }
}
