package com.defano.wyldcard.editor;

import javax.swing.*;

public class EditorStatus extends JLabel {

    private Timer spinnerTimer;
    private final static ImageIcon SPINNER_ICON = new ImageIcon(EditorStatus.class.getClassLoader().getResource("gifs/wait.gif"));

    public EditorStatus() {
        super();

        spinnerTimer = new Timer(200, e -> {
            setText("");
            setIcon(SPINNER_ICON);
        });
    }

    public void setStatusPending() {
        spinnerTimer.restart();
        setText("");
        setIcon(null);
    }

    public void setStatusOkay() {
        spinnerTimer.stop();
        setText("");
        setIcon(null);
    }

    public void setStatusError(String errorMessage) {
        spinnerTimer.stop();
        setText(errorMessage);
        setIcon(null);
    }
}
