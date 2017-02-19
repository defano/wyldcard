/*
 * ThenElseBlock
 * hypertalk-java
 *
 * Created by Matt DeFano on 2/19/17 3:11 PM.
 * Copyright © 2017 Matt DeFano. All rights reserved.
 */

/**
 * ThenElseBlock.java
 * @author matt.defano@gmail.com
 * 
 * Encapsulates the then and else branches of a conditional statement.
 */

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
