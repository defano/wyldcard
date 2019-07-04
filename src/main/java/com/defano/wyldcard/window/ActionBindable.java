package com.defano.wyldcard.window;

import com.defano.wyldcard.aspect.RunOnDispatch;
import com.defano.wyldcard.awt.DocumentActionListener;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public interface ActionBindable {

    @RunOnDispatch
    default void addActionListenerToComponents(ActionListener actionListener, JComponent... components) {
        for (JComponent component : components) {

            if (component instanceof JSpinner) {
                ((JSpinner) component).addChangeListener(e -> actionListener.actionPerformed(new ActionEvent(e.getSource(), ActionEvent.ACTION_PERFORMED, "Changed")));
            }

            if (component instanceof JComboBox) {
                ((JComboBox) component).addActionListener(actionListener);
            }

            if (component instanceof JButton) {
                ((JButton) component).addActionListener(actionListener);
            }

            if (component instanceof JCheckBox) {
                ((JCheckBox) component).addActionListener(actionListener);
            }

            if (component instanceof JRadioButton) {
                ((JRadioButton) component).addActionListener(actionListener);
            }

            if (component instanceof JTextField) {
                ((JTextField) component).getDocument().addDocumentListener(new DocumentActionListener(actionListener));
            }
        }
    }
}
