package com.defano.wyldcard.window;

import com.defano.hypertalk.ast.model.Value;
import com.defano.hypertalk.exception.HtSemanticException;
import com.defano.wyldcard.WyldCard;
import com.defano.wyldcard.part.stack.StackModel;
import com.defano.wyldcard.runtime.ExecutionContext;
import com.defano.wyldcard.thread.Invoke;
import com.defano.wyldcard.util.Hashable;
import com.google.inject.Inject;
import com.google.inject.Singleton;

import javax.swing.*;
import java.awt.*;
import java.io.File;

@Singleton
public class WyldCardDialogManager implements DialogManager, Hashable {

    private static final Value CANCEL_RESPONSE = new Value("Cancel");

    @Inject
    private WindowManager windowManager;

    @Override
    public DialogResponse answer(ExecutionContext context, Value msg, Value choice1, Value choice2, Value choice3) {

        return Invoke.onDispatch(() -> {
            Component parent = windowManager.getWindowForStack(context, context.getCurrentStack()).getWindowPanel();
            Object[] choices;

            if (choice1 != null && choice2 != null && choice3 != null) {
                choices = new Object[]{choice1, choice2, choice3};
            }
            else if (choice1 != null && choice2 != null) {
                choices = new Object[]{choice1, choice2};
            }
            else if (choice1 != null) {
                choices = new Object[]{choice1};
            } else {
                throw new IllegalArgumentException("Must provide first choice.");
            }

            int choice = JOptionPane.showOptionDialog(parent, msg, "Answer",
                    JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, null, choices, choices[0]);

            switch (choice) {
                case 0:     return new DialogResponse(choice1, null);
                case 1:     return new DialogResponse(choice2, null);
                case 2:     return new DialogResponse(choice3, null);
                default:    return new DialogResponse(new Value(), null);
            }
        });
    }

    @Override
    public DialogResponse answerFile(ExecutionContext context, Value promptString, Value fileFilter) {

        return Invoke.onDispatch(() -> {
            FileDialog fd = new FileDialog(
                    WyldCard.getInstance().getWindowManager().getWindowForStack(context, context.getCurrentStack()).getWindow(),
                    promptString.toString(),
                    FileDialog.LOAD);

            fd.setMultipleMode(false);

            // TODO: Support for file types and signatures, not just extensions
            if (fileFilter != null) {
                String fileExtension = fileFilter.toString();
                fd.setFilenameFilter((dir, name) -> name.endsWith(fileExtension));
            }

            fd.setVisible(true);

            if (fd.getFiles().length > 0) {
                return new DialogResponse(null, new Value(fd.getFiles()[0].getAbsolutePath()));
            } else {
                return new DialogResponse(null, null);
            }
        });
    }

    @Override
    public DialogResponse ask(ExecutionContext context, Value question, Value suggestion) {

        return Invoke.onDispatch(() -> {
            Component parent = windowManager.getWindowForStack(context, context.getCurrentStack()).getWindowPanel();

            String result = (String) JOptionPane.showInputDialog(
                    parent,
                    question,
                    "Ask",
                    JOptionPane.PLAIN_MESSAGE,
                    null,
                    null,
                    suggestion);

            return result == null ?
                    new DialogResponse(CANCEL_RESPONSE, new Value()) :
                    new DialogResponse(new Value(), new Value(result));
        });
    }

    @Override
    public DialogResponse askFile(ExecutionContext context, Value prompt, Value file) {

        return Invoke.onDispatch(() -> {
            FileDialog fd = new FileDialog(
                    windowManager.getWindowForStack(context, context.getCurrentStack()).getWindow(),
                    prompt.toString(),
                    FileDialog.SAVE);

            if (file != null) {
                fd.setFile(file.toString());
            }

            fd.setVisible(true);
            if (fd.getFiles().length > 0) {
                File f = fd.getFiles()[0];
                String path = f.getAbsolutePath().endsWith(StackModel.FILE_EXTENSION) ?
                        f.getAbsolutePath() :
                        f.getAbsolutePath() + StackModel.FILE_EXTENSION;

                return new DialogResponse(new Value(), new Value(path));
            } else {
                return new DialogResponse(CANCEL_RESPONSE, null);
            }
        });
    }

    @Override
    public DialogResponse askPassword(ExecutionContext context, Value question, Value suggestion, boolean hashResponse) throws HtSemanticException {

        return Invoke.onDispatch(() -> {
            Component parent = windowManager.getWindowForStack(context, context.getCurrentStack()).getWindowPanel();

            JPanel panel = new JPanel();
            panel.setLayout(new GridLayout(2, 1));
            JLabel label = new JLabel(question.toString());
            JPasswordField pass = new JPasswordField();
            pass.setText(suggestion.toString());
            panel.add(label);
            panel.add(pass);

            int result = JOptionPane.showConfirmDialog(parent, panel, "Enter Password", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

            if (result == JOptionPane.CANCEL_OPTION) {
                return new DialogResponse(CANCEL_RESPONSE, null);
            } else {
                String password = hashResponse ? calculateSha256Hash(new String(pass.getPassword())) : new String(pass.getPassword());
                return new DialogResponse(new Value(), new Value(password));
            }
        }, HtSemanticException.class);
    }
}
