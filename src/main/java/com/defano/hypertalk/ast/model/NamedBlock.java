package com.defano.hypertalk.ast.model;

import com.defano.hypertalk.ast.statements.StatementList;

public class NamedBlock {

    public final String name;
    public final StatementList statements;
    public final ParameterList parameters;
    
    public NamedBlock (String onName, String endName, StatementList body) {
        this(onName, endName, new ParameterList(), body);
    }

    public NamedBlock (String onName, String endName, ParameterList parameters, StatementList body) {
        if (!onName.equalsIgnoreCase(endName)) {
            throw new IllegalArgumentException("Handler on ID " + onName + " does not match end ID " + endName);
        }

        this.name = onName;
        this.statements = body;
        this.parameters = parameters;
    }
}
