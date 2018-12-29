package com.defano.hypertalk.ast.expressions.functions;

import com.defano.hypertalk.ast.model.Value;
import com.defano.hypertalk.ast.expressions.Expression;
import com.defano.hypertalk.exception.HtException;
import com.defano.hypertalk.exception.HtSemanticException;
import com.defano.wyldcard.runtime.context.ExecutionContext;
import org.antlr.v4.runtime.ParserRuleContext;

import java.io.File;

public class DiskSpaceFunc extends Expression {

    private final Expression diskExpr;

    public DiskSpaceFunc(ParserRuleContext context) {
        super(context);
        this.diskExpr = null;
    }

    public DiskSpaceFunc(ParserRuleContext context, Expression diskExpr) {
        super(context);
        this.diskExpr = diskExpr;
    }

    @Override
    public Value onEvaluate(ExecutionContext context) throws HtException {
        File f = diskExpr == null ? new File(".") : new File(diskExpr.evaluate(context).toString());

        if (!f.exists()) {
            throw new HtSemanticException("That disk does not exist.");
        }

        return new Value(f.getFreeSpace());
    }
}
