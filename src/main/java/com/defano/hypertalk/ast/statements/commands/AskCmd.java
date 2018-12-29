package com.defano.hypertalk.ast.statements.commands;

import com.defano.wyldcard.runtime.context.ExecutionContext;
import com.defano.hypertalk.ast.expressions.Expression;
import com.defano.hypertalk.ast.expressions.LiteralExp;
import com.defano.hypertalk.ast.model.Value;
import com.defano.hypertalk.ast.statements.Command;
import com.defano.hypertalk.exception.HtException;
import com.defano.wyldcard.window.WindowManager;
import com.google.inject.Inject;
import org.antlr.v4.runtime.ParserRuleContext;

import javax.swing.*;
import java.awt.*;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicReference;

public class AskCmd extends Command {

    @Inject
    private WindowManager windowManager;

    private final Expression question;
    private final Expression suggestion;
    
    public AskCmd(ParserRuleContext context, Expression question, Expression suggestion) {
        super(context, "ask");

        this.question = question;
        this.suggestion = suggestion;
    }
    
    public AskCmd(ParserRuleContext context,  Expression question) {
        super(context, "ask");

        this.question = question;
        this.suggestion = new LiteralExp(context, "");
    }
    
    public void onExecute(ExecutionContext context) throws HtException {
        if (suggestion != null)
            ask(context, question.evaluate(context), suggestion.evaluate(context));
        else
            ask(context, question.evaluate(context));
    }
    
    private void ask(ExecutionContext context, Value question, Value suggestion) {

        CountDownLatch latch = new CountDownLatch(1);
        AtomicReference<String> result = new AtomicReference<>();
        
        SwingUtilities.invokeLater(() -> {
            Component parent = windowManager.getWindowForStack(context, context.getCurrentStack()).getWindowPanel();

            result.set((String) JOptionPane.showInputDialog(
                    parent,
                    question,
                    "Ask",
                    JOptionPane.PLAIN_MESSAGE,
                    null,
                    null,
                    suggestion));

            if (result.get() == null) {
                context.setResult(new Value("Cancel"));
                result.set("");
            } else {
                context.setResult(new Value());
            }

            latch.countDown();
        });

        try {
            latch.await();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        context.setIt(new Value(result.get()));
    }
    
    private void ask(ExecutionContext context, Value question) {
        ask(context, question, new Value());
    }
}
