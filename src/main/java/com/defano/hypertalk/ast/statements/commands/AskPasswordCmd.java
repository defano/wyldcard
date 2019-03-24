package com.defano.hypertalk.ast.statements.commands;

import com.defano.hypertalk.ast.expressions.Expression;
import com.defano.hypertalk.ast.expressions.LiteralExp;
import com.defano.hypertalk.ast.model.Value;
import com.defano.hypertalk.ast.preemptions.Preemption;
import com.defano.hypertalk.ast.statements.Command;
import com.defano.hypertalk.exception.HtException;
import com.defano.hypertalk.exception.HtSemanticException;
import com.defano.wyldcard.runtime.context.ExecutionContext;
import com.defano.wyldcard.util.Hashable;
import com.defano.wyldcard.window.WindowManager;
import com.google.inject.Inject;
import org.antlr.v4.runtime.ParserRuleContext;

import javax.swing.*;
import java.awt.*;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicReference;

public class AskPasswordCmd extends Command implements Hashable {

    private final boolean isClear;
    private final Expression promptExpr;
    private final Expression passwordExpr;

    @Inject
    private WindowManager windowManager;

    public AskPasswordCmd(ParserRuleContext context, boolean isClear, Expression promptExpr) {
        this(context, isClear, promptExpr, new LiteralExp(null));
    }

    public AskPasswordCmd(ParserRuleContext context, boolean isClear, Expression promptExpr, Expression passwordExpr) {
        super(context, "ask");
        this.isClear = isClear;
        this.promptExpr = promptExpr;
        this.passwordExpr = passwordExpr;
    }

    @Override
    protected void onExecute(ExecutionContext context) throws HtException, Preemption {
        ask(context, promptExpr.evaluate(context), passwordExpr.evaluate(context));
    }

    private void ask(ExecutionContext context, Value question, Value suggestion) throws HtSemanticException {

        CountDownLatch latch = new CountDownLatch(1);
        AtomicReference<String> password = new AtomicReference<>();

        SwingUtilities.invokeLater(() -> {
            Component parent = windowManager.getWindowForStack(context, context.getCurrentStack()).getWindowPanel();

            JPanel panel = new JPanel();
            panel.setLayout(new GridLayout(2, 1));
            JLabel label = new JLabel(question.toString());
            JPasswordField pass = new JPasswordField();
            pass.setText(suggestion.toString());
            panel.add(label);
            panel.add(pass);

            int result = JOptionPane.showConfirmDialog(parent, panel, "Enter Password", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
            password.set(new String(pass.getPassword()));

            if (result == JOptionPane.CANCEL_OPTION) {
                context.setResult(new Value("Cancel"));
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

        if (isClear) {
            context.setIt(new Value(password.get()));
        } else {
            context.setIt(calculateSha256Hash(password.get()));
        }
    }

}
