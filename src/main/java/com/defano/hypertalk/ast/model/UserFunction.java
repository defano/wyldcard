package com.defano.hypertalk.ast.model;

import com.defano.hypertalk.ast.statements.StatementList;

public class UserFunction extends NamedBlock {

    public UserFunction (String onName, String endName, ParameterList parameters, StatementList statements) {
        super(onName, endName, parameters, statements);
    }
}
