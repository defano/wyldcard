package com.defano.hypertalk.ast;

import org.antlr.v4.runtime.ParserRuleContext;

public abstract class ASTNode {

    public final ParserRuleContext context;

    public ASTNode(ParserRuleContext context) {
        this.context = context;
    }
}
