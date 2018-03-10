package com.defano.wyldcard.awt;

import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Utility class that converts an action reported to a {@link DocumentListener} to an action reported to an
 * {@link ActionListener}. That is, this class lets changes to text fields behave like button and menu actions.
 */
public class DocumentActionListener implements DocumentListener {

    public static final String INSERT_ACTION = "Insert";
    public static final String REMOVE_ACTION = "Remove";
    public static final String CHANGED_ACTION = "Changed";

    private final ActionListener actionListener;

    public DocumentActionListener (ActionListener actionListener) {
        this.actionListener = actionListener;
    }

    @Override
    public void insertUpdate(DocumentEvent e) {
        actionListener.actionPerformed(new ActionEvent(this, ActionEvent.ACTION_PERFORMED, INSERT_ACTION));
    }

    @Override
    public void removeUpdate(DocumentEvent e) {
        actionListener.actionPerformed(new ActionEvent(this, ActionEvent.ACTION_PERFORMED, REMOVE_ACTION));
    }

    @Override
    public void changedUpdate(DocumentEvent e) {
        actionListener.actionPerformed(new ActionEvent(this, ActionEvent.ACTION_PERFORMED, CHANGED_ACTION));
    }
}
