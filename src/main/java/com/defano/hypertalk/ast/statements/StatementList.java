/*
 * StatementList
 * hypertalk-java
 *
 * Created by Matt DeFano on 2/19/17 3:11 PM.
 * Copyright © 2017 Matt DeFano. All rights reserved.
 */

/**
 * StatementList.java
 * @author matt.defano@gmail.com
 * 
 * Encapsulation of a list of statements (e.g., the body of a function or handler)
 */

package com.defano.hypertalk.ast.statements;

import com.defano.hypertalk.ast.breakpoints.Breakpoint;
import com.defano.hypertalk.exception.HtException;

import java.util.Vector;

public class StatementList {

    public final Vector<Statement> list;

    public StatementList () {
        list = new Vector<>();
    }
    
    public StatementList (Statement s) {
        list = new Vector<>();
        append(s);
    }

    public StatementList append (Statement s) {
        list.add(s);
        return this;
    }

    public void execute() throws HtException, Breakpoint {
        for (Statement s : list) {
            s.execute();
        }
    }
}
