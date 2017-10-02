/*
 * StatAskCmd
 * hypertalk-java
 *
 * Created by Matt DeFano on 2/19/17 3:11 PM.
 * Copyright © 2017 Matt DeFano. All rights reserved.
 */

/**
 * AskCmd.java
 * @author matt.defano@gmail.com
 * 
 * Implementation of the "ask" statement
 */

package com.defano.hypertalk.ast.commands;

import com.defano.hypercard.runtime.context.ExecutionContext;
import com.defano.hypercard.window.WindowManager;
import com.defano.hypertalk.ast.common.Value;
import com.defano.hypertalk.ast.expressions.LiteralExp;
import com.defano.hypertalk.ast.expressions.Expression;
import com.defano.hypertalk.ast.statements.Command;
import com.defano.hypertalk.exception.HtSemanticException;

import java.awt.*;
import java.util.concurrent.CountDownLatch;

import javax.swing.*;

public class AskCmd extends Command {

    public final Expression question;
    public final Expression suggestion;
    
    public AskCmd(Expression question, Expression suggestion) {
        super("ask");

        this.question = question;
        this.suggestion = suggestion;
    }
    
    public AskCmd(Expression question) {
        super("ask");

        this.question = question;
        this.suggestion = new LiteralExp("");
    }
    
    public void onExecute () throws HtSemanticException {
        if (suggestion != null)
            ask(question.evaluate(), suggestion.evaluate());
        else
            ask(question.evaluate());
    }
    
    private void ask (Value question, Value suggestion) {

        CountDownLatch latch = new CountDownLatch(1);

        SwingUtilities.invokeLater(() -> {
            Component parent = WindowManager.getStackWindow().getWindowPanel();

            String result = (String)JOptionPane.showInputDialog(
                    parent,
                    question,
                    "Ask",
                    JOptionPane.PLAIN_MESSAGE,
                    null,
                    null,
                    suggestion);

            if (result == null)
                result = "";

            ExecutionContext.getContext().setIt(new Value(result));
            latch.countDown();
        });

        try {
            latch.await();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
    
    private void ask (Value question) {
        ask(question, new Value());
    }
}
