package com.defano.wyldcard.window;

import com.defano.wyldcard.aspect.RunOnDispatch;
import io.reactivex.Observable;
import io.reactivex.subjects.BehaviorSubject;
import io.reactivex.subjects.Subject;

import javax.swing.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public abstract class HyperCardDialog extends JDialog implements HyperCardWindow<JDialog> {

    private final Subject<Boolean> windowVisibleProvider = BehaviorSubject.createDefault(false);
    private boolean ownsMenubar;

    public HyperCardDialog() {
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
    public boolean ownsMenubar() {
        return this.ownsMenubar;
    }

    @Override
    public void setOwnsMenubar(boolean ownsMenubar) {
        this.ownsMenubar = ownsMenubar;
    }
}
