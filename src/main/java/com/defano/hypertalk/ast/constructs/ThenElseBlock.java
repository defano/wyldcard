package com.defano.hypertalk.ast.constructs;

import com.defano.hypertalk.ast.statements.Statement;

public class ThenElseBlock {

    public final Statement thenBranch;
    public final Statement elseBranch;

    public ThenElseBlock (Statement thenBranch, Statement elseBranch) {
        this.thenBranch = thenBranch;
        this.elseBranch = elseBranch;
    }
}
