package com.defano.wyldcard.awt;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class MarkdownComboBox extends JComboBox<String> {

    public MarkdownComboBox() {
        setRenderer(new ComboBoxRenderer());
        addActionListener(new SeparatorSelectionDelegate());
    }

    private class SeparatorSelectionDelegate implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {

            // Don't allow selection of separator elements; select previous value
            if (String.valueOf(getSelectedItem()).startsWith("---")) {
                setSelectedIndex(Math.min(0, getSelectedIndex() - 1));
            }
        }
    }

    private class ComboBoxRenderer extends DefaultListCellRenderer {

        @Override
        public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
            super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);

            if (String.valueOf(value).startsWith("---")) {
                return new JSeparator();
            }

            if (String.valueOf(value).startsWith("*") && String.valueOf(value).endsWith("*")) {
                setText(getText().substring(1, getText().length() - 2));
                setFont(getFont().deriveFont(Font.BOLD));
            }

            if (String.valueOf(value).startsWith("_") && String.valueOf(value).endsWith("_")) {
                setText(getText().substring(1, getText().length() - 2));
                setFont(getFont().deriveFont(Font.ITALIC));
            }

            return this;
        }
    }

}
