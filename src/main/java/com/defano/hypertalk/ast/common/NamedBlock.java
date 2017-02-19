/*
 * NamedBlock
 * hypertalk-java
 *
 * Created by Matt DeFano on 2/19/17 3:11 PM.
 * Copyright © 2017 Matt DeFano. All rights reserved.
 */

/**
 * NamedBlock.java
 * @author matt.defano@gmail.com
 * 
 * Representation of a named block of statements, such as a function
 * definition or event handler. 
 */

package com.defano.hypertalk.ast.common;

import com.defano.hypertalk.ast.statements.StatementList;

public class NamedBlock {

    public final String name;
    public final StatementList body;
    
    public NamedBlock (String name, StatementList body) {
        this.name = name;
        this.body = body;
    }
}
