/*
 * StatPutCmd
 * hypertalk-java
 *
 * Created by Matt DeFano on 2/19/17 3:11 PM.
 * Copyright © 2017 Matt DeFano. All rights reserved.
 */

/**
 * PutCmd.java
 * @author matt.defano@gmail.com
 * 
 * Encapsulation of the "put" command
 */

package com.defano.hypertalk.ast.statements;

import com.defano.hypertalk.ast.containers.Preposition;
import com.defano.hypertalk.ast.expressions.Expression;
import com.defano.hypertalk.exception.HtException;
import com.defano.hypertalk.ast.containers.Container;

public class PutCmd extends Command {

    public final Expression expression;
    public final Preposition preposition;
    public final Container container;
    
    public PutCmd(Expression e, Preposition p, Container d) {
        super("put");

        expression = e;
        preposition = p;
        container = d;
    }
    
    public void onExecute () throws HtException {
        container.putValue(expression.evaluate(), preposition);
    }
}
