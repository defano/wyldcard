package hypercard.gui.window;

import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import hypercard.context.GlobalContext;
import hypercard.gui.HyperCardWindow;
import hypercard.parts.CardPart;
import hypercard.runtime.Interpreter;
import hypercard.runtime.RuntimeEnv;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class StackWindow implements HyperCardWindow {

    private CardPart card;

    private JTextField messageBox;
    private JButton executeButton;
    private JPanel cardPanel;
    private JPanel stackWindow;

    public StackWindow() {
        executeButton.addActionListener(e -> {
            executeMessageBox();
        });

        messageBox.addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
                super.keyTyped(e);
                if (e.getKeyChar() == '\n') {
                    executeMessageBox();
                }
            }
        });
    }

    public CardPart getCurrentCard() {
        return card;
    }

    public void setCurrentCard(CardPart card) {
        this.card = card;
        cardPanel.removeAll();
        cardPanel.add(card);

        stackWindow.invalidate();
        stackWindow.validate();
        stackWindow.repaint();
    }

    public void setMsgBoxText(String text) {
        messageBox.setText(text);
    }

    public String getMsgBoxText() {
        return messageBox.getText();
    }

    private void executeMessageBox() {
        try {
            Interpreter.execute(messageBox.getText());
            RuntimeEnv.getRuntimeEnv().setMsgBoxText(GlobalContext.getContext().getIt());
        } catch (Exception e) {
            RuntimeEnv.getRuntimeEnv().dialogSyntaxError(e);
        }
    }


    @Override
    public JPanel getWindowPanel() {
        return stackWindow;
    }

    @Override
    public void bindModel(Object data) {
        if (data instanceof CardPart) {
            setCurrentCard((CardPart) data);
        } else {
            throw new RuntimeException("Bug! Don't know how to bind data class to window." + data);
        }
    }

    {
// GUI initializer generated by IntelliJ IDEA GUI Designer
// >>> IMPORTANT!! <<<
// DO NOT EDIT OR ADD ANY CODE HERE!
        $$$setupUI$$$();
    }

    /**
     * Method generated by IntelliJ IDEA GUI Designer
     * >>> IMPORTANT!! <<<
     * DO NOT edit this method OR call it in your code!
     *
     * @noinspection ALL
     */
    private void $$$setupUI$$$() {
        stackWindow = new JPanel();
        stackWindow.setLayout(new GridLayoutManager(2, 1, new Insets(10, 10, 10, 10), -1, -1));
        final JPanel panel1 = new JPanel();
        panel1.setLayout(new GridLayoutManager(1, 2, new Insets(0, 0, 0, 0), -1, -1));
        stackWindow.add(panel1, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        panel1.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), "Message Box"));
        messageBox = new JTextField();
        panel1.add(messageBox, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        executeButton = new JButton();
        executeButton.setText("Execute");
        panel1.add(executeButton, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        cardPanel = new JPanel();
        cardPanel.setLayout(new BorderLayout(0, 0));
        stackWindow.add(cardPanel, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, new Dimension(640, 480), null, 0, false));
        cardPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), "Card"));
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return stackWindow;
    }
}
