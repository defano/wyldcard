package com.defano.hypertalk.ast;

import com.defano.wyldcard.runtime.Breadcrumb;
import com.defano.wyldcard.runtime.context.ExecutionContext;
import com.defano.hypertalk.exception.HtException;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.Token;

/**
 * A base class for all nodes in the Abstract Syntax Tree. Holds a reference to the Antlr parser context referring to
 * where in the script this node was encountered.
 */
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

    /**
     * Gets the Antlr parse rule context that was provided when this node was instantiated. May be null if this node
     * was generated outside of parsing a script.
     *
     * @return The ParserRuleContext associated with this node.
     */
    protected ParserRuleContext getContext() {
        return context;
    }

    /**
     * Given a HyperTalk exception, this method modifies the exception object by injecting a "breadcrumb" into it and
     * then then re-throws it.
     *
     * A breadcrumb consists of the parser context (i.e., the line and column number of the script text associated with
     * this node), plus a reference to 'me' (the HyperCard object to which the script belongs).
     *
     * Has no effect if the given exception already contains a breadcrumb.
     *
     * @param e The exception to contextualize and re-throw.
     * @throws HtException The contextualized exception
     */
    protected void rethrowContextualizedException(HtException e) throws HtException {
        if (e.getBreadcrumb() == null) {
            e.setBreadcrumb(new Breadcrumb(getToken(), ExecutionContext.getContext().getMe()));
        }

        throw e;
    }

}
