package com.defano.hypertalk.ast.commands;

import com.defano.hypertalk.ast.common.Value;
import com.defano.hypertalk.ast.containers.Container;
import com.defano.hypertalk.ast.common.Preposition;
import com.defano.hypertalk.ast.statements.Command;
import com.defano.hypertalk.exception.HtException;
import org.antlr.v4.runtime.ParserRuleContext;

public class DeleteChunkCmd extends Command {

    private final Container container;

    public DeleteChunkCmd(ParserRuleContext context, Container container) {
        super(context, "delete");
        this.container = container;
    }

    @Override
    public void onExecute() throws HtException {
        container.putValue(new Value(), Preposition.INTO);
    }
}
