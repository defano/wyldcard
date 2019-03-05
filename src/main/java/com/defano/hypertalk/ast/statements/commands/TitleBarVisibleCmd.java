package com.defano.hypertalk.ast.statements.commands;

import com.defano.wyldcard.runtime.context.ExecutionContext;
import com.defano.hypertalk.ast.statements.Command;
import com.defano.wyldcard.window.WindowManager;
import com.google.inject.Inject;
import org.antlr.v4.runtime.ParserRuleContext;

import javax.swing.*;

public class TitleBarVisibleCmd extends Command {

    @Inject
    private WindowManager windowManager;

    private final boolean visibility;

    public TitleBarVisibleCmd(ParserRuleContext context, boolean visibility) {
        super(context, visibility ? "show" : "hide");
        this.visibility = visibility;
    }

    @Override
    protected void onExecute(ExecutionContext context) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = windowManager.getWindowForStack(context, context.getCurrentStack()).getWindow();

            frame.dispose();
            frame.setUndecorated(!visibility);
            frame.pack();
            frame.setVisible(true);
        });
    }
}
