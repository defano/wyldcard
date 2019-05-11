package com.defano.hypertalk.ast.model;

import com.defano.hypertalk.ast.statement.Statement;
import com.defano.hypertalk.ast.statement.StatementList;
import com.defano.hypertalk.exception.HtSyntaxException;
import com.defano.hypertalk.exception.HtUncheckedSemanticException;
import org.antlr.v4.runtime.ParserRuleContext;
import org.apache.commons.lang.builder.ToStringBuilder;

import java.util.Collection;

public class NamedBlock {

    public final String name;
    public final StatementList statements;
    public final ParameterList parameters;
    public final ParserRuleContext context;

    /**
     * Wraps a list of statements in an NamedBlock object whose name is unused.
     *
     * @param statementList The list of statements
     * @return A NamedBlock representing the
     */
    public static NamedBlock anonymousBlock(StatementList statementList) {
        return new NamedBlock(null, "", "", new ParameterList(), statementList);
    }

    public NamedBlock (ParserRuleContext context, String onName, String endName, StatementList body) {
        this(context, onName, endName, new ParameterList(), body);
    }

    public NamedBlock (ParserRuleContext context, String onName, String endName, ParameterList parameters, StatementList body) {
        if (onName == null) {
            throw new HtUncheckedSemanticException(new HtSyntaxException("Missing 'on' clause in handler definition.", context.getStart()));
        }

        if (endName == null) {
            throw new HtUncheckedSemanticException(new HtSyntaxException("Missing 'end' clause in handler definition.", context.getStart()));
        }

        if (!onName.equalsIgnoreCase(endName)) {
            throw new HtUncheckedSemanticException(new HtSyntaxException("Found 'end " + endName + "' but expected 'end " + onName + "'.", context.getStart()));
        }

        this.name = onName;
        this.statements = body;
        this.parameters = parameters;
        this.context = context;
    }

    public Collection<Statement> findStatementsOnLine(int line) {
        return statements.findStatementsOnLine(line);
    }

    public Integer getLineNumber() {
        if (context != null && context.getStart() != null) {
            return context.getStart().getLine();
        } else {
            return null;
        }
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }
}
