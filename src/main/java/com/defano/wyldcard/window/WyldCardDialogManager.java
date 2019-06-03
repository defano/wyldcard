package com.defano.wyldcard.window;

import com.defano.hypertalk.ast.model.Value;
import com.defano.wyldcard.WyldCard;
import com.defano.wyldcard.runtime.ExecutionContext;
import com.google.inject.Inject;
import com.google.inject.Singleton;

import javax.swing.*;
import java.awt.*;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;

@Singleton
public class WyldCardDialogManager implements DialogManager {

    @Inject
    private WindowManager windowManager;

    public Value answer(ExecutionContext context, Value msg, Value choice1, Value choice2, Value choice3) {

        CountDownLatch latch = new CountDownLatch(1);
        AtomicInteger choice = new AtomicInteger();

        SwingUtilities.invokeLater(() -> {
            Component parent = windowManager.getWindowForStack(context, context.getCurrentStack()).getWindowPanel();
            Object[] choices = null;

            if (choice1 != null && choice2 != null && choice3 != null) {
                choices = new Object[]{choice1, choice2, choice3};
            }
            else if (choice1 != null && choice2 != null) {
                choices = new Object[]{choice1, choice2};
            }
            else if (choice1 != null) {
                choices = new Object[]{choice1};
            }

            choice.set(JOptionPane.showOptionDialog(parent, msg, "Answer",
                    JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, null, choices, choices[0]));

            latch.countDown();
        });

        try {
            latch.await();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        switch (choice.get()) {
            case 0:     return choice1;
            case 1:     return choice2;
            case 2:     return choice3;
            default:    return new Value();
        }
    }

    public Value answerFile(ExecutionContext context, Value promptString, Value fileFilter) {
        FileDialog fd = new FileDialog(WyldCard.getInstance().getWindowManager().getWindowForStack(context, context.getCurrentStack()).getWindow(), promptString.toString(), FileDialog.LOAD);
        fd.setMultipleMode(false);

        // TODO: Support for file types and signatures, not just extensions
        if (fileFilter != null) {
            String fileExtension = fileFilter.toString();
            fd.setFilenameFilter((dir, name) -> name.endsWith(fileExtension));
        }

        fd.setVisible(true);

        if (fd.getFiles().length > 0) {
            return new Value(fd.getFiles()[0].getAbsolutePath());
//            context.setIt(new Value(fd.getFiles()[0].getAbsolutePath()));
//            context.setResult(new Value());
        } else {
            return null;
//            context.setIt(new Value());
//            context.setResult(new Value("Cancel"));
        }
    }
}
