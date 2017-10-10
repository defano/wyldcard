package com.defano.hypertalk.ast.constructs;

import com.defano.hypertalk.ast.statements.StatementList;

public class ThenElseBlock {

    public final StatementList thenBranch;
    public final StatementList elseBranch;
    
    public ThenElseBlock (StatementList thenBranch, StatementList elseBranch) {
        this.thenBranch = thenBranch;
        this.elseBranch = elseBranch;
    }
}
