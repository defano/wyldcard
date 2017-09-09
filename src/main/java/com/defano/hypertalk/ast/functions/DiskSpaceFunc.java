package com.defano.hypertalk.ast.functions;

import com.defano.hypertalk.ast.common.Value;
import com.defano.hypertalk.ast.expressions.Expression;
import com.defano.hypertalk.exception.HtSemanticException;

import java.io.File;

public class DiskSpaceFunc extends Expression {

    private final Expression diskExpr;

    public DiskSpaceFunc() {
        this.diskExpr = null;
    }

    public DiskSpaceFunc(Expression diskExpr) {
        this.diskExpr = diskExpr;
    }

    @Override
    public Value evaluate() throws HtSemanticException {
        File f = diskExpr == null ? new File(".") : new File(diskExpr.evaluate().stringValue());

        if (!f.exists()) {
            throw new HtSemanticException("That disk does not exist.");
        }

        return new Value(f.getFreeSpace());
    }
}
