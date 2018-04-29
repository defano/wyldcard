package com.defano.wyldcard.window.forms;

import com.defano.hypertalk.ast.model.Value;
import com.defano.wyldcard.aspect.RunOnDispatch;
import com.defano.wyldcard.runtime.StackFrame;
import com.defano.wyldcard.runtime.context.ExecutionContext;
import com.defano.wyldcard.runtime.symbol.SymbolObserver;
import com.defano.wyldcard.runtime.symbol.SymbolTable;
import com.defano.wyldcard.window.WyldCardFrame;
import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import com.intellij.uiDesigner.core.Spacer;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.Collection;

public class VariableWatcher extends WyldCardFrame implements SymbolObserver {

    private JPanel windowPanel;
    private JTable variablesTable;
    private JTextArea variableEditor;
    private JLabel contextLabel;

    private DefaultTableModel tableModel = new DefaultTableModel();
    private SymbolTable variables;
    private Collection<String> globalsInScope;

    public VariableWatcher() {
        setWatchedVariables(null, null);

        tableModel.setColumnCount(2);
        tableModel.setColumnIdentifiers(new Object[]{"Variable", "Value"});

        variablesTable.setDefaultEditor(Object.class, null);
        variablesTable.setModel(tableModel);
        variablesTable.getSelectionModel().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        variablesTable.setDefaultRenderer(Object.class, new VariableCellRenderer());

        variablesTable.getSelectionModel().addListSelectionListener(e -> {
            variableEditor.setText(getSelectedVariableValue());
        });

        variableEditor.addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
                SwingUtilities.invokeLater(() -> variables.set(getSelectedVariableName(), new Value(variableEditor.getText())));
            }
        });
    }

    @Override
    @RunOnDispatch
    public void setVisible(boolean visible) {
        super.setVisible(visible);

        if (visible && variables != null) {
            this.variables.addObserver(this);
            invalidateTable();
        } else if (variables != null) {
            this.variables.removeObserver(this);
        }
    }

    public void setWatchGlobalVariables() {
        contextLabel.setText("Globals");
        setWatchedVariables(null, null);
    }

    public void setWatchedVariables(ExecutionContext context) {
        if (context.getStackDepth() == 0) {
            setWatchGlobalVariables();
        } else {
            StackFrame frame = context.getStackFrame();
            setWatchedVariables(frame.getVariables(), frame.getGlobalsInScope());
            contextLabel.setText(context.toString());
        }
    }

    /**
     * Sets the set of variables to be viewed in the variable watcher.
     *
     * @param variables      A symbol table of variables to be shown. When null, all global variables will be shown.
     * @param globalsInScope A collection of variable names that should be highlighted in italics to indicate they
     *                       represent in-scope globals. When null, all variables are assumed global.
     */
    @RunOnDispatch
    public void setWatchedVariables(SymbolTable variables, Collection<String> globalsInScope) {
        tableModel.setNumRows(0);

        if (variables == null) {
            variables = ExecutionContext.getGlobals();
        }

        if (this.variables != null) {
            this.variables.removeObserver(this);
        }

        this.variables = variables;
        this.globalsInScope = globalsInScope;

        if (isVisible()) {
            this.variables.addObserver(this);
        }

        invalidateTable();
    }

    @Override
    public void onSymbolChanged(SymbolTable symbolTable, String id, Value oldValue, Value newValue) {
        invalidateRow(id, oldValue, newValue);
    }

    @Override
    public JComponent getWindowPanel() {
        return windowPanel;
    }

    @Override
    public void bindModel(Object data) {
        // Nothing to do
    }

    @RunOnDispatch
    private void invalidateTable() {
        tableModel.setRowCount(0);
        for (String variableName : variables.getSymbols()) {
            tableModel.addRow(new Object[]{variableName, variables.get(variableName).stringValue()});
        }
    }

    @RunOnDispatch
    private void invalidateRow(String variable, Value oldValue, Value newValue) {
        if (oldValue == null) {
            tableModel.addRow(new Object[]{variable, newValue.stringValue()});
        } else {
            for (int index = 0; index < tableModel.getRowCount(); index++) {
                if (tableModel.getValueAt(index, 0).toString().equalsIgnoreCase(variable)) {
                    tableModel.setValueAt(newValue.stringValue(), index, 1);
                }
            }
        }
    }

    @RunOnDispatch
    private String getSelectedVariableName() {
        int selectedRow = variablesTable.getSelectedRow();

        if (selectedRow >= 0 && selectedRow < variablesTable.getModel().getRowCount()) {
            return variablesTable.getModel().getValueAt(selectedRow, 0).toString();
        } else {
            return "";
        }
    }

    @RunOnDispatch
    private String getSelectedVariableValue() {
        int selectedRow = variablesTable.getSelectedRow();

        if (selectedRow >= 0 && selectedRow < variablesTable.getModel().getRowCount()) {
            return variablesTable.getModel().getValueAt(selectedRow, 1).toString();
        } else {
            return "";
        }
    }

    private boolean isGlobalInScope(String id) {
        if (globalsInScope == null) {
            return true;
        }

        for (String thisGlobal : globalsInScope) {
            if (thisGlobal.equalsIgnoreCase(id)) {
                return true;
            }
        }

        return false;
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
        windowPanel.setLayout(new GridLayoutManager(3, 1, new Insets(10, 10, 10, 10), -1, -1));
        final JScrollPane scrollPane1 = new JScrollPane();
        windowPanel.add(scrollPane1, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        scrollPane1.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), null));
        variablesTable = new JTable();
        variablesTable.setEnabled(true);
        variablesTable.setPreferredScrollableViewportSize(new Dimension(300, 150));
        scrollPane1.setViewportView(variablesTable);
        final JScrollPane scrollPane2 = new JScrollPane();
        windowPanel.add(scrollPane2, new GridConstraints(2, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        scrollPane2.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), null));
        variableEditor = new JTextArea();
        variableEditor.setWrapStyleWord(false);
        scrollPane2.setViewportView(variableEditor);
        final JPanel panel1 = new JPanel();
        panel1.setLayout(new GridLayoutManager(1, 2, new Insets(0, 0, 0, 0), -1, -1));
        windowPanel.add(panel1, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        contextLabel = new JLabel();
        contextLabel.setEnabled(false);
        Font contextLabelFont = this.$$$getFont$$$(null, -1, -1, contextLabel.getFont());
        if (contextLabelFont != null) contextLabel.setFont(contextLabelFont);
        contextLabel.setText("Globals");
        panel1.add(contextLabel, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final Spacer spacer1 = new Spacer();
        panel1.add(spacer1, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, null, null, 0, false));
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

    private class VariableCellRenderer extends DefaultTableCellRenderer {
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

            if (globalsInScope == null || isGlobalInScope(table.getModel().getValueAt(0, column).toString())) {
                setFont(getFont().deriveFont(Font.ITALIC));
            }

            return this;
        }
    }

}
