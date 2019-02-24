package com.defano.hypertalk.ast.statements.commands;

import com.defano.hypertalk.ast.expressions.Expression;
import com.defano.hypertalk.ast.expressions.ListExp;
import com.defano.hypertalk.ast.expressions.containers.PartExp;
import com.defano.hypertalk.ast.model.Value;
import com.defano.hypertalk.ast.preemptions.Preemption;
import com.defano.hypertalk.ast.statements.Command;
import com.defano.hypertalk.exception.HtException;
import com.defano.hypertalk.exception.HtSemanticException;
import com.defano.wyldcard.runtime.context.ExecutionContext;
import com.defano.wyldcard.runtime.interpreter.Interpreter;
import org.antlr.v4.runtime.ParserRuleContext;

import java.util.ArrayList;

public class SendCmd extends Command {

    public final Expression part;
    public final Expression message;

    public SendCmd(ParserRuleContext context, Expression part, Expression message) {
        super(context, "send");

        this.part = part;
        this.message = message;
    }

    public void onExecute(ExecutionContext context) throws HtException, Preemption {
        PartExp recipient = part.factor(context, PartExp.class, new HtSemanticException("Cannot send a message to that."));
        MessageCmd messageCmd = synthesizeMessageCmd(context, message.evaluate(context).toString());

        messageCmd.setMessageRecipient(recipient);
        messageCmd.execute(context);
    }

    private MessageCmd synthesizeMessageCmd(ExecutionContext context, String text) {
        String message = text.trim().split("\\s")[0];
        ArrayList<Value> arguments = new ArrayList<>();

        for (String component : text.trim().substring(message.length()).split(",")) {
            arguments.add(Interpreter.blockingEvaluate(component.trim(), context));
        }

        return new MessageCmd(null, message, ListExp.fromValues(null, arguments.toArray(new Value[]{})));
    }

}
