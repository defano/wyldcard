package com.defano.wyldcard.window.layout;

import com.defano.hypertalk.ast.model.Value;
import com.defano.hypertalk.exception.HtException;
import com.defano.wyldcard.awt.MarkdownComboBox;
import com.defano.wyldcard.debug.message.HandlerInvocation;
import com.defano.wyldcard.debug.message.HandlerInvocationCache;
import com.defano.wyldcard.debug.message.HandlerInvocationObserver;
import com.defano.wyldcard.runtime.ExecutionContext;
import com.defano.wyldcard.window.WyldCardWindow;
import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import com.intellij.uiDesigner.core.Spacer;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class MessageWatcher extends WyldCardWindow<Object> implements HandlerInvocationObserver {

    private static final MessageWatcher instance = new MessageWatcher();

    private JPanel windowPanel;
    private JCheckBox suppressIdleCheckBox;
    private JTable messagesTable;
    private JScrollPane scrollPane;
    private JCheckBox suppressUnusedCheckBox;
    private MarkdownComboBox threadDropDown;
    private JCheckBox showOnlyMessageTargetCheckBox;
    private JButton trashButton;

    private DefaultComboBoxModel<String> threadDropDownModel = new DefaultComboBoxModel<>();
    private DefaultTableModel model = new DefaultTableModel();

    private MessageWatcher() {

        suppressIdleCheckBox.addActionListener(a -> invalidateData());
        suppressUnusedCheckBox.addActionListener(a -> invalidateData());
        threadDropDown.addActionListener(a -> invalidateData());
        showOnlyMessageTargetCheckBox.addActionListener(a -> invalidateData());

        model.setColumnCount(3);
        model.setColumnIdentifiers(new Object[]{"Thread", "Message", "Recipient"});

        threadDropDownModel.addElement("All threads");
        threadDropDownModel.addElement("---");
        threadDropDown.setModel(threadDropDownModel);

        messagesTable.setDefaultRenderer(Object.class, new HandlerInvocationCellRenderer());
        messagesTable.setModel(model);

        //noinspection ResultOfMethodCallIgnored
        getWindowVisibleProvider().subscribe(isVisible -> {
            if (isVisible) {
                HandlerInvocationCache.getInstance().addObserver(MessageWatcher.this);
            } else {
                HandlerInvocationCache.getInstance().removeObserver(MessageWatcher.this);
            }
        });

        trashButton.addActionListener(e -> {
            HandlerInvocationCache.getInstance().clear();
            invalidateData();
        });
    }

    public static MessageWatcher getInstance() {
        return instance;
    }

    private void smartScroll() {
        JScrollBar scrollBar = scrollPane.getVerticalScrollBar();
        int extent = scrollBar.getModel().getExtent();
        int maximum = scrollBar.getModel().getMaximum();

        if (maximum - (extent + scrollBar.getValue()) < 50) {
            messagesTable.scrollRectToVisible(messagesTable.getCellRect(messagesTable.getRowCount() - 1, 0, true));
        }
    }

    private void invalidateData() {
        model.setNumRows(0);

        if (threadDropDown.getSelectedIndex() == 0) {
            HandlerInvocationCache.getInstance().getInvocationHistory().stream()
                    .filter(i -> !suppressUnusedCheckBox.isSelected() || i.isMessageHandled())
                    .filter(i -> !suppressIdleCheckBox.isSelected() || !i.isPeriodicMessage())
                    .filter(i -> !showOnlyMessageTargetCheckBox.isSelected() || i.isTarget())
                    .forEach(i -> model.addRow(new Object[]{i.getThread(), i, i.getRecipient().getHyperTalkIdentifier(new ExecutionContext())}));
        } else {
            HandlerInvocationCache.getInstance().getInvocationHistory(String.valueOf(threadDropDown.getSelectedItem())).stream()
                    .filter(i -> !suppressUnusedCheckBox.isSelected() || i.isMessageHandled())
                    .filter(i -> !suppressIdleCheckBox.isSelected() || !i.isPeriodicMessage())
                    .filter(i -> !showOnlyMessageTargetCheckBox.isSelected() || i.isTarget())
                    .forEach(i -> model.addRow(new Object[]{i.getThread(), i, i.getRecipient().getHyperTalkIdentifier(new ExecutionContext())}));
        }
    }

    private void appendInvocation(HandlerInvocation i) {
        if ((!suppressUnusedCheckBox.isSelected() || i.isMessageHandled()) &&
                (!suppressIdleCheckBox.isSelected() || !i.isPeriodicMessage()) &&
                (!showOnlyMessageTargetCheckBox.isSelected() || i.isTarget())) {
            model.addRow(new Object[]{i.getThread(), i, i.getRecipient().getHyperTalkIdentifier(new ExecutionContext())});
        }
    }

    private boolean hasThread(String threadName) {
        for (int index = 0; index < threadDropDownModel.getSize(); index++) {
            if (threadDropDownModel.getElementAt(index).equalsIgnoreCase(threadName)) {
                return true;
            }
        }

        return false;
    }

    @Override
    public JComponent getWindowPanel() {
        return windowPanel;
    }

    @Override
    public void bindModel(Object data) {
        // Nothing to do
    }

    @Override
    public void onHandlerInvoked(HandlerInvocation invocation) {
        if (isVisible()) {
            if (!hasThread(invocation.getThread())) {
                threadDropDownModel.addElement(invocation.getThread());
            }

            if (threadDropDown.getSelectedIndex() == 0 || String.valueOf(threadDropDown.getSelectedItem()).equalsIgnoreCase(invocation.getThread())) {
                appendInvocation(invocation);
                smartScroll();
            }
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
        windowPanel = new JPanel();
        windowPanel.setLayout(new GridLayoutManager(3, 5, new Insets(10, 10, 10, 10), -1, -1));
        windowPanel.setPreferredSize(new Dimension(482, 275));
        scrollPane = new JScrollPane();
        windowPanel.add(scrollPane, new GridConstraints(1, 0, 1, 5, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        messagesTable = new JTable();
        messagesTable.setEnabled(false);
        messagesTable.setFillsViewportHeight(false);
        Font messagesTableFont = this.$$$getFont$$$("Monaco", Font.PLAIN, 12, messagesTable.getFont());
        if (messagesTableFont != null) messagesTable.setFont(messagesTableFont);
        scrollPane.setViewportView(messagesTable);
        suppressIdleCheckBox = new JCheckBox();
        suppressIdleCheckBox.setSelected(true);
        suppressIdleCheckBox.setText("Hide repetitive messages");
        suppressIdleCheckBox.setToolTipText("When checked, messages that are sent periodically (like 'idle') will be supressed.");
        windowPanel.add(suppressIdleCheckBox, new GridConstraints(2, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        suppressUnusedCheckBox = new JCheckBox();
        suppressUnusedCheckBox.setText("Hide ignored messages");
        suppressUnusedCheckBox.setToolTipText("When checked, only messages for which a handler exist will be shown.");
        windowPanel.add(suppressUnusedCheckBox, new GridConstraints(2, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        showOnlyMessageTargetCheckBox = new JCheckBox();
        showOnlyMessageTargetCheckBox.setLabel("Show only message target");
        showOnlyMessageTargetCheckBox.setSelected(true);
        showOnlyMessageTargetCheckBox.setText("Show only message target");
        showOnlyMessageTargetCheckBox.setToolTipText("When checked only messages delivered to their target (first part in the message passing order) will be shown.");
        windowPanel.add(showOnlyMessageTargetCheckBox, new GridConstraints(0, 1, 1, 2, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final Spacer spacer1 = new Spacer();
        windowPanel.add(spacer1, new GridConstraints(2, 2, 1, 3, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, null, null, 0, false));
        trashButton = new JButton();
        trashButton.setIcon(new ImageIcon(getClass().getResource("/icons/trash.png")));
        trashButton.setText("");
        windowPanel.add(trashButton, new GridConstraints(0, 4, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final Spacer spacer2 = new Spacer();
        windowPanel.add(spacer2, new GridConstraints(0, 3, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, null, null, 0, false));
        threadDropDown = new MarkdownComboBox();
        windowPanel.add(threadDropDown, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
    }

    /**
     * @noinspection ALL
     */
    private Font $$$getFont$$$(String fontName, int style, int size, Font currentFont) {
        if (currentFont == null) return null;
        String resultName;
        if (fontName == null) {
            resultName = currentFont.getName();
        } else {
            Font testFont = new Font(fontName, Font.PLAIN, 10);
            if (testFont.canDisplay('a') && testFont.canDisplay('1')) {
                resultName = fontName;
            } else {
                resultName = currentFont.getName();
            }
        }
        return new Font(resultName, style >= 0 ? style : currentFont.getStyle(), size >= 0 ? size : currentFont.getSize());
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return windowPanel;
    }

    private class HandlerInvocationCellRenderer extends DefaultTableCellRenderer {
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

            ExecutionContext context = new ExecutionContext();
            if (value instanceof HandlerInvocation) {
                HandlerInvocation invocation = (HandlerInvocation) value;

                List<Value> arguments = new ArrayList<>();
                try {
                    arguments = invocation.evaluateArguments(context);
                } catch (HtException e) {
                    // Nothing to do
                }

                StringBuilder message = new StringBuilder(invocation.getMessageName());

                // Indent message depth of call stack
                for (int index = 0; index < invocation.getStackDepth() - 1; index++) {
                    message.insert(0, "  ");
                }

                // Append arguments
                for (int index = 0; index < arguments.size(); index++) {
                    String argument = arguments.get(index).toString();

                    message.append(" ");

                    // Quote non-empty arguments
                    if (!argument.isEmpty()) {
                        message.append("\"");
                    }

                    message.append(argument);

                    if (!argument.isEmpty()) {
                        message.append("\"");
                    }

                    if (index < arguments.size() - 1) {
                        message.append(",");
                    }
                }

                setText(message.toString());

                // Highlight handled messages in italics
                if (invocation.isMessageHandled()) {
                    setFont(getFont().deriveFont(Font.ITALIC));
                }
            }

            setToolTipText(getText());

            return this;
        }
    }
}
