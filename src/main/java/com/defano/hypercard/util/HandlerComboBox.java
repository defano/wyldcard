package com.defano.hypercard.util;

import com.google.common.collect.Lists;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

public class HandlerComboBox extends JComboBox<String> {

    private final static String SEPARATOR = "[---]";

    public interface HandlerComboBoxDelegate {
        Collection<String> getImplementedHandlers(HandlerComboBox theComboBox);
        Collection<String> getSystemMessages(HandlerComboBox theComboBox);
        void jumpToHandler(HandlerComboBox theComboBox, String handler);
    }

    private HandlerComboBoxDelegate delegate;
    private boolean ignoreChanges;
    private String lastSelection;

    public void setDelegate(HandlerComboBoxDelegate delegate) {
        this.delegate = delegate;

        setRenderer(new ComboBoxRenderer());
        addActionListener(new ItemSelectedActionDelegate());

        invalidateDataset();
    }

    public void setActiveHandler(String handler) {
        if (handler != null) {
            ignoreChanges = true;
            setSelectedItem(handler);
            lastSelection = handler;
            ignoreChanges = false;
        }
    }

    public void invalidateDataset() {
        DefaultComboBoxModel<String> cbm = new DefaultComboBoxModel<>();

        ArrayList<String> implementedHandlers = Lists.newArrayList(delegate.getImplementedHandlers(this));
        ArrayList<String> systemHandlers = new ArrayList<>();

        systemHandlers.addAll(delegate.getSystemMessages(this));
        Collections.sort(implementedHandlers);

        for (String thisHandler : implementedHandlers) {
            cbm.addElement(thisHandler);
            systemHandlers.remove(thisHandler);
        }

        Collections.sort(systemHandlers);

        if (systemHandlers.size() > 0 && implementedHandlers.size() > 0) {
            cbm.addElement(SEPARATOR);
        }

        for (String thisHandler : systemHandlers) {
            cbm.addElement(thisHandler);
        }

        setModel(cbm);
        setActiveHandler(lastSelection);
    }

    private boolean isImplementedBlock(String handler) {
        if (delegate != null && handler != null) {
            for (String thisHandler : delegate.getImplementedHandlers(this)) {
                if (thisHandler.equalsIgnoreCase(handler)) {
                    return true;
                }
            }
        }

        return false;
    }

    private class ItemSelectedActionDelegate implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            if (!ignoreChanges) {

                if (SEPARATOR.equals(getSelectedItem())) {
                    setSelectedIndex(0);
                } else if (delegate != null) {
                    delegate.jumpToHandler(HandlerComboBox.this, String.valueOf(getSelectedItem()));
                }
            }
        }
    }

    private class ComboBoxRenderer extends DefaultListCellRenderer {

        @Override
        public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
            super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);

            if (String.valueOf(value).equals(SEPARATOR)) {
                return new JSeparator();
            }

            if (isImplementedBlock(String.valueOf(value))) {
                setFont(getFont().deriveFont(Font.BOLD));
            }

            return this;
        }
    }

}
