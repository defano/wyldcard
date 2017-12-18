package com.defano.hypertalk.ast.expressions.functions;

import com.defano.hypertalk.ast.model.NamedBlock;
import com.defano.hypertalk.ast.model.ParameterList;
import com.defano.hypertalk.ast.statements.StatementList;

public class UserFunction extends NamedBlock {

    public UserFunction (String onName, String endName, ParameterList parameters, StatementList statements) {
        super(onName, endName, parameters, statements);
    }
}
