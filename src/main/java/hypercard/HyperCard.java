/**
 * HyperCard.java
 * @author matt.defano@gmail.com
 * 
 * The HyperCard runtime environment; this is the program's main class and is
 * responsible for initializing the HyperCard window, tracking mouse changes
 * and reporting exceptions to the user.
 */

package hypercard;

import hypercard.context.GlobalContext;
import hypercard.context.ToolsContext;
import hypercard.gui.util.ModifierKeyListener;
import hypercard.gui.util.MouseListener;
import hypercard.paint.model.PaintToolType;
import hypercard.parts.CardPart;
import hypercard.parts.model.StackModel;
import hypercard.parts.model.StackModelObserver;
import hypercard.runtime.Interpreter;
import hypercard.runtime.WindowManager;

import javax.swing.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class HyperCard implements StackModelObserver {

    private static HyperCard _instance;
    private static ExecutorService messageBoxExecutor = Executors.newSingleThreadExecutor();

    private StackModel stack;

    public static void main(String argv[]) {
        // Display the frame's menu as the Mac OS menubar
        System.setProperty("apple.laf.useScreenMenuBar", "true");
        System.setProperty("com.apple.macos.useScreenMenuBar", "true" );
        System.setProperty("com.apple.mrj.application.apple.menu.about.name", "HyperCard");
        System.setProperty("apple.awt.application.name", "HyperCard");

        _instance = new HyperCard();
    }

    private HyperCard() {

        try {
            // Use this operating systems look and feel for our user interface.
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            // Nothing to do
        }

        // Create a new stack to work on.
        stack = StackModel.newStack("Untitled");
        stack.addObserver(this);

        // Fire up the key and mouse listeners
        ModifierKeyListener.start();
        MouseListener.start();

        // Window manager expects this object to be fully initialized before it can start, thus, we can't invoke
        // directly from the constructor
        SwingUtilities.invokeLater(() -> {
            WindowManager.start();
            ToolsContext.getInstance().setSelectedToolType(PaintToolType.ARROW);
        });
    }

    public static HyperCard getRuntimeEnv() {
        return _instance;
    }


    public StackModel getStack () { return stack; }

    public void setStack (StackModel model) {
        SwingUtilities.invokeLater(() -> {
            stack = model;
            stack.addObserver(HyperCard.this);
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
                    HyperCard.getRuntimeEnv().setMsgBoxText(GlobalContext.getContext().getIt());
                }
            } catch (Exception e) {
                HyperCard.getRuntimeEnv().dialogSyntaxError(e);
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
