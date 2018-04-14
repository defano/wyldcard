package com.defano.hypertalk.ast.model;

import com.defano.hypertalk.ast.statements.StatementList;
import org.antlr.v4.runtime.ParserRuleContext;

public class UserFunction extends NamedBlock {

    public UserFunction (ParserRuleContext context, String onName, String endName, ParameterList parameters, StatementList statements) {
        super(context, onName, endName, parameters, statements);
    }
}
