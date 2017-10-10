package com.defano.hypertalk.ast.functions;

import com.defano.hypertalk.ast.common.NamedBlock;
import com.defano.hypertalk.ast.common.ParameterList;
import com.defano.hypertalk.ast.statements.StatementList;

public class UserFunction extends NamedBlock {

    public UserFunction (String onName, String endName, ParameterList parameters, StatementList statements) {
        super(onName, endName, parameters, statements);
    }
}
