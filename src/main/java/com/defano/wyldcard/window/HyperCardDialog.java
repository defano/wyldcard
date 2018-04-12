package com.defano.wyldcard.window;

import io.reactivex.Observable;
import io.reactivex.subjects.BehaviorSubject;
import io.reactivex.subjects.Subject;

import javax.swing.*;
import java.awt.event.*;

public abstract class HyperCardDialog extends JDialog implements HyperCardWindow<JDialog> {

    private final Subject<Boolean> windowVisibleProvider = BehaviorSubject.createDefault(false);
    private boolean ownsMenubar;

    public HyperCardDialog() {
        setAlwaysOnTop(true);

        this.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentShown(ComponentEvent e) {
                windowVisibleProvider.onNext(true);
            }

            @Override
            public void componentHidden(ComponentEvent e) {
                windowVisibleProvider.onNext(false);
            }
        });

        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent e) {
                windowVisibleProvider.onNext(false);
            }
        });

        // Dispose dialog box if user presses escape
        this.getRootPane().registerKeyboardAction(e -> {
            this.dispose();
        }, KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_IN_FOCUSED_WINDOW);
    }

    @Override
    public JDialog getWindow() {
        return this;
    }

    @Override
    public Observable<Boolean> getWindowVisibleProvider() {
        return windowVisibleProvider;
    }

    @Override
    public boolean hasMenuBar() {
        return this.ownsMenubar;
    }

    @Override
    public void setOwnsMenuBar(boolean ownsMenuBar) {
        this.ownsMenubar = ownsMenuBar;
    }
}
