package com.defano.wyldcard.runtime;

import com.defano.wyldcard.parts.PartException;
import com.defano.wyldcard.parts.model.PartModel;
import com.defano.wyldcard.parts.stack.StackPart;
import com.defano.wyldcard.runtime.context.ExecutionContext;
import com.defano.hypertalk.ast.model.specifiers.PartSpecifier;
import com.defano.hypertalk.utils.Range;
import org.antlr.v4.runtime.Token;

public class Breadcrumb {

    private final ExecutionContext context;
    private final Token token;
    private final PartSpecifier part;

    public Breadcrumb (ExecutionContext context, Token token) {
        this.context = context;
        this.token = token;
        this.part = context.getStackFrame().getMe();
    }

    public Breadcrumb (Token token) {
        this.token = token;
        this.part = null;
        this.context = null;
    }

    public Range getCharRange() {
        int start = token.getStartIndex();
        int end = token.getStopIndex();

        if (end > start) {
            return new Range(start, end + 1);
        }

        return null;
    }

    public Token getToken() {
        return token;
    }

    public PartSpecifier getPart() {
        return part;
    }

    public ExecutionContext getContext() {
        return context;
    }

    @Override
    public String toString() {
        String breadcrumb = "";

        if ( token != null) {
            breadcrumb += "line " + token.getLine() + ", column " + token.getCharPositionInLine();
        }

        if (part != null) {
            breadcrumb += " of " + part.getHyperTalkIdentifier(new ExecutionContext());
        }

        return breadcrumb;
    }

    public PartModel getPartModel() {
        PartModel partModel = null;

        if (getPart() != null) {
            try {
                partModel = context.getPart(getPart());
            } catch (PartException e) {
                // Nothing to do
            }
        }

        return partModel;
    }

}
