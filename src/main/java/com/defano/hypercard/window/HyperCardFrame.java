package com.defano.hypercard.window;

import io.reactivex.Observable;
import io.reactivex.subjects.BehaviorSubject;
import io.reactivex.subjects.Subject;

import javax.swing.*;
import java.awt.event.*;

public abstract class HyperCardFrame extends JFrame implements HyperCardWindow<JFrame> {

    private final Subject<Boolean> windowVisibleProvider = BehaviorSubject.createDefault(false);
    private boolean ownsMenubar = false;

    public HyperCardFrame() {
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
        this.addWindowFocusListener(new WindowFocusListener() {
            @Override
            public void windowGainedFocus(WindowEvent e) {
                HyperCardFrame.this.applyMenuBar();
            }

            @Override
            public void windowLostFocus(WindowEvent e) {
            }
        });

    }

    @Override
    public JFrame getWindow() {
        return this;
    }

    public Observable<Boolean> getWindowVisibleProvider() {
        return windowVisibleProvider;
    }

    @Override
    public boolean ownsMenubar() {
        return this.ownsMenubar;
    }

    @Override
    public void setOwnsMenubar(boolean ownsMenubar) {
        this.ownsMenubar = ownsMenubar;
    }
}
