package com.defano.hypertalk.ast.model;

import com.defano.hypertalk.ast.statements.Statement;
import com.defano.hypertalk.ast.statements.StatementList;
import com.defano.hypertalk.ast.statements.commands.PassCmd;
import com.defano.hypertalk.exception.HtSyntaxException;
import com.defano.hypertalk.exception.HtUncheckedSemanticException;
import org.antlr.v4.runtime.ParserRuleContext;

import java.util.Collection;

public class NamedBlock {

    public final String name;
    public final StatementList statements;
    public final ParameterList parameters;

    private boolean isEmptyPassBlock;

    /**
     * Creates a NamedBlock instance that simply passes the name of the block back to HyperCard. Useful as a "default"
     * handler for objects that do not implement a handler for this message themselves.
     *
     * @param name The name of the block and message name to be passed.
     * @return An empty NamedBlock that passes the command back to HyperCard.
     */
    public static NamedBlock emptyPassBlock(String name) {
        NamedBlock block = new NamedBlock(null, name, name, new StatementList(new PassCmd(null, name)));
        block.isEmptyPassBlock = true;
        return block;
    }

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
    }

    public boolean isEmptyPassBlock() {
        return isEmptyPassBlock;
    }

    public Collection<Statement> findStatementsOnLine(int line) {
        return statements.findStatementsOnLine(line);
    }

}
