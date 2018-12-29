package com.defano.wyldcard.window;

import com.defano.wyldcard.WyldCard;
import io.reactivex.Observable;
import io.reactivex.subjects.BehaviorSubject;
import io.reactivex.subjects.Subject;

import javax.swing.*;
import java.awt.event.*;

public abstract class WyldCardDialog<ModelType> extends JDialog implements WyldCardFrame<JDialog, ModelType> {

    private final Subject<Boolean> windowVisibleProvider = BehaviorSubject.createDefault(false);
    private final Subject<Boolean> windowFocusedProvider = BehaviorSubject.createDefault(false);

    private boolean ownsMenubar;

    public WyldCardDialog() {
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

        this.addWindowFocusListener(new WindowAdapter() {
            @Override
            public void windowGainedFocus(WindowEvent e) {
                windowFocusedProvider.onNext(true);
            }

            @Override
            public void windowLostFocus(WindowEvent e) {
                windowFocusedProvider.onNext(false);
            }
        });

        // Dispose dialog box if user presses escape
        this.getRootPane().registerKeyboardAction(e -> this.dispose(), KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_IN_FOCUSED_WINDOW);
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
    public Observable<Boolean> getWindowFocusedProvider() {
        return windowFocusedProvider;
    }

    @Override
    public boolean hasMenuBar() {
        return this.ownsMenubar;
    }

    @Override
    public void setHasMenuBar(boolean ownsMenuBar) {
        this.ownsMenubar = ownsMenuBar;
    }

    @Override
    public void setVisible(boolean isVisible) {
        super.setVisible(isVisible);
        WyldCard.getInstance().getWindowManager().notifyWindowVisibilityChanged();
    }

    @Override
    public String toString() {
        return getTitle();
    }
}
