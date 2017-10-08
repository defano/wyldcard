package com.defano.hypertalk.ast;

import com.defano.hypercard.runtime.Breadcrumb;
import com.defano.hypercard.runtime.context.ExecutionContext;
import com.defano.hypertalk.exception.HtException;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.Token;

public abstract class ASTNode {

    private final ParserRuleContext context;

    public ASTNode(ParserRuleContext context) {
        this.context = context;
    }

    /**
     * Gets the starting Antlr token associated with this AST node, or null if this node was generated outside of a
     * parsed script text.
     *
     * @return The beginning token of this node, or null if it cannot be determined.
     */
    protected Token getToken() {
        return context == null ? null : context.getStart();
    }

    protected ParserRuleContext getContext() {
        return context;
    }

    protected void rethrowContextualizedException(HtException e) throws HtException {
        e.setBreadcrumb(new Breadcrumb(getToken(), ExecutionContext.getContext().getMe()));
        throw e;
    }

}
