package com.defano.hypertalk.ast;

import com.defano.hypercard.runtime.Breadcrumb;
import com.defano.hypercard.runtime.context.ExecutionContext;
import com.defano.hypertalk.exception.HtException;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.Token;

public abstract class ASTNode {

    // The staring Antlr token associated with this node in the abstract syntax tree; held as a debugging breadcrumb
    private final Token token;

    public ASTNode(ParserRuleContext context) {
        this.token = context == null ? null : context.getStart();
    }

    /**
     * Gets the starting Antlr token associated with this AST node.
     * @return The beginning token of this node.
     */
    protected Token getToken() {
        return token;
    }

    protected void rethrowContextualizedException(HtException e) throws HtException {
        try {
            e.setBreadcrumb(new Breadcrumb(getToken(), ExecutionContext.getContext().getMe()));
        } catch (Throwable t) {
            // Nothing to do
        }

        throw e;
    }

}
