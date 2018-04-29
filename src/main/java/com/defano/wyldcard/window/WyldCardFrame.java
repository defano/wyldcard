package com.defano.wyldcard.window;

import io.reactivex.Observable;
import io.reactivex.subjects.BehaviorSubject;
import io.reactivex.subjects.Subject;

import javax.swing.*;
import java.awt.event.*;

public abstract class WyldCardFrame extends JFrame implements WyldCardWindow<JFrame> {

    private final Subject<Boolean> windowVisibleProvider = BehaviorSubject.createDefault(false);
    private boolean ownsMenubar = false;

    public WyldCardFrame() {
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

        // Swing does not allow a JMenuBar to "live" on multiple windows at once; this lets us "steal" the
        // menubar each time the window comes into focus.
        this.addWindowFocusListener(new WindowAdapter() {
            @Override
            public void windowGainedFocus(WindowEvent e) {
                WyldCardFrame.this.applyMenuBar();
            }
        });

        setResizable(true);
    }

    @Override
    public JFrame getWindow() {
        return this;
    }

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
