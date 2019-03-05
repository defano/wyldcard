package com.defano.wyldcard.editor;

import com.defano.wyldcard.aspect.RunOnDispatch;

import javax.swing.*;

public class EditorStatus extends JLabel {

    private Timer spinnerTimer;

    private final static ImageIcon SPINNER_ICON = new ImageIcon(EditorStatus.class.getClassLoader().getResource("gifs/wait.gif"));
    private final static int SPINNER_DELAY_MS = 200;

    public EditorStatus() {
        super();

        spinnerTimer = new Timer(SPINNER_DELAY_MS, e -> {
            setText("");
            setIcon(SPINNER_ICON);
        });
    }

    @RunOnDispatch
    public void setStatusPending() {
        spinnerTimer.restart();
        setText("");
        setIcon(null);
    }

    @RunOnDispatch
    public void setStatusOkay() {
        spinnerTimer.stop();
        setText("");
        setIcon(null);
    }

    @RunOnDispatch
    public void setStatusError(String errorMessage) {
        spinnerTimer.stop();
        setText(errorMessage);
        setIcon(null);
    }

    @RunOnDispatch
    public boolean isShowingError() {
        return getText() != null && !getText().isEmpty();
    }
}
