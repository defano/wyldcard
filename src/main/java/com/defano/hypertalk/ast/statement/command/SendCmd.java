package com.defano.hypertalk.ast.statement.command;

import com.defano.hypertalk.ast.expression.Expression;
import com.defano.hypertalk.ast.expression.container.PartExp;
import com.defano.hypertalk.ast.preemption.Preemption;
import com.defano.hypertalk.ast.statement.Command;
import com.defano.hypertalk.exception.HtException;
import com.defano.hypertalk.exception.HtSemanticException;
import com.defano.wyldcard.message.Message;
import com.defano.wyldcard.message.MessageBuilder;
import com.defano.wyldcard.runtime.ExecutionContext;
import org.antlr.v4.runtime.ParserRuleContext;

public class SendCmd extends Command {

    public final Expression partExpr;
    public final Expression messageExpr;

    public SendCmd(ParserRuleContext context, Expression partExpr, Expression messageExpr) {
        super(context, "send");

        this.partExpr = partExpr;
        this.messageExpr = messageExpr;
    }

    public void onExecute(ExecutionContext context) throws HtException, Preemption {
        PartExp recipient = partExpr.factor(context, PartExp.class, new HtSemanticException("Cannot send a message to that."));
        Message message = MessageBuilder.fromString(messageExpr.evaluate(context).toString());

        new MessageCmd(getParserContext(), recipient, message).execute(context);
    }

}
