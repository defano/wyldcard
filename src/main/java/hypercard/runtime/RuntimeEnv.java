/**
 * RuntimeEnv.java
 * @author matt.defano@gmail.com
 * 
 * The HyperCard runtime environment; this is the program's main class and is
 * responsible for initializing the HyperCard window, tracking mouse changes
 * and reporting exceptions to the user.
 */

package hypercard.runtime;

import hypercard.context.GlobalContext;
import hypercard.gui.util.ModifierKeyListener;
import hypercard.gui.util.MouseListener;
import hypercard.parts.CardPart;
import hypercard.parts.model.StackModel;
import hypercard.parts.model.StackModelObserver;
import hypertalk.ast.common.Value;

import javax.swing.*;
import java.awt.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class RuntimeEnv implements StackModelObserver {

    private static RuntimeEnv _instance;
    private static ExecutorService messageBoxExecutor = Executors.newSingleThreadExecutor();

    private StackModel stack;

    public static void main(String argv[]) {
        // Display the frame's menu as the Mac OS menubar
        System.setProperty("apple.laf.useScreenMenuBar", "true");
        System.setProperty("com.apple.macos.useScreenMenuBar", "true" );

        _instance = new RuntimeEnv();
    }

    private RuntimeEnv() {
        /*
         * Use this operating systems look and feel for our user interface. If
         * this causes an exception, just ignore it (it's not the end of the
         * world if we can't use the native look anyway).
         */
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            System.out.println("Unable to set the UI look and feel");
        }

        stack = StackModel.newStack("Untitled");
        stack.addObserver(this);

        ModifierKeyListener.start();
        MouseListener.start();

        SwingUtilities.invokeLater(() -> WindowManager.start());
    }

    public static RuntimeEnv getRuntimeEnv() {
        return _instance;
    }


    public StackModel getStack () { return stack; }

    public void setStack (StackModel model) {
        SwingUtilities.invokeLater(() -> {
            stack = model;
            stack.addObserver(RuntimeEnv.this);
            WindowManager.getStackWindow().setDisplayedCard(stack.getCurrentCard());
        });
    }

    public CardPart getCard () {
        return stack.getCurrentCard();
    }

    public void setMsgBoxText(Object theMsg) {
        SwingUtilities.invokeLater(() -> WindowManager.getMessageWindow().setMsgBoxText(theMsg.toString()));
    }

    public String getMsgBoxText() {
        return WindowManager.getMessageWindow().getMsgBoxText();
    }

    public void doMsgBoxText() {
        messageBoxExecutor.submit(() -> {
            try {
                if (!getMsgBoxText().trim().isEmpty()) {
                    Interpreter.executeString(null, getMsgBoxText()).get();
                    RuntimeEnv.getRuntimeEnv().setMsgBoxText(GlobalContext.getContext().getIt());
                }
            } catch (Exception e) {
                RuntimeEnv.getRuntimeEnv().dialogSyntaxError(e);
            }
        });
    }

    public void dialogSyntaxError(Exception e) {
        SwingUtilities.invokeLater(() -> JOptionPane.showMessageDialog(WindowManager.getStackWindow().getWindowPanel(), e.getMessage()));
        e.printStackTrace();
    }

    @Override
    public void onCurrentCardChanged(CardPart newCard) {
        SwingUtilities.invokeLater(() -> WindowManager.getStackWindow().setDisplayedCard(newCard));
    }
}
