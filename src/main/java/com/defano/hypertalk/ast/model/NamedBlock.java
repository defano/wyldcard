package com.defano.hypertalk.ast.model;

import com.defano.hypertalk.ast.statements.StatementList;
import com.defano.hypertalk.ast.statements.commands.PassCmd;

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
        NamedBlock block = new NamedBlock(name, name, new StatementList(null, new PassCmd(null, name)));
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
        return new NamedBlock("", "", new ParameterList(), statementList);
    }

    public NamedBlock (String onName, String endName, StatementList body) {
        this(onName, endName, new ParameterList(), body);
    }

    public NamedBlock (String onName, String endName, ParameterList parameters, StatementList body) {
        if (!onName.equalsIgnoreCase(endName)) {
            throw new IllegalArgumentException("Handler on ID " + onName + " does not match end ID " + endName);
        }

        this.name = onName;
        this.statements = body;
        this.parameters = parameters;
    }

    public boolean isEmptyPassBlock() {
        return isEmptyPassBlock;
    }
}
